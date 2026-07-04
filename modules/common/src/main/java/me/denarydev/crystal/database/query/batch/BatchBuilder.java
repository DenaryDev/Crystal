/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query.batch;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.query.AbstractQuery;
import me.denarydev.crystal.database.query.impl.Raw;
import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes a query with multiple sets of parameters in a single batch,
 * using {@link PreparedStatement#executeBatch()}.
 */
public final class BatchBuilder {

    private final ConnectionPool pool;

    private final List<AbstractQuery> queries = new ArrayList<>();

    private BatchBuilder(ConnectionPool pool) {
        this.pool = pool;
    }

    /**
     * Creates a new BatchBuilder instance.
     *
     * @param pool the connection pool.
     * @return a new BatchBuilder.
     */
    public static BatchBuilder of(ConnectionPool pool) {
        return new BatchBuilder(pool);
    }

    /**
     * Adds a query to the batch. If the builder is not empty, the SQL text of the query
     * must exactly match that of the first query added.
     *
     * @param query the query to add.
     * @return this object.
     */
    public BatchBuilder add(@NonNull AbstractQuery query) {
        final AbstractQuery raw = new Raw(pool, query.getSQL(), query.getParams());

        if (!this.queries.isEmpty()) {
            final String first = this.queries.getFirst().getSQL();

            if (!raw.getSQL().equals(first)) {
                throw new IllegalArgumentException("Can't add a query '" + raw.getSQL() + "' to a batch. Expected the query to be '" + first + "'");
            }
        }

        this.queries.add(raw);

        return this;
    }

    /**
     * Acquires a connection from the pool, executes all added queries as a batch,
     * and closes the connection.
     *
     * @return an array of update counts for each executed statement.
     * @throws SQLException on SQL error.
     */
    public int[] execute() throws SQLException {
        if (this.queries.isEmpty()) {
            return new int[0];
        }

        try (final Connection con = pool.connection();
             final PreparedStatement statement = con.prepareStatement(this.queries.getFirst().getSQL())
        ) {
            for (final AbstractQuery query : this.queries) {
                final List<Object> params = query.getParams();

                for (int i = 0; i < params.size(); i++) {
                    statement.setObject(i + 1, params.get(i));
                }

                statement.addBatch();
            }

            return statement.executeBatch();
        }
    }
}

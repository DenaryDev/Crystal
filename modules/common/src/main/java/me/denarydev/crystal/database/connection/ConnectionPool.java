/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.database.DatabaseType;
import me.denarydev.crystal.database.connection.file.FlatfileConnectionPool;
import me.denarydev.crystal.database.connection.hikari.HikariConnectionPool;
import me.denarydev.crystal.database.query.QueryBuilder;
import me.denarydev.crystal.database.query.batch.BatchBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public sealed abstract class ConnectionPool permits FlatfileConnectionPool, HikariConnectionPool {

    protected final Logger logger = Crystal.instance().logger();

    /**
     * Returns the database type used by this connection pool.
     *
     * @return {@link DatabaseType}
     */
    @NonNull
    public abstract DatabaseType implementationType();

    /**
     * Initializes the database connection pool.
     */
    public abstract void initialize();

    /**
     * Shuts down the database connection pool.
     */
    public abstract void shutdown();

    /**
     * Returns whether this connection pool has been initialized and is ready for use.
     *
     * @return {@code true} if the pool is initialized, {@code false} otherwise
     */
    public abstract boolean isInitialized();

    /**
     * Creates a new query builder backed by this connection pool.
     *
     * @return a {@link QueryBuilder} instance for building SQL queries.
     * @see me.denarydev.crystal.database.query.impl
     */
    public final QueryBuilder query() {
        return QueryBuilder.of(this);
    }

    /**
     * Creates a new batch query builder backed by this connection pool.
     * Allows multiple parameterized executions of the same statement to be sent in a single batch.
     *
     * @return a {@link BatchBuilder} instance for building batched SQL queries.
     */
    public final BatchBuilder batch() {
        return BatchBuilder.of(this);
    }

    /**
     * Returns the data source for this pool, or throws if it has not been initialized.
     *
     * @return the data source.
     * @throws SQLException if the data source is not initialized.
     */
    @NonNull
    public abstract DataSource dataSource() throws SQLException;

    /**
     * Acquires and returns a new connection from the pool.
     *
     * @return a new database connection.
     * @throws SQLException if a connection cannot be obtained.
     */
    @NonNull
    public final Connection connection() throws SQLException {
        if (!isInitialized()) {
            throw new SQLException("Unable to get a connection from the pool. (ConnectionPool not initialized)");
        }

        final DataSource dataSource;
        try {
            dataSource = dataSource();
        } catch (SQLException e) {
            throw new SQLException("Unable to get a connection from the pool. (dataSource not initialized)");
        }

        final Connection connection = dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    /**
     * Executes the given callback with a connection from the pool, automatically
     * closing the connection on completion.
     *
     * @param callback the callback to execute.
     */
    public abstract void connect(@NonNull final ConnectionCallback callback);

    @ApiStatus.Internal
    public abstract Function<String, String> statementProcessor();

    /**
     * A callback that wraps a connection and automatically handles SQL error interception.
     */
    public interface ConnectionCallback {
        void accept(@NonNull final Connection connection) throws SQLException;
    }

    public static abstract sealed class Builder<T extends ConnectionPool> permits FlatfileConnectionPool.Builder, HikariConnectionPool.Builder {

        /**
         * Builds a connection pool from the parameters set on this builder.
         *
         * @return the connection pool
         */
        public abstract T build();
    }
}

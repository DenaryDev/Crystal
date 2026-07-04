/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.query.impl.CreateTable;
import me.denarydev.crystal.database.query.impl.Delete;
import me.denarydev.crystal.database.query.impl.Insert;
import me.denarydev.crystal.database.query.impl.Raw;
import me.denarydev.crystal.database.query.impl.Select;
import me.denarydev.crystal.database.query.impl.Update;
import me.denarydev.crystal.database.util.SQLUtil;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

/**
 * Utility class for creating query objects.
 */
public final class QueryBuilder {

    private final ConnectionPool pool;

    private QueryBuilder(ConnectionPool pool) {
        this.pool = pool;
    }

    /**
     * Creates a query builder backed by the given connection pool.
     *
     * @param pool the connection pool.
     * @return a query builder instance.
     */
    public static QueryBuilder of(ConnectionPool pool) {
        return new QueryBuilder(pool);
    }

    /**
     * @param tableName the name of the table to create.
     * @return a <code>CREATE TABLE</code> query.
     */
    public CreateTable createTable(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new CreateTable(pool, tableName);
    }

    /**
     * @param tableName the name of the table to insert into.
     * @return an <code>INSERT</code> query.
     */
    public Insert insertInto(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Insert(pool, tableName);
    }

    /**
     * @param tableName the name of the table to update.
     * @return an <code>UPDATE</code> query.
     */
    public Update update(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Update(pool, tableName);
    }

    /**
     * @param tableName the name of the table to delete from.
     * @return a <code>DELETE</code> query.
     */
    public Delete deleteFrom(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Delete(pool, tableName);
    }

    /**
     * @param columns the column names to select; if empty, the caller is responsible
     *                for specifying the select expressions manually.
     * @return a <code>SELECT</code> query.
     */
    public Select select(@NonNull String... columns) {
        Select select = new Select(pool);

        for (String column : columns) {
            select.column(column);
        }

        return select;
    }

    /**
     * Creates a raw query from arbitrary SQL text.
     *
     * @param sql    the SQL query string.
     * @param params the parameters (substituted in place of <code>?</code> placeholders).
     * @return the query.
     */
    public AbstractQuery raw(@NonNull String sql, @NonNull Object... params) {
        SQLUtil.validatePlaceholderCount(sql, params);

        return new Raw(pool, sql, Arrays.asList(params));
    }
}

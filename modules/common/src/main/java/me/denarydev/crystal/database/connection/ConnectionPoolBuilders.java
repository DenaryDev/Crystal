/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection;

import me.denarydev.crystal.database.connection.file.H2ConnectionPool;
import me.denarydev.crystal.database.connection.file.SQLiteConnectionPool;
import me.denarydev.crystal.database.connection.hikari.MariaDBConnectionPool;
import me.denarydev.crystal.database.connection.hikari.MySqlConnectionPool;
import me.denarydev.crystal.database.connection.hikari.PostgresConnectionPool;

/**
 * Factory for connection pool builders.
 */
public final class ConnectionPoolBuilders {

    /**
     * Creates a new SQLite connection pool builder.
     *
     * @return an SQLite connection pool builder
     */
    public static SQLiteConnectionPool.Builder sqlite() {
        return new SQLiteConnectionPool.Builder();
    }

    /**
     * Creates a new H2 connection pool builder.
     *
     * @return an H2 connection pool builder
     */
    public static H2ConnectionPool.Builder h2() {
        return new H2ConnectionPool.Builder();
    }

    /**
     * Creates a new MySQL connection pool builder.
     *
     * @return a MySQL connection pool builder
     */
    public static MySqlConnectionPool.Builder mysql() {
        return new MySqlConnectionPool.Builder();
    }

    /**
     * Creates a new MariaDB connection pool builder.
     *
     * @return a MariaDB connection pool builder
     */
    public static MariaDBConnectionPool.Builder mariadb() {
        return new MariaDBConnectionPool.Builder();
    }

    /**
     * Creates a new PostgreSQL connection pool builder.
     *
     * @return a PostgreSQL connection pool builder
     */
    public static PostgresConnectionPool.Builder postgresql() {
        return new PostgresConnectionPool.Builder();
    }
}

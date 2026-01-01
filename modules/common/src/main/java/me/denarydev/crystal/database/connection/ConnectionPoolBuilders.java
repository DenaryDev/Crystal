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
 * @author DenaryDev
 * @since 3:29 18.05.2025
 */
public final class ConnectionPoolBuilders {

    /**
     * Создаёт новый билдер фабрики соединений SQLite
     */
    public static SQLiteConnectionPool.Builder sqlite() {
        return new SQLiteConnectionPool.Builder();
    }

    /**
     * Создаёт новый билдер фабрики соединений H2
     */
    public static H2ConnectionPool.Builder h2() {
        return new H2ConnectionPool.Builder();
    }

    /**
     * Создаёт новый билдер фабрики соединений MySQL
     */
    public static MySqlConnectionPool.Builder mysql() {
        return new MySqlConnectionPool.Builder();
    }

    /**
     * Создаёт новый билдер фабрики соединений MariaDB
     */
    public static MariaDBConnectionPool.Builder mariadb() {
        return new MariaDBConnectionPool.Builder();
    }

    /**
     * Создаёт новый билдер фабрики соединений PostgreSQL
     */
    public static PostgresConnectionPool.Builder postgresql() {
        return new PostgresConnectionPool.Builder();
    }
}

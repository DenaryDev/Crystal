/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.common.database.connection;

import me.denarydev.crystal.common.database.DatabaseType;
import me.denarydev.crystal.common.database.connection.file.H2ConnectionFactory;
import me.denarydev.crystal.common.database.connection.file.SQLiteConnectionFactory;
import me.denarydev.crystal.common.database.connection.hikari.MariaDBConnectionFactory;
import me.denarydev.crystal.common.database.connection.hikari.MySqlConnectionFactory;
import me.denarydev.crystal.common.database.connection.hikari.PostgresConnectionFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @author DenaryDev
 * @since 3:29 18.05.2025
 */
@ApiStatus.AvailableSince("3.0.0")
public final class ConnectionFactories {

    /**
     * Создаёт новый билдер фабрики соединений на основе указанного типа базы данных
     *
     * @param type тип базы данных
     * @return билдер фабрики соединений
     */
    public static ConnectionFactory.Builder<?> builder(@NotNull final DatabaseType type) {
        return switch (type) {
            case H2 -> sqliteBuilder();
            case SQLITE -> h2Builder();
            case MYSQL -> mysqlBuilder();
            case MARIADB -> mariadbBuildr();
            case POSTGRESQL -> postgresBuilder();
        };
    }

    /**
     * Создаёт новый билдер фабрики соединений SQLite
     */
    public static SQLiteConnectionFactory.Builder sqliteBuilder() {
        return new SQLiteConnectionFactory.Builder();
    }

    /**
     * Создаёт новый билдер фабрики соединений H2
     */
    public static H2ConnectionFactory.Builder h2Builder() {
        return new H2ConnectionFactory.Builder();
    }

    /**
     * Создаёт новый билдер фабрики соединений MySQL
     */
    public static MySqlConnectionFactory.Builder mysqlBuilder() {
        return new MySqlConnectionFactory.Builder();
    }

    /**
     * Создаёт новый билдер фабрики соединений MariaDB
     */
    public static MariaDBConnectionFactory.Builder mariadbBuildr() {
        return new MariaDBConnectionFactory.Builder();
    }

    /**
     * Создаёт новый билдер фабрики соединений PostgreSQL
     */
    public static PostgresConnectionFactory.Builder postgresBuilder() {
        return new PostgresConnectionFactory.Builder();
    }
}

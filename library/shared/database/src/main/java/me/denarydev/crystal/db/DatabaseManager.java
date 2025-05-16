/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db;

import me.denarydev.crystal.db.connection.ConnectionFactory;
import me.denarydev.crystal.db.connection.file.H2ConnectionFactory;
import me.denarydev.crystal.db.connection.file.SQLiteConnectionFactory;
import me.denarydev.crystal.db.connection.hikari.MariaDBConnectionFactory;
import me.denarydev.crystal.db.connection.hikari.MySqlConnectionFactory;
import me.denarydev.crystal.db.connection.hikari.PostgresConnectionFactory;
import me.denarydev.crystal.db.settings.ConnectionSettings;
import me.denarydev.crystal.db.settings.FlatfileConnectionSettings;
import me.denarydev.crystal.db.settings.HikariConnectionSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @author DenaryDev
 * @since 2:23 23.12.2023
 */
@ApiStatus.AvailableSince("2.1.0")
public final class DatabaseManager {
    private ConnectionSettings settings;
    private ConnectionFactory connectionFactory;

    public void initialize(@NotNull ConnectionSettings settings) throws IllegalArgumentException {
        this.settings = settings;

        final var type = settings.databaseType();
        if (type.remote()) {
            final var remoteSettings = (HikariConnectionSettings) settings;
            switch (type) {
                case MYSQL -> this.connectionFactory = new MySqlConnectionFactory(remoteSettings);
                case MARIADB -> this.connectionFactory = new MariaDBConnectionFactory(remoteSettings);
                case POSTGRESQL -> this.connectionFactory = new PostgresConnectionFactory(remoteSettings);
            }
        } else {
            final var dataFolder = ((FlatfileConnectionSettings) settings).dataFolder();
            switch (type) {
                case SQLITE -> {
                    final var file = dataFolder.resolve(settings.pluginName().toLowerCase() + "-sqlite.db");
                    this.connectionFactory = new SQLiteConnectionFactory(file);
                }
                case H2 -> {
                    final var file = dataFolder.resolve(settings.pluginName().toLowerCase() + "-h2");
                    this.connectionFactory = new H2ConnectionFactory(file);
                }
            }
        }

        connectionFactory.initialize();
    }

    public void shutdown() {
        try {
            this.connectionFactory.shutdown();
        } catch (Exception e) {
            settings.logger().error("Exception whilst disabling SQL storage", e);
        }
    }

    public ConnectionSettings settings() {
        return settings;
    }

    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }
}

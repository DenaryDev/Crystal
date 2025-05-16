/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.denarydev.crystal.db.connection.ConnectionFactory;
import me.denarydev.crystal.db.settings.HikariConnectionSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author DenaryDev
 * @since 16:48 23.11.2023
 */
@ApiStatus.Internal
@ApiStatus.AvailableSince("2.1.0")
public abstract sealed class HikariConnectionFactory implements ConnectionFactory permits DriverBasedHikariConnectionFactory {
    private final HikariConnectionSettings settings;
    private HikariDataSource hikari;

    public HikariConnectionFactory(HikariConnectionSettings settings) {
        this.settings = settings;
    }

    /**
     * Gets the default port used by the database
     *
     * @return the default port
     */
    protected abstract String defaultPort();

    /**
     * Configures the {@link HikariConfig} with the relevant database properties.
     *
     * <p>Each driver does this slightly differently...</p>
     *
     * @param config the hikari config
     * @param address the database address
     * @param port the database port
     * @param databaseName the database name
     * @param username the database username
     * @param password the database password
     */
    protected abstract void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password);

    /**
     * Allows the connection factory instance to override certain properties before they are set.
     *
     * @param properties the current properties
     */
    protected void overrideProperties(Map<String, Object> properties) {
        // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    /**
     * Sets the given connection properties onto the config.
     *
     * @param config the hikari config
     * @param properties the properties
     */
    protected void setProperties(HikariConfig config, Map<String, Object> properties) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    /**
     * Called after the Hikari pool has been initialised
     */
    protected void postInitialize() {

    }

    @Override
    public void initialize() {
        final var config = new HikariConfig();

        // set pool name so the logging output can be linked back to us
        config.setPoolName(settings.pluginName() + "-Hikari");

        // get the database port from the config
        final var port = settings.port() != null ? settings.port() : defaultPort();

        // allow the implementation to configure the HikariConfig appropriately with these values
        configureDatabase(config, settings.address(), port, settings.database(), settings.username(), settings.password());

        // get the extra connection properties from the config
        Map<String, Object> properties = new HashMap<>(settings.properties());

        // allow the implementation to override/make changes to these properties
        overrideProperties(properties);

        // set the properties
        setProperties(config, properties);

        // configure the connection pool
        config.setMaximumPoolSize(this.settings.maxPoolSize());
        config.setMinimumIdle(this.settings.minimumIdle());
        config.setMaxLifetime(this.settings.maxLifetime());
        config.setKeepaliveTime(this.settings.keepAliveTime());
        config.setConnectionTimeout(this.settings.connectionTimeout());

        // don't perform any initial connection validation - we subsequently call #getConnection
        // to setup the schema anyways
        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);

        postInitialize();
    }

    @Override
    public void shutdown() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public @NotNull Connection connection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    @Override
    public void connect(@NotNull ConnectionCallback callback) {
        try (final Connection connection = connection()) {
            callback.accept(connection);
        } catch (SQLException ex) {
            settings.logger().error("An error occured executing a SQL query", ex);
        }
    }
}

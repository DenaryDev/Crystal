/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.denarydev.crystal.database.connection.ConnectionPool;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Base class for network-based connection pools using HikariCP
 * (MySQL, MariaDB, PostgreSQL).
 */
public abstract sealed class HikariConnectionPool extends ConnectionPool permits MySqlConnectionPool, MariaDBConnectionPool, PostgresConnectionPool {

    private final String poolPrefix;

    private final String address;
    private final Integer port;
    private final String database;
    private final String username;
    private final String password;

    private final int maxPoolSize;
    private final int minimumIdle;
    private final int maxLifetime;
    private final int keepaliveTime;
    private final int connectionTimeout;

    private final Map<String, String> properties;

    private HikariDataSource dataSource;

    private volatile boolean initialized = false;

    public HikariConnectionPool(String poolPrefix, String address, Integer port, String database, String username, String password,
                                int maxPoolSize, int minimumIdle, int maxLifetime, int keepaliveTime, int connectionTimeout, Map<String, String> properties) {
        this.poolPrefix = poolPrefix;
        this.address = address;
        this.port = port == null ? defaultPort() : port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.minimumIdle = minimumIdle;
        this.maxLifetime = maxLifetime;
        this.keepaliveTime = keepaliveTime;
        this.connectionTimeout = connectionTimeout;
        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns the default port for this database type.
     *
     * @return the default port
     */
    protected abstract Integer defaultPort();

    /**
     * Returns the JDBC driver class name for this database type.
     *
     * @return the driver class name
     */
    protected abstract String driverClassName();

    /**
     * Returns the JDBC URL identifier for this database type (e.g. jdbc:&lt;id&gt;://...).
     *
     * @return the JDBC type identifier
     */
    protected abstract String driverJdbcIdentifier();

    /**
     * Allows connection pool implementations to override or modify connection properties
     * before they are applied.
     *
     * @param properties the current connection properties
     */
    protected void overrideProperties(Map<String, Object> properties) {
        // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    /**
     * Applies the given connection properties to the HikariCP configuration.
     *
     * @param config     the HikariCP configuration
     * @param properties the properties to apply
     */
    protected void setProperties(HikariConfig config, Map<String, Object> properties) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    @Override
    public void initialize() {
        final HikariConfig config = new HikariConfig();

        // set pool name so the logging output can be linked back to us
        final String poolName = this.poolPrefix != null ?
            this.poolPrefix + "-Hikari" :
            "Crystal-Hikari";
        config.setPoolName(poolName);

        // configure the HikariConfig with these values
        config.setDriverClassName(driverClassName());
        config.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", driverJdbcIdentifier(), this.address, this.port, this.database));
        config.setUsername(this.username);
        config.setPassword(this.password);

        // get the extra connection properties from the config
        Map<String, Object> properties = new HashMap<>(this.properties);

        // allow the implementation to override/make changes to these properties
        overrideProperties(properties);

        // set the properties
        setProperties(config, properties);

        // configure the connection pool
        config.setMaximumPoolSize(this.maxPoolSize);
        config.setMinimumIdle(this.minimumIdle);
        config.setMaxLifetime(this.maxLifetime);
        config.setKeepaliveTime(this.keepaliveTime);
        config.setConnectionTimeout(this.connectionTimeout);

        // don't perform any initial connection validation - we subsequently call #getConnection
        // to setup the schema anyways
        config.setInitializationFailTimeout(-1);

        // Class.forName() only runs the static initializer once, so if a previous pool already loaded
        // and then deregistered this driver, it won't be in DriverManager — re-register it explicitly.
        registerDriverIfAbsent(driverClassName());

        this.dataSource = new HikariDataSource(config);

        // Calling Class.forName("<driver class name>") is enough to call the static initializer
        // which makes our driver available in DriverManager. We don't want that, so unregister it after
        // the pool has been setup.
        deregisterDriver(driverClassName());

        initialized = true;
    }

    private static void registerDriverIfAbsent(String driverClassName) {
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            if (drivers.nextElement().getClass().getName().equals(driverClassName)) {
                return;
            }
        }

        try {
            final Driver driver = (Driver) Class.forName(driverClassName).getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driver);
        } catch (Exception ignored) {
        }
    }

    private static void deregisterDriver(String driverClassName) {
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(driverClassName)) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    @Override
    public void shutdown() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    @Override
    public @NonNull DataSource dataSource() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }

        return this.dataSource;
    }

    @Override
    public void connect(@NonNull ConnectionCallback callback) {
        try (final Connection connection = connection()) {
            callback.accept(connection);
        } catch (SQLException ex) {
            logger.error("An error occurred executing a SQL query", ex);
        }
    }

    public static abstract sealed class Builder<T extends HikariConnectionPool> extends ConnectionPool.Builder<T> permits MariaDBConnectionPool.Builder,
        MySqlConnectionPool.Builder, PostgresConnectionPool.Builder {
        protected String poolPrefix;
        protected String address;
        protected Integer port;
        protected String database;
        protected String username;
        protected String password;

        protected int maxPoolSize = 6;
        protected int minimumIdle = 6;
        protected int maxLifetime = 1800000;
        protected int keepAliveTime = 0;
        protected int connectionTimeout = 5000;

        protected final Map<String, String> properties = new HashMap<>();

        /**
         * The prefix for pool names in HikariCP.
         * <p>
         * A unique prefix helps distinguish the HikariCP log output of this plugin
         * from that of other plugins.
         * <p>
         * <u>It is recommended to use your plugin's name as the pool name prefix.</u>
         */
        public final Builder<T> poolPrefix(@NonNull final String pluginName) {
            this.poolPrefix = pluginName;

            return this;
        }

        /**
         * The IP address or hostname of the database server, without the port.
         */
        public Builder<T> address(@NonNull String address) {
            this.address = address;

            return this;
        }

        /**
         * The port to connect on.
         * <p>
         * Default: 3306 for MySQL and MariaDB, 5432 for PostgreSQL.
         */
        public Builder<T> port(@NonNull Integer port) {
            this.port = port;

            return this;
        }

        /**
         * The name of the database to use.
         */
        public Builder<T> database(@NonNull String database) {
            this.database = database;

            return this;
        }

        /**
         * The username for authentication.
         */
        public Builder<T> username(@NonNull String username) {
            this.username = username;

            return this;
        }

        /**
         * The password for authentication.
         */
        public Builder<T> password(@NonNull String password) {
            this.password = password;

            return this;
        }

        /**
         * The maximum number of simultaneous connections.
         * Should match the number of CPU cores available.
         * <p>
         * Default: 6
         */
        public Builder<T> maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;

            return this;
        }

        /**
         * The minimum number of connections that should always remain open.
         * To avoid issues, set this to the same value as maxPoolSize.
         * <p>
         * Default: 6
         */
        public Builder<T> minimumIdle(int minimumIdle) {
            this.minimumIdle = minimumIdle;

            return this;
        }

        /**
         * The maximum number of milliseconds a single connection should remain open.
         * <p>
         * Default: 1800000 (30 minutes)
         */
        public Builder<T> maxLifetime(int maxLifetime) {
            this.maxLifetime = maxLifetime;

            return this;
        }

        /**
         * The interval at which to ping the database to keep connections alive. Set to 0 to disable.
         * <p>
         * Default: 0
         */
        public Builder<T> keepAliveTime(int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;

            return this;
        }

        /**
         * The number of milliseconds to wait for a response from the database before timing out.
         * <p>
         * Default: 5000
         */
        public Builder<T> connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;

            return this;
        }

        /**
         * Whether to use SSL.
         * <p>
         * Default: true
         */
        public Builder<T> useSSL(boolean useSSL) {
            this.properties.put("useSSL", Boolean.toString(useSSL));

            return this;
        }

        /**
         * Whether to verify the server certificate.
         * <p>
         * Default: true
         */
        public Builder<T> verifyServerCertificate(boolean verifyServerCertificate) {
            this.properties.put("verifyServerCertificate", Boolean.toString(verifyServerCertificate));

            return this;
        }

        /**
         * Whether to use Unicode.
         * <p>
         * Default: true
         * <p>
         * <i>Not available for PostgreSQL</i>
         */
        public Builder<T> useUnicode(boolean useUnicode) {
            this.properties.put("useUnicode", Boolean.toString(useUnicode));

            return this;
        }

        /**
         * The character encoding to use.
         * <p>
         * Default: utf8
         * <p>
         * <i>Not available for PostgreSQL</i>
         */
        public Builder<T> characterEncoding(@NonNull String characterEncoding) {
            this.properties.put("characterEncoding", characterEncoding);

            return this;
        }

        /**
         * Sets an additional connection property.
         *
         * @param key   the property name
         * @param value the property value
         */
        public Builder<T> property(@NonNull String key, @NonNull String value) {
            this.properties.put(key, value);

            return this;
        }

        /**
         * Sets additional connection properties.
         *
         * @param properties the connection properties to apply
         */
        public Builder<T> properties(@NonNull Map<String, String> properties) {
            this.properties.putAll(properties);

            return this;
        }

        @ApiStatus.Internal
        protected void verify() {
            if (this.address == null) {
                throw new IllegalArgumentException("You must specify an address");
            }

            if (this.database == null) {
                throw new IllegalArgumentException("You must specify a database");
            }

            if (this.username == null) {
                throw new IllegalArgumentException("You must specify a username");
            }

            if (this.password == null) {
                throw new IllegalArgumentException("You must specify a password");
            }
        }
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author DenaryDev
 * @since 0:38 24.11.2023
 */
@ApiStatus.Internal
@ApiStatus.AvailableSince("2.1.0")
public abstract sealed class DriverBasedHikariConnectionFactory extends HikariConnectionFactory permits MySqlConnectionFactory, MariaDBConnectionFactory, PostgresConnectionFactory {

    protected DriverBasedHikariConnectionFactory(String poolPrefix, Logger logger, String address, String port, String database, String username, String password,
                                                 int maxPoolSize, int minimumIdle, int maxLifetime, int keepaliveTime, int connectionTimeout, Map<String, String> properties) {
        super(poolPrefix, logger, address, port, database, username, password, maxPoolSize, minimumIdle, maxLifetime, keepaliveTime, connectionTimeout, properties);
    }

    protected abstract String driverClassName();

    protected abstract String driverJdbcIdentifier();

    @Override
    protected void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password) {
        config.setDriverClassName(driverClassName());
        config.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", driverJdbcIdentifier(), address, port, databaseName));
        config.setUsername(username);
        config.setPassword(password);
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();

        // Calling Class.forName("<driver class name>") is enough to call the static initializer
        // which makes our driver available in DriverManager. We don't want that, so unregister it after
        // the pool has been setup.
        deregisterDriver(driverClassName());
    }

    private static void deregisterDriver(String driverClassName) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(driverClassName)) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }
}

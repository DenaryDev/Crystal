/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.hikari;

import me.denarydev.crystal.database.DatabaseType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.ApiStatus.AvailableSince;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Function;

/**
 * @author DenaryDev
 * @since 0:09 24.11.2023
 */
@AvailableSince("2.1.0")
public final class MariaDBConnectionFactory extends DriverBasedHikariConnectionFactory {

    public MariaDBConnectionFactory(String poolPrefix, Logger logger, String address, String port, String database, String username, String password,
                                    int maxPoolSize, int minimumIdle, int maxLifetime, int keepaliveTime, int connectionTimeout, Map<String, String> properties) {
        super(poolPrefix, logger, address, port, database, username, password, maxPoolSize, minimumIdle, maxLifetime, keepaliveTime, connectionTimeout, properties);
    }

    @Override
    public @NotNull DatabaseType implementationType() {
        return DatabaseType.MARIADB;
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }

    @Override
    protected String driverClassName() {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "mariadb";
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }

    @ApiStatus.AvailableSince("3.0.0")
    public static final class Builder extends HikariConnectionFactory.Builder<MariaDBConnectionFactory> {
        @Override
        public MariaDBConnectionFactory build() {
            verify();

            return new MariaDBConnectionFactory(this.poolPrefix, this.logger, this.address, this.port, this.database, this.username, this.password,
                this.maxPoolSize, this.minimumIdle, this.maxLifetime, this.keepAliveTime, this.connectionTimeout, this.properties);
        }
    }
}

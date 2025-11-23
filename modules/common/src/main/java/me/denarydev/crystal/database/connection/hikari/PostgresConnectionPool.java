/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.hikari;

import me.denarydev.crystal.database.DatabaseType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

/**
 * @author DenaryDev
 * @since 0:50 24.11.2023
 */
public final class PostgresConnectionPool extends DriverBasedHikariConnectionPool {

    public PostgresConnectionPool(String poolPrefix, String address, String port, String database, String username, String password,
                                  int maxPoolSize, int minimumIdle, int maxLifetime, int keepaliveTime, int connectionTimeout, Map<String, String> properties) {
        super(poolPrefix, address, port, database, username, password, maxPoolSize, minimumIdle, maxLifetime, keepaliveTime, connectionTimeout, properties);
    }

    @Override
    public @NotNull DatabaseType implementationType() {
        return DatabaseType.POSTGRESQL;
    }

    @Override
    protected String defaultPort() {
        return "5432";
    }

    @Override
    protected String driverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "postgresql";
    }

    @Override
    protected void overrideProperties(Map<String, Object> properties) {
        super.overrideProperties(properties);

        // remove the default config properties which don't exist for PostgreSQL
        properties.remove("useUnicode");
        properties.remove("characterEncoding");
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '"');
    }

    public static final class Builder extends HikariConnectionPool.Builder<PostgresConnectionPool> {
        @Override
        public PostgresConnectionPool build() {
            verify();

            return new PostgresConnectionPool(this.poolPrefix, this.address, this.port, this.database, this.username, this.password,
                this.maxPoolSize, this.minimumIdle, this.maxLifetime, this.keepAliveTime, this.connectionTimeout, this.properties);
        }
    }
}

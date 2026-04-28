/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.hikari;

import me.denarydev.crystal.database.DatabaseType;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.Function;

public final class MariaDBConnectionPool extends HikariConnectionPool {

    public MariaDBConnectionPool(String poolPrefix, String address, Integer port, String database, String username, String password,
                                 int maxPoolSize, int minimumIdle, int maxLifetime, int keepaliveTime, int connectionTimeout, Map<String, String> properties) {
        super(poolPrefix, address, port, database, username, password, maxPoolSize, minimumIdle, maxLifetime, keepaliveTime, connectionTimeout, properties);
    }

    @Override
    public @NonNull DatabaseType implementationType() {
        return DatabaseType.MARIADB;
    }

    @Override
    protected Integer defaultPort() {
        return 3306;
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

    public static final class Builder extends HikariConnectionPool.Builder<MariaDBConnectionPool> {
        @Override
        public MariaDBConnectionPool build() {
            verify();

            return new MariaDBConnectionPool(this.poolPrefix, this.address, this.port, this.database, this.username, this.password,
                this.maxPoolSize, this.minimumIdle, this.maxLifetime, this.keepAliveTime, this.connectionTimeout, this.properties);
        }
    }
}

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

/**
 * Connection pool for the MySQL database.
 */
public final class MySqlConnectionPool extends HikariConnectionPool {

    public MySqlConnectionPool(String poolPrefix, String address, Integer port, String database, String username, String password,
                               int maxPoolSize, int minimumIdle, int maxLifetime, int keepaliveTime, int connectionTimeout, Map<String, String> properties) {
        super(poolPrefix, address, port, database, username, password, maxPoolSize, minimumIdle, maxLifetime, keepaliveTime, connectionTimeout, properties);
    }

    @Override
    public @NonNull DatabaseType implementationType() {
        return DatabaseType.MYSQL;
    }

    @Override
    protected Integer defaultPort() {
        return 3306;
    }

    @Override
    protected String driverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "mysql";
    }

    @Override
    protected void overrideProperties(Map<String, Object> properties) {
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        properties.putIfAbsent("cachePrepStmts", "true");
        properties.putIfAbsent("prepStmtCacheSize", "250");
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
        properties.putIfAbsent("useServerPrepStmts", "true");
        properties.putIfAbsent("useLocalSessionState", "true");
        properties.putIfAbsent("rewriteBatchedStatements", "true");
        properties.putIfAbsent("cacheResultSetMetadata", "true");
        properties.putIfAbsent("cacheServerConfiguration", "true");
        properties.putIfAbsent("elideSetAutoCommits", "true");
        properties.putIfAbsent("maintainTimeStats", "false");
        properties.putIfAbsent("alwaysSendSetIsolation", "false");
        properties.putIfAbsent("cacheCallableStmts", "true");
        properties.putIfAbsent("serverTimezone", "UTC");

        super.overrideProperties(properties);
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }

    public static final class Builder extends HikariConnectionPool.Builder<MySqlConnectionPool> {
        @Override
        public MySqlConnectionPool build() {
            verify();

            return new MySqlConnectionPool(this.poolPrefix, this.address, this.port, this.database, this.username, this.password,
                this.maxPoolSize, this.minimumIdle, this.maxLifetime, this.keepAliveTime, this.connectionTimeout, this.properties);
        }
    }
}

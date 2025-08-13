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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Function;

/**
 * @author DenaryDev
 * @since 0:09 24.11.2023
 */
@ApiStatus.AvailableSince("2.1.0")
public final class MySqlConnectionFactory extends DriverBasedHikariConnectionFactory {

    public MySqlConnectionFactory(String poolPrefix, Logger logger, String address, String port, String database, String username, String password,
                                  int maxPoolSize, int minimumIdle, int maxLifetime, int keepaliveTime, int connectionTimeout, Map<String, String> properties) {
        super(poolPrefix, logger, address, port, database, username, password, maxPoolSize, minimumIdle, maxLifetime, keepaliveTime, connectionTimeout, properties);
    }

    @Override
    public @NotNull DatabaseType implementationType() {
        return DatabaseType.MYSQL;
    }

    @Override
    protected String defaultPort() {
        return "3306";
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

        // https://stackoverflow.com/a/54256150
        // It's not super important which timezone we pick, because we don't use time-based
        // data types in any of our schemas/queries.
        properties.putIfAbsent("serverTimezone", "UTC");

        super.overrideProperties(properties);
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }

    @ApiStatus.AvailableSince("3.0.0")
    public static final class Builder extends HikariConnectionFactory.Builder<MySqlConnectionFactory> {
        @Override
        public MySqlConnectionFactory build() {
            verify();

            return new MySqlConnectionFactory(this.poolPrefix, this.logger, this.address, this.port, this.database, this.username, this.password,
                this.maxPoolSize, this.minimumIdle, this.maxLifetime, this.keepAliveTime, this.connectionTimeout, this.properties);
        }
    }
}

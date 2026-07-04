/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.internal;

import io.sapphiremc.lib.configurate.objectmapping.ConfigSerializable;
import io.sapphiremc.lib.configurate.objectmapping.meta.Comment;
import io.sapphiremc.lib.configurate.objectmapping.meta.PostProcess;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import me.denarydev.crystal.database.DatabaseType;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
@ConfigSerializable
public final class PoolsConfig {
    public static final String HEADER = """
        +--------------------------------+
        |             Crystal            |
        |          by DenaryDev          |
        +--------------------------------+
        |- This config defines database connection pools.
        |- Any parameter that has a default value can be omitted from the config;
        |  the plugin will fall back to its default in that case.
        """;

    @Comment("If true, the plugin will connect to all databases immediately on startup instead of lazily on first access.")
    private boolean eagerConnect = false;

    @Comment("""
        Default settings applied to every pool.
        Any value not explicitly set in a pool entry is inherited from here.
        """)
    private PoolConfig defaultSettings = new PoolConfig(6, 6, 1800000, 0, 5000);

    @Comment("""
        Connection pool definitions.
        The primary pool ID is the name of its config section.
        """)
    private Map<String, PoolConfig> pools = Map.of(
        "local", new PoolConfig(Path.of("storage.db")),
        "main", new PoolConfig("localhost", 3306, "minecraft", "root", "", List.of("games"))
    );

    public boolean eagerConnect() {
        return eagerConnect;
    }

    public Map<String, PoolConfig> pools() {
        return pools;
    }

    @PostProcess
    private void validate() throws SerializationException {
        for (Map.Entry<String, PoolConfig> entry : pools.entrySet()) {
            final String name = entry.getKey();
            final PoolConfig pool = entry.getValue();

            pool.type = valueOrThrow(name, "type", pool.type, defaultSettings.type);
            if (!pool.type.remote()) {
                pool.file = valueOrThrow(name, "file", pool.file, defaultSettings.file);
                continue;
            }

            pool.address = valueOrThrow(name, "address", pool.address, defaultSettings.address);
            pool.port = valueOrFallback(pool.port, defaultSettings.port, 3306);

            pool.database = valueOrThrow(name, "database", pool.database, defaultSettings.database);
            pool.username = valueOrThrow(name, "username", pool.username, defaultSettings.username);
            pool.password = valueOrThrow(name, "password", pool.password, defaultSettings.password);

            pool.maxPoolSize = valueOrFallback(pool.maxPoolSize, defaultSettings.maxPoolSize, 6);
            pool.minimumIdle = valueOrFallback(pool.minimumIdle, defaultSettings.minimumIdle, 6);
            pool.maxLifetime = valueOrFallback(pool.maxLifetime, defaultSettings.maxLifetime, 1800000);
            pool.keepAliveTime = valueOrFallback(pool.keepAliveTime, defaultSettings.keepAliveTime, 0);
            pool.connectionTimeout = valueOrFallback(pool.connectionTimeout, defaultSettings.connectionTimeout, 5000);
            pool.properties = valueOrFallback(pool.properties, defaultSettings.properties, Map.of("useUnicode", "true", "characterEncoding", "utf8"));
        }
    }

    private <T> T valueOrThrow(@NonNull String pool, @NonNull String name, @Nullable T value, @Nullable T def) throws SerializationException {
        if (value == null) {
            if (def == null) {
                throw new SerializationException("Value '" + name + "' not found in pool '" + pool + "' and not specified in default pool settings!");
            }

            return def;
        }

        return value;
    }

    private <T> T valueOrFallback(@Nullable T value, @Nullable T def, @NonNull T fallback) {
        if (value == null) {
            if (def == null) {
                return fallback;
            }

            return def;
        }

        return value;
    }

    @ConfigSerializable
    public static final class PoolConfig {
        @Comment("Available types: H2, SQLITE, MYSQL, MARIADB, POSTGRESQL")
        private DatabaseType type;

        @Comment("""
            File where the database will be stored.
            For file-based databases, this is the ONLY required parameter.
            """)
        private Path file;

        @Comment("Database host address (without port).")
        private String address;
        @Comment("""
            Connection port.
            Defaults: 3306 for MySQL and MariaDB, 5432 for PostgreSQL.
            """)
        private Integer port;
        @Comment("Database name.")
        private String database;
        @Comment("Database username.")
        private String username;
        @Comment("Database password.")
        private String password;

        @Comment("""
            Maximum number of concurrent connections.
            Recommended value: number of CPU cores.
            Default: 6.
            """)
        private Integer maxPoolSize;
        @Comment("""
            Number of connections kept open at all times.
            To avoid issues, set this to the same value as maxPoolSize.
            Default: 6.
            """)
        private Integer minimumIdle;
        @Comment("""
            Maximum lifetime of a single connection in milliseconds.
            Default: 1800000 (30 minutes).
            """)
        private Integer maxLifetime;
        @Comment("""
            Interval for keepalive pings to the database, in milliseconds. Set to 0 to disable.
            Default: 0.
            """)
        private Integer keepAliveTime;
        @Comment("""
            Time in milliseconds to wait for a response from the database before timing out.
            Default: 5000.
            """)
        private Integer connectionTimeout;
        @Comment("Additional connection properties.")
        private Map<String, String> properties;

        @Comment("Additional IDs (aliases) by which plugins can look up this pool.")
        private List<String> aliases;

        @SuppressWarnings("unused")
        private PoolConfig() {
            // Required no-arg constructor for ObjectMapping
        }

        private PoolConfig(Path file) {
            this.type = DatabaseType.H2;
            this.file = file;
        }

        private PoolConfig(String address, Integer port, String database, String username, String password, List<String> aliases) {
            this.type = DatabaseType.MARIADB;
            this.address = address;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
            this.aliases = aliases;
        }

        private PoolConfig(int maxPoolSize, int minimumIdle, int maxLifetime, int keepAliveTime, int connectionTimeout) {
            this.type = DatabaseType.MARIADB;
            this.maxPoolSize = maxPoolSize;
            this.minimumIdle = minimumIdle;
            this.maxLifetime = maxLifetime;
            this.keepAliveTime = keepAliveTime;
            this.connectionTimeout = connectionTimeout;
        }

        public DatabaseType type() {
            return type;
        }

        public Path file() {
            return file;
        }

        public String address() {
            return address;
        }

        public Integer port() {
            return port;
        }

        public String database() {
            return database;
        }

        public String username() {
            return username;
        }

        public String password() {
            return password;
        }

        public Integer maxPoolSize() {
            return maxPoolSize;
        }

        public Integer minimumIdle() {
            return minimumIdle;
        }

        public Integer maxLifetime() {
            return maxLifetime;
        }

        public Integer keepAliveTime() {
            return keepAliveTime;
        }

        public Integer connectionTimeout() {
            return connectionTimeout;
        }

        public Map<String, String> properties() {
            return properties;
        }

        public List<String> aliases() {
            return aliases;
        }

        @PostProcess
        private void postProcess() {
            if (properties != null && properties.isEmpty()) {
                properties = null;
            }

            if (aliases != null && aliases.isEmpty()) {
                aliases = null;
            }
        }

        @Override
        public String toString() {
            if (type.remote()) {
                return "PoolConfig{" +
                    "type=" + type +
                    ", address='" + address + '\'' +
                    ", port='" + port + '\'' +
                    ", database='" + database + '\'' +
                    '}';
            } else {
                return "PoolConfig{" +
                    "type=" + type +
                    ", file='" + file + '\'' +
                    "}";
            }
        }
    }
}

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author DenaryDev
 * @since 1:40 28.10.2025
 */
@ApiStatus.Internal
@ConfigSerializable
public final class PoolsConfig {
    public static final String HEADER = """
                                        +--------------------------------+
                                        |             Crystal            |
                                        |          by DenaryDev          |
                                        +--------------------------------+
                                        |- В этом конфиге настраиваются пулы подключений к БД.
                                        |- Все параметры, у которых указано значение по умолчанию, можно удалить из
                                        |  конфига, и тогда плагин будет использовать их значения по умолчанию.
                                        """;

    @Comment("Если true, плагин сразу после старта подключится ко всем БД, не ожидая, пока кто-то их попросит.")
    private boolean eagerConnect = false;
    @Comment(
        """
        Настройки, базовые для любого пула.
        Если в пуле что-то не определено, это будет взято отсюда."""
    )
    private PoolConfig defaultSettings = new PoolConfig(6, 6, 1800000, 0, 5000);
    @Comment(
        """
        Пулы соединений.
        Основной ID пула - название секции с ним."""
    )
    private Map<String, PoolConfig> pools = Map.of(
        "local", new PoolConfig(Path.of("storage.db")),
        "main", new PoolConfig("localhost", 3306, "minecraft", "root", "", List.of("games"))
    );

    public boolean eagerConnect() {
        return eagerConnect;
    }

    public PoolConfig defaultSettings() {
        return defaultSettings;
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

    private <T> T valueOrThrow(@NotNull String pool, @NotNull String name, @Nullable T value, @Nullable T def) throws SerializationException {
        if (value == null) {
            if (def == null) {
                throw new SerializationException("Value '" + name + "' not found in pool '" + pool + "' and not specified in default pool settings!");
            }

            return def;
        }

        return value;
    }

    private <T> T valueOrFallback(@Nullable T value, @Nullable T def, @NotNull T fallback) {
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
        @Comment("Доступные типы: H2, SQLITE, MYSQL, MARIADB, POSTGRESQL")
        private DatabaseType type;

        @Comment(
            """
            Файл, в котором будет храниться база данных.
            Для локальных БД указывается ТОЛЬКО этот параметр."""
        )
        private Path file;

        @Comment("IP или адрес базы данных без порта.")
        private String address;
        @Comment(
            """
            Порт для подключения.
            По умолчанию: 3306 для MySQL и MariaDB, 5432 для PostgreSQL."""
        )
        private Integer port;
        @Comment("Название базы данных.")
        private String database;
        @Comment("Имя пользователя.")
        private String username;
        @Comment("Пароль.")
        private String password;

        @Comment(
            """
            Максимальное количество одновременных подключений.
            Должно быть столько же, сколько у вас ядер.
            По умолчанию: 6."""
        )
        private Integer maxPoolSize;
        @Comment(
            """
            Количество соединений, которые всегда должны быть открыты.
            Чтобы избежать проблем, установите для этого параметра то же значение, что и для maxPoolSize.
            По умолчанию: 6."""
        )
        private Integer minimumIdle;
        @Comment(
            """
            Количество миллисекунд, в течение которых одно соединение должно оставаться открытым.
            По умолчанию: 1800000 (30 минут)."""
        )
        private Integer maxLifetime;
        @Comment(
            """
            Установка интервала, в течение которого нужно «пинговать» базу данных. Установите 0, чтобы отключить.
            По умолчанию: 0."""
        )
        private Integer keepAliveTime;
        @Comment(
            """
            Количество секунд, в течение которых мы ждем ответа от базы данных, прежде чем истечет время ожидания.
            По умолчанию: 5000."""
        )
        private Integer connectionTimeout;
        @Comment("Дополнительные свойства соединения.")
        private Map<String, String> properties;

        @Comment("Дополнительные ID этого пула, по которым плагины могут получать его.")
        private List<String> aliases;

        private PoolConfig() {
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

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.internal;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import me.denarydev.crystal.database.DatabaseType;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author DenaryDev
 * @since 1:40 28.10.2025
 */
@ApiStatus.Internal
@Configuration
public final class PoolsConfiguration {
    public static final String HEADER = """
                                        +--------------------------------+
                                        |             Crystal            |
                                        |          by DenaryDev          |
                                        +--------------------------------+
                                        |- В этом конфиге настраиваются пулы подключений к БД.
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
        "main", new PoolConfig("localhost", "3006", "minecraft", "root", "", List.of("games"))
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

    @Configuration
    public static final class PoolConfig {
        @Comment("Доступные типы: H2, SQLITE, MYSQL, MARIADB, POSTGRESQL")
        private DatabaseType type;

        @Comment(
            """
            
            Файл, в котором будет храниться база данных.
            Для локальных БД указывается ТОЛЬКО этот параметр."""
        )
        private Path file;

        @Comment(
            """
            
            IP или адрес базы данных без порта."""
        )
        private String address;
        @Comment(
            """
            Порт для подключения.
            По умолчанию: 3306 для MySQL и MariaDB, 5432 для PostgreSQL."""
        )
        private String port;
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

        @Comment(
            """
            
            Дополнительные ID этого пула, по которым плагины могут получать его."""
        )
        private List<String> aliases;

        private PoolConfig() {
        }

        private PoolConfig(Path file) {
            this.type = DatabaseType.H2;
            this.file = file;
        }

        private PoolConfig(String address, String port, String database, String username, String password, List<String> aliases) {
            this.type = DatabaseType.MARIADB;
            this.address = address;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
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

        public String port() {
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

        public int maxPoolSize() {
            return maxPoolSize == null ? 6 : maxPoolSize;
        }

        public int minimumIdle() {
            return minimumIdle == null ? 6 : minimumIdle;
        }

        public int maxLifetime() {
            return maxLifetime == null ? 1800000 : maxLifetime;
        }

        public int keepAliveTime() {
            return keepAliveTime == null ? 0 : keepAliveTime;
        }

        public int connectionTimeout() {
            return connectionTimeout == null ? 5000 : connectionTimeout;
        }

        public Map<String, String> properties() {
            return properties == null ? Map.of("useUnicode", "true", "characterEncoding", "utf8") : properties;
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

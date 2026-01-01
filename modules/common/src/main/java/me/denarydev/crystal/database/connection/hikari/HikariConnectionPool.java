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
import org.jetbrains.annotations.NotNull;

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
 * @author DenaryDev
 * @since 16:48 23.11.2023
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

    /**
     * Возвращает порт по умолчанию для данного типа БД.
     *
     * @return порт по умолчанию
     */
    protected abstract Integer defaultPort();

    /**
     * Название класса драйвера для данного типа БД.
     *
     * @return название класса драйвера
     */
    protected abstract String driverClassName();

    /**
     * Идентификатор типа бд для JDBC-ссылки (jdbc:айди://...)
     *
     * @return идентификатор типа бд
     */
    protected abstract String driverJdbcIdentifier();

    /**
     * Позволяет экземпляру фабрики подключений переопределять определенные свойства до их установки.
     *
     * @param properties текущие свойства
     */
    protected void overrideProperties(Map<String, Object> properties) {
        // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    /**
     * Устанавливает заданные свойства соединения в конфигурацию.
     *
     * @param config     конфигурация hikari
     * @param properties свойства
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

        this.dataSource = new HikariDataSource(config);

        // Calling Class.forName("<driver class name>") is enough to call the static initializer
        // which makes our driver available in DriverManager. We don't want that, so unregister it after
        // the pool has been setup.
        deregisterDriver(driverClassName());
    }

    private static void deregisterDriver(String driverClassName) {
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(driverClassName)) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore
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
    public @NotNull DataSource dataSource() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }

        return this.dataSource;
    }

    @Override
    public void connect(@NotNull ConnectionCallback callback) {
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
         * Префикс для имён пулов в hikari.
         * <p>
         * Уникальный префикс позволит вам отличать логи hikari этого плагина от логов hikari других плагинов.
         * <p>
         * <u>Лучше всего использовать название вашего плагина в качестве префикса имени пула.
         */
        public final Builder<T> poolPrefix(@NotNull final String pluginName) {
            this.poolPrefix = pluginName;

            return this;
        }

        /**
         * IP или адрес базы данных без порта.
         */
        public Builder<T> address(@NotNull String address) {
            this.address = address;

            return this;
        }

        /**
         * Порт для подключения.
         * <p>
         * По умолчанию: 3306 для MySQL и MariaDB, 5432 для PostgreSQL
         */
        public Builder<T> port(@NotNull Integer port) {
            this.port = port;

            return this;
        }

        /**
         * Название базы данных для использования.
         */
        public Builder<T> database(@NotNull String database) {
            this.database = database;

            return this;
        }

        /**
         * Имя пользователя для аутентификации.
         */
        public Builder<T> username(@NotNull String username) {
            this.username = username;

            return this;
        }

        /**
         * Пароль для аутентификации.
         */
        public Builder<T> password(@NotNull String password) {
            this.password = password;

            return this;
        }

        /**
         * Максимальное количество одновременных подключений.
         * Должно быть столько же, сколько у вас ядер.
         * <p>
         * По умолчанию: 6
         */
        public Builder<T> maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;

            return this;
        }

        /**
         * Количество соединений, которые всегда должны быть открыты.
         * Чтобы избежать проблем, установите для этого параметра то же значение, что и для maxPoolSize.
         * <p>
         * По умолчанию: 6
         */
        public Builder<T> minimumIdle(int minimumIdle) {
            this.minimumIdle = minimumIdle;

            return this;
        }

        /**
         * Количество миллисекунд, в течение которых одно соединение должно оставаться открытым.
         * <p>
         * По умолчанию: 1800000 (30 минут)
         */
        public Builder<T> maxLifetime(int maxLifetime) {
            this.maxLifetime = maxLifetime;

            return this;
        }

        /**
         * Установка интервала, в течение которого нужно «пинговать» базу данных. Установите 0, чтобы отключить.
         * <p>
         * По умолчанию: 0
         */
        public Builder<T> keepAliveTime(int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;

            return this;
        }

        /**
         * Количество секунд, в течение которых мы ждем ответа от базы данных, прежде чем истечет время ожидания.
         * <p>
         * По умолчанию: 5000
         */
        public Builder<T> connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;

            return this;
        }

        /**
         * Использовать ли SSL.
         * <p>
         * По умолчанию: true
         */
        public Builder<T> useSSL(boolean useSSL) {
            this.properties.put("useSSL", Boolean.toString(useSSL));

            return this;
        }

        /**
         * Проверять ли сертификат сервера.
         * <p>
         * По умолчанию: true
         */
        public Builder<T> verifyServerCertificate(boolean verifyServerCertificate) {
            this.properties.put("verifyServerCertificate", Boolean.toString(verifyServerCertificate));

            return this;
        }

        /**
         * Использовать ли Unicode.
         * <p>
         * По умолчанию: true
         * <p>
         * <i>Недоступно на PostgreSQL</i>
         */
        public Builder<T> useUnicode(boolean useUnicode) {
            this.properties.put("useUnicode", Boolean.toString(useUnicode));

            return this;
        }

        /**
         * Используемая кодировка символов.
         * <p>
         * По умолчанию: utf8
         * <p>
         * <i>Недоступно на PostgreSQL</i>
         */
        public Builder<T> characterEncoding(@NotNull String characterEncoding) {
            this.properties.put("characterEncoding", characterEncoding);

            return this;
        }

        /**
         * Устанавливает дополнительное свойство соединения.
         *
         * @param key   название свойства
         * @param value значение свойства
         */
        public Builder<T> property(@NotNull String key, @NotNull String value) {
            this.properties.put(key, value);

            return this;
        }

        /**
         * Устанавливает дополнительные свойства соединения.
         *
         * @param properties свойства соединения
         */
        public Builder<T> properties(@NotNull Map<String, String> properties) {
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

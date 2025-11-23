/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.database.DatabaseType;
import me.denarydev.crystal.database.connection.file.FlatfileConnectionPool;
import me.denarydev.crystal.database.connection.hikari.HikariConnectionPool;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public sealed abstract class ConnectionPool permits FlatfileConnectionPool, HikariConnectionPool {
    protected final Logger logger = Crystal.instance().logger();

    /**
     * Возвращает тип базы данных, которая используется в этой фабрике.
     *
     * @return {@link DatabaseType}
     */
    @NotNull
    public abstract DatabaseType implementationType();

    /**
     * Инициализирует пул соединений с БД.
     */
    public abstract void initialize();

    /**
     * Закрывает пул соединений с БД.
     */
    public abstract void shutdown();

    /**
     * Возвращает пул соединений с БД или null, если он не инициализирован.
     *
     * @return {@link DataSource} или null, если нет соединения с БД.
     */
    @Nullable
    public abstract DataSource dataSource();

    /**
     * Подключается к БД и возвращает это подключение.
     *
     * @return {@link Connection}
     * @throws SQLException когда соединение не может быть получено.
     */
    @NotNull
    public final Connection connection() throws SQLException {
        if (dataSource() == null) {
            throw new SQLException("Unable to get a connection from the pool. (dataSource is null)");
        }

        final Connection connection = dataSource().getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    /**
     * Выполняет обратный вызов с переданным соединением и автоматически закрывает его по завершении.
     *
     * @param callback Обратный вызов, который будет выполнен при успешном соединении.
     */
    public abstract void connect(@NotNull final ConnectionCallback callback);

    @ApiStatus.Internal
    public abstract Function<String, String> statementProcessor();

    /**
     * Оборачивает соединение в обратный вызов, который автоматически обрабатывает перехват ошибок SQL.
     */
    public interface ConnectionCallback {
        void accept(@NotNull final Connection connection) throws SQLException;
    }

    public static abstract sealed class Builder<T extends ConnectionPool> permits FlatfileConnectionPool.Builder, HikariConnectionPool.Builder {
        protected String poolPrefix;

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
         * Создаёт фабрику соединений с параметрами из этого билдера.
         *
         * @return фабрика соединений
         */
        public abstract T build();
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection;

import me.denarydev.crystal.database.DatabaseType;
import me.denarydev.crystal.database.connection.file.FlatfileConnectionFactory;
import me.denarydev.crystal.database.connection.hikari.HikariConnectionFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

@ApiStatus.AvailableSince("2.1.0")
public sealed abstract class ConnectionFactory permits FlatfileConnectionFactory, HikariConnectionFactory {
    protected final Logger logger;

    @ApiStatus.Internal
    public ConnectionFactory(final Logger logger) {
        this.logger = logger;
    }

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
    public abstract void shutdown() throws SQLException;

    /**
     * Подключается к БД и возвращает инстанс соединения.
     *
     * @return {@link Connection}
     * @throws SQLException когда соединение не может быть получено.
     */
    @NotNull
    public abstract Connection connection() throws SQLException;

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

    @ApiStatus.AvailableSince("3.0.0")
    public static abstract sealed class Builder<T extends ConnectionFactory> permits FlatfileConnectionFactory.Builder, HikariConnectionFactory.Builder {
        protected String poolPrefix;
        protected Logger logger;

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
         * Реализация {@link org.slf4j.Logger} из вашего плагина
         */
        public final Builder<T> logger(@NotNull final Logger logger) {
            this.logger = logger;

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

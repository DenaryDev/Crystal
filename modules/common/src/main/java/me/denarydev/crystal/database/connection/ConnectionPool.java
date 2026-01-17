/*
 * Copyright (c) 2026 DenaryDev
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
import me.denarydev.crystal.database.query.QueryBuilder;
import me.denarydev.crystal.database.query.batch.BatchBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
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
     * Создает новый создатель запросов, использующий данный пул соединений.
     *
     * @return Экземпляр {@link QueryBuilder} для формирования SQL-запросов.
     * @see me.denarydev.crystal.database.query.impl
     */
    public final QueryBuilder query() {
        return QueryBuilder.of(this);
    }

    /**
     * Создает новый построитель пакетных запросов (batch), использующий данный пул соединений.
     * Позволяет эффективно выполнять однотипные запросы с разными наборами параметров за один раз.
     *
     * @return Экземпляр {@link BatchBuilder} для формирования пакета SQL-запросов.
     */
    public final BatchBuilder batch() {
        return BatchBuilder.of(this);
    }

    /**
     * Возвращает источник соединений с БД или выкидывает исключение, если он не инициализирован.
     *
     * @return Источник подключений к БД.
     * @throws SQLException если не удалось соединиться с БД.
     */
    @NotNull
    public abstract DataSource dataSource() throws SQLException;

    /**
     * Подключается к БД и возвращает это подключение.
     *
     * @return Новое подключение к БД.
     * @throws SQLException когда соединение не может быть получено.
     */
    @NotNull
    public final Connection connection() throws SQLException {
        final DataSource dataSource;
        try {
            dataSource = dataSource();
        } catch (SQLException e) {
            throw new SQLException("Unable to get a connection from the pool. (dataSource not initialized)");
        }

        final Connection connection = dataSource.getConnection();
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

        /**
         * Создаёт фабрику соединений с параметрами из этого билдера.
         *
         * @return фабрика соединений
         */
        public abstract T build();
    }
}

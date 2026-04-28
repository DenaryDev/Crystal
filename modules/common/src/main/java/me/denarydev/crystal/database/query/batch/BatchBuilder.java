/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query.batch;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.query.AbstractQuery;
import me.denarydev.crystal.database.query.impl.Raw;
import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Позволяет выполнять запрос с несколькими списками параметров в одном пакете,
 * используя метод {@link PreparedStatement#executeBatch()}.
 */
public final class BatchBuilder {

    private final ConnectionPool pool;

    private final List<AbstractQuery> queries = new ArrayList<>();

    private BatchBuilder(ConnectionPool pool) {
        this.pool = pool;
    }

    /**
     * Создает новый экземпляр BatchBuilder.
     *
     * @param pool Пул соединений.
     * @return Новый объект BatchBuilder.
     */
    public static BatchBuilder of(ConnectionPool pool) {
        return new BatchBuilder(pool);
    }

    /**
     * Добавляет запрос в пакет. Если построитель не пуст, SQL-текст запроса
     * должен в точности совпадать с первым добавленным запросом.
     *
     * @param query Добавляемый запрос.
     * @return Этот объект.
     */
    public BatchBuilder add(@NonNull AbstractQuery query) {
        final AbstractQuery raw = new Raw(pool, query.getSQL(), query.getParams());

        if (!this.queries.isEmpty()) {
            final String first = this.queries.getFirst().getSQL();

            if (!raw.getSQL().equals(first)) {
                throw new IllegalArgumentException("Can't add a query '" + raw.getSQL() + "' to a batch. Expected the query to be '" + first + "'");
            }
        }

        this.queries.add(raw);

        return this;
    }

    /**
     * Получает соединение из указанного при создании пула, выполняет все добавленные запросы
     * в виде пакета и закрывает соединение.
     *
     * @return Массив счетчиков обновлений для каждой выполненной инструкции.
     * @throws SQLException При ошибке SQL.
     */
    public int[] execute() throws SQLException {
        if (this.queries.isEmpty()) {
            return new int[0];
        }

        try (final Connection con = pool.connection();
             final PreparedStatement statement = con.prepareStatement(this.queries.getFirst().getSQL())
        ) {
            for (final AbstractQuery query : this.queries) {
                final List<Object> params = query.getParams();

                for (int i = 0; i < params.size(); i++) {
                    statement.setObject(i + 1, params.get(i));
                }

                statement.addBatch();
            }

            return statement.executeBatch();
        }
    }
}

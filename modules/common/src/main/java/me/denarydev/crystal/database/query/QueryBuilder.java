/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.query.impl.CreateTable;
import me.denarydev.crystal.database.query.impl.Delete;
import me.denarydev.crystal.database.query.impl.Insert;
import me.denarydev.crystal.database.query.impl.Raw;
import me.denarydev.crystal.database.query.impl.Select;
import me.denarydev.crystal.database.query.impl.Update;
import me.denarydev.crystal.database.util.SQLUtil;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

/**
 * Утилитный класс для создания объектов запросов.
 */
public final class QueryBuilder {

    private final ConnectionPool pool;

    private QueryBuilder(ConnectionPool pool) {
        this.pool = pool;
    }

    /**
     * Создаёт экземпляр создателя запросов на основе указанного пула соединений.
     *
     * @param pool Пул соединений.
     * @return Экземпляр создателя запросов.
     */
    public static QueryBuilder of(ConnectionPool pool) {
        return new QueryBuilder(pool);
    }

    /**
     * @param tableName Название таблицы, которая будет создана.
     * @return Запрос <code>CREATE TABLE</code>.
     */
    public CreateTable createTable(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new CreateTable(pool, tableName);
    }

    /**
     * @param tableName Название таблицы, в которую будет добавлена запись.
     * @return Запрос <code>INSERT</code>.
     */
    public Insert insertInto(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Insert(pool, tableName);
    }

    /**
     * @param tableName Название таблицы, которая будет обновлена.
     * @return Запрос <code>UPDATE</code>.
     */
    public Update update(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Update(pool, tableName);
    }

    /**
     * @param tableName Название таблицы, из которой будут удалены записи.
     * @return Запрос <code>DELETE</code>.
     */
    public Delete deleteFrom(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Delete(pool, tableName);
    }

    /**
     * @param columns Названия столбцов для выбора. Если пустой, вызывающий должен
     *                самостоятельно указать выбираемые столбцы или выражения.
     * @return Запрос <code>SELECT</code>.
     */
    public Select select(@NonNull String... columns) {
        Select select = new Select(pool);

        for (String column : columns) {
            select.column(column);
        }

        return select;
    }

    /**
     * Создает "сырой" запрос из произвольного текста SQL.
     *
     * @param sql    Текст SQL-запроса.
     * @param params Параметры (для заполнения плейсхолдеров <code>?</code>).
     * @return Запрос.
     */
    public AbstractQuery raw(@NonNull String sql, @NonNull Object... params) {
        SQLUtil.validatePlaceholderCount(sql, params);

        return new Raw(pool, sql, Arrays.asList(params));
    }
}

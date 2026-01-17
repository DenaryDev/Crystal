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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Утилитный класс для создания объектов запросов.
 *
 * @author DenaryDev
 * @since 2:17 17.01.2026
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
    public CreateTable createTable(@NotNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new CreateTable(pool, tableName);
    }

    /**
     * @param tableName Название таблицы, в которую будет добавлена запись.
     * @return Запрос <code>INSERT</code>.
     */
    public Insert insertInto(@NotNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Insert(pool, tableName);
    }

    /**
     * @param tableName Название таблицы, которая будет обновлена.
     * @return Запрос <code>UPDATE</code>.
     */
    public Update update(@NotNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Update(pool, tableName);
    }

    /**
     * @param tableName Название таблицы, из которой будут удалены записи.
     * @return Запрос <code>DELETE</code>.
     */
    public Delete deleteFrom(@NotNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        return new Delete(pool, tableName);
    }

    /**
     * @param columns Названия столбцов для выбора. Если пустой, вызывающий должен
     *                самостоятельно указать выбираемые столбцы или выражения.
     * @return Запрос <code>SELECT</code>.
     */
    public Select select(@NotNull String... columns) {
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
    public AbstractQuery raw(@NotNull String sql, @NotNull Object... params) {
        SQLUtil.validatePlaceholderCount(sql, params);

        return new Raw(pool, sql, Arrays.asList(params));
    }
}

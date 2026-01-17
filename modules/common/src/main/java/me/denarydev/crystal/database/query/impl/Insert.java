/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query.impl;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.query.AbstractQuery;
import me.denarydev.crystal.database.query.Expression;
import me.denarydev.crystal.database.util.SQLUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Запрос <code>INSERT</code>.
 *
 * @author DenaryDev
 * @since 2:17 17.01.2026
 */
public final class Insert extends AbstractQuery {

    private final String table;

    private boolean ignore;

    private final Map<String, Expression> insert = new LinkedHashMap<>();
    private final Map<String, Expression> update = new LinkedHashMap<>();

    @ApiStatus.Internal
    public Insert(ConnectionPool pool, String table) {
        super(pool);
        this.table = table;
    }

    /**
     * Добавляет предложение <code>IGNORE</code> в запрос.
     *
     * @return Этот объект.
     */
    public Insert ignore() {
        this.ignore = true;
        return this;
    }

    /**
     * Добавляет значение для вставки в указанный столбец.
     *
     * @param column Имя столбца.
     * @param value  Значение столбца.
     * @return Этот объект.
     */
    public Insert value(@NotNull String column, @NotNull Object value) {
        SQLUtil.validateIdentifier(column);
        return valueExpr(column, "?", value);
    }

    /**
     * Добавляет значение в виде SQL-выражения для вставки в указанный столбец.
     *
     * @param column Имя столбца.
     * @param expr   SQL-выражение.
     * @param params Параметры выражения (для заполнения плейсхолдеров <code>?</code>).
     * @return Этот объект.
     */
    public Insert valueExpr(@NotNull String column, @NotNull String expr, @NotNull Object... params) {
        SQLUtil.validateIdentifier(column);
        SQLUtil.validatePlaceholderCount(expr, params);
        this.insert.put(column, new Expression(expr, params));
        return this;
    }

    /**
     * Добавляет значение <code>NULL</code> для вставки в указанный столбец.
     *
     * @param column Имя столбца.
     * @return Этот объект.
     */
    public Insert valueNull(@NotNull String column) {
        return valueExpr(column, "NULL");
    }

    /**
     * Если указанное значение равно null, добавляет значение <code>NULL</code> для указанного столбца,
     * в противном случае добавляет указанное значение как обычное значение столбца.
     *
     * @param column Имя столбца.
     * @param value  Допускающее null значение.
     * @return Этот объект.
     */
    public Insert valueNullable(@NotNull String column, @Nullable Object value) {
        return value == null ? valueNull(column) : value(column, value);
    }

    /**
     * Добавляет предложение <code>ON DUPLICATE KEY UPDATE</code> со всеми значениями, добавленными через
     * методы <code>value*()</code>, исключая столбцы, помеченные как ключи.
     *
     * @param keys Ключевые столбцы, которые не должны обновляться.
     * @return Этот объект.
     */
    public Insert onDuplicateKeyUpdateExcept(@NotNull String... keys) {
        this.update.clear();

        Set<String> keySet = new HashSet<>();
        Collections.addAll(keySet, keys);
        this.insert.forEach((k, v) -> {
            if (!keySet.contains(k)) {
                this.update.put(k, v);
            }
        });

        return this;
    }

    @Override
    public String getSQL() {
        if (this.insert.isEmpty()) {
            throw new IllegalStateException("No values specified");
        }

        if (this.ignore && !this.update.isEmpty()) {
            throw new IllegalStateException("Can't use INGORE with ON DUPLICATE KEY UPDATE");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("INSERT");

        if (this.ignore) {
            builder.append(" IGNORE");
        }

        builder.append(" INTO ");

        builder.append("`").append(this.table).append("` (");

        this.insert.forEach((k, v) -> builder.append("`").append(k).append("`, "));

        builder.setLength(builder.length() - 2);

        builder.append(") VALUES (");

        this.insert.forEach((k, v) -> builder.append(v.expr()).append(", "));

        builder.setLength(builder.length() - 2);

        builder.append(")");

        if (!this.update.isEmpty()) {
            builder.append(" ON DUPLICATE KEY UPDATE ");

            this.update.forEach((k, v) -> builder.append("`").append(k).append("` = ").append(v.expr()).append(", "));

            builder.setLength(builder.length() - 2);
        }

        builder.append(";");

        return builder.toString();
    }

    @Override
    public List<Object> getParams() {
        List<Object> params = new ArrayList<>();

        for (Expression expression : this.insert.values()) {
            Collections.addAll(params, expression.params());
        }

        for (Expression expression : this.update.values()) {
            Collections.addAll(params, expression.params());
        }

        return params;
    }
}

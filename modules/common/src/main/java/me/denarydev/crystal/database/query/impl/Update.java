/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query.impl;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.query.ConditionalQuery;
import me.denarydev.crystal.database.query.Expression;
import me.denarydev.crystal.database.util.SQLUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Запрос <code>UPDATE</code>.
 */
public final class Update extends ConditionalQuery<Update> {

    private final String table;

    private final Map<String, Expression> expressions = new LinkedHashMap<>();

    @ApiStatus.Internal
    public Update(ConnectionPool pool, String table) {
        super(pool);
        this.table = table;
    }

    /**
     * Добавляет значение, которое будет установлено для указанного столбца.
     *
     * @param column Имя столбца.
     * @param value  Значение.
     * @return Этот объект.
     */
    public Update value(@NonNull String column, @NonNull Object value) {
        SQLUtil.validateIdentifier(column);
        return valueExpr(column, "?", value);
    }

    /**
     * Добавляет значение в виде SQL-выражения, которое будет установлено для указанного столбца.
     *
     * @param column Имя столбца.
     * @param expr   SQL-выражение.
     * @param params Параметры выражения (для заполнения плейсхолдеров <code>?</code>).
     * @return Этот объект.
     */
    public Update valueExpr(@NonNull String column, @NonNull String expr, @NonNull Object... params) {
        SQLUtil.validateIdentifier(column);
        SQLUtil.validatePlaceholderCount(expr, params);
        this.expressions.put(column, new Expression(expr, params));

        return this;
    }

    /**
     * Добавляет значение <code>NULL</code> для указанного столбца.
     *
     * @param column Имя столбца.
     * @return Этот объект.
     */
    public Update valueNull(@NonNull String column) {
        return valueExpr(column, "NULL");
    }

    /**
     * Если указанное значение равно null, добавляет значение <code>NULL</code> для указанного столбца,
     * в противном случае устанавливает указанное значение.
     *
     * @param column Имя столбца.
     * @param value  Допускающее null значение.
     * @return Этот объект.
     */
    public Update valueNullable(@NonNull String column, @Nullable Object value) {
        return value == null ? valueNull(column) : value(column, value);
    }

    @Override
    public String getSQL() {
        if (this.expressions.isEmpty()) {
            throw new IllegalStateException("No values specified");
        }

        final StringBuilder builder = new StringBuilder();

        builder.append("UPDATE ");

        builder.append("`").append(this.table).append("` SET ");

        this.expressions.forEach((k, v) -> builder.append("`").append(k).append("` = ").append(v.expr()).append(", "));

        builder.setLength(builder.length() - 2);
        builder.append(" ");

        appendConditions(builder);

        builder.append(";");

        return builder.toString();
    }

    @Override
    public List<Object> getParams() {
        final List<Object> params = new ArrayList<>();

        for (final Expression expression : this.expressions.values()) {
            Collections.addAll(params, expression.params());
        }

        for (final Expression condition : this.conditions) {
            Collections.addAll(params, condition.params());
        }

        return params;
    }
}

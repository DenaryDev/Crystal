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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Запрос <code>SELECT</code>.
 *
 * @author DenaryDev
 * @since 2:17 17.01.2026
 */
public final class Select extends ConditionalQuery<Select> {

    private final List<Expression> expressions = new ArrayList<>();
    private boolean all;

    private String database;
    private String table;

    private String orderBy;
    private Object[] orderByParams;
    private boolean desc;

    private Long limit;
    private Long offset;

    private boolean forUpdate;

    @ApiStatus.Internal
    public Select(ConnectionPool pool) {
        super(pool);
    }

    /**
     * Добавляет <code>*</code> в список выбираемых выражений. Другие столбцы не могут быть добавлены.
     *
     * @return Этот объект.
     */
    public Select all() {
        if (!this.expressions.isEmpty()) {
            throw new IllegalStateException("Some columns were added");
        }

        this.all = true;

        return this;
    }

    /**
     * Добавляет столбец в список выбираемых выражений.
     *
     * @param name Имя столбца.
     * @return Этот объект.
     */
    public Select column(@NotNull String name) {
        SQLUtil.validateIdentifier(name);
        return expression("`" + name + "`");
    }

    /**
     * Добавляет SQL-выражение в список выбираемых выражений.
     *
     * @param expr   SQL-выражение.
     * @param params Параметры выражения (для заполнения плейсхолдеров <code>?</code>).
     * @return Этот объект.
     */
    public Select expression(@NotNull String expr, @NotNull Object... params) {
        SQLUtil.validatePlaceholderCount(expr, params);

        if (this.all) {
            throw new IllegalStateException("Can't add expressions to SELECT when selecting all columns");
        }

        this.expressions.add(new Expression(expr, params));

        return this;
    }

    /**
     * Устанавливает имя таблицы, из которой будет производиться выборка.
     *
     * @param tableName Имя таблицы.
     * @return Этот объект.
     */
    public Select from(@NotNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        this.table = tableName;
        return this;
    }

    /**
     * Устанавливает имена базы данных и таблицы, из которых будет производиться выборка.
     *
     * @param database  Имя базы данных.
     * @param tableName Имя таблицы.
     * @return Этот объект.
     */
    public Select from(@NotNull String database, @NotNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        this.database = database;
        this.table = tableName;
        return this;
    }

    /**
     * Добавляет предложение <code>ORDER BY столбец</code> в запрос.
     *
     * @param column Имя столбца.
     * @return Этот объект.
     */
    public Select orderBy(@NotNull String column) {
        SQLUtil.validateIdentifier(column);
        return orderByExpr("`" + column + "`");
    }

    /**
     * Добавляет предложение <code>ORDER BY выражение</code> в запрос.
     *
     * @param expr   SQL-выражение.
     * @param params Параметры выражения (для заполнения плейсхолдеров <code>?</code>).
     * @return Этот объект.
     */
    public Select orderByExpr(@NotNull String expr, @NotNull Object... params) {
        SQLUtil.validatePlaceholderCount(expr, params);
        this.orderBy = expr;
        this.orderByParams = params;
        return this;
    }

    /**
     * Добавляет ключевое слово <code>DESC</code> в предложение <code>ORDER BY</code>.
     *
     * @return Этот объект.
     */
    public Select desc() {
        if (this.orderBy == null) {
            throw new IllegalStateException("Specify order expression first");
        }

        this.desc = true;

        return this;
    }

    /**
     * Добавляет предложение <code>LIMIT limit</code> в запрос.
     *
     * @param limit Лимит, должен быть положительным числом.
     * @return Этот объект.
     */
    public Select limit(long limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("" + limit);
        }

        this.limit = limit;

        return this;
    }

    /**
     * Добавляет предложение <code>OFFSET offset</code> в запрос.
     *
     * @param offset Смещение, должно быть неотрицательным числом.
     * @return Этот объект.
     */
    public Select offset(long offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("" + offset);
        }

        this.offset = offset;

        return this;
    }

    /**
     * Добавляет предложение <code>FOR UPDATE</code> в конец запроса.
     *
     * @return Этот объект.
     */
    public Select forUpdate() {
        this.forUpdate = true;

        return this;
    }

    @Override
    public String getSQL() {
        if (!this.all && this.expressions.isEmpty()) {
            throw new IllegalStateException("Selected expression list is empty");
        }

        final StringBuilder builder = new StringBuilder();

        builder.append("SELECT ");

        if (this.all) {
            builder.append("* ");
        } else {
            for (final Expression expression : this.expressions) {
                builder.append(expression.expr()).append(", ");
            }

            builder.setLength(builder.length() - 2);
            builder.append(" ");
        }

        if (this.table != null) {
            builder.append("FROM ");

            if (this.database != null) {
                builder.append("`").append(this.database).append("`.");
            }

            builder.append("`").append(this.table).append("` ");
        }

        appendConditions(builder);

        if (this.orderBy != null) {
            builder.append("ORDER BY (").append(this.orderBy).append(") ").append(this.desc ? "DESC" : "ASC").append(' ');
        }

        if (this.limit != null) {
            builder.append("LIMIT ").append(this.limit).append(' ');
        }

        if (this.offset != null) {
            builder.append("OFFSET ").append(this.offset).append(' ');
        }

        if (this.forUpdate) {
            builder.append("FOR UPDATE");
        }

        builder.append(";");

        return builder.toString();
    }

    @Override
    public List<Object> getParams() {
        final List<Object> params = new ArrayList<>();

        for (final Expression expression : this.expressions) {
            Collections.addAll(params, expression.params());
        }

        for (final Expression condition : this.conditions) {
            Collections.addAll(params, condition.params());
        }

        if (this.orderByParams != null) {
            Collections.addAll(params, this.orderByParams);
        }

        return params;
    }
}

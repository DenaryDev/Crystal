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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>SELECT</code> query.
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
     * Adds <code>*</code> to the list of selected expressions. No other columns may be added after this.
     *
     * @return this object.
     */
    public Select all() {
        if (!this.expressions.isEmpty()) {
            throw new IllegalStateException("Some columns were added");
        }

        this.all = true;

        return this;
    }

    /**
     * Adds a column to the list of selected expressions.
     *
     * @param name the column name.
     * @return this object.
     */
    public Select column(@NonNull String name) {
        SQLUtil.validateIdentifier(name);
        return expression("`" + name + "`");
    }

    /**
     * Adds a SQL expression to the list of selected expressions.
     *
     * @param expr   the SQL expression.
     * @param params the expression parameters (substituted in place of <code>?</code> placeholders).
     * @return this object.
     */
    public Select expression(@NonNull String expr, @NonNull Object... params) {
        SQLUtil.validatePlaceholderCount(expr, params);

        if (this.all) {
            throw new IllegalStateException("Can't add expressions to SELECT when selecting all columns");
        }

        this.expressions.add(new Expression(expr, params));

        return this;
    }

    /**
     * Sets the name of the table to select from.
     *
     * @param tableName the table name.
     * @return this object.
     */
    public Select from(@NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        this.table = tableName;
        return this;
    }

    /**
     * Sets the database and table name to select from.
     *
     * @param database  the database name.
     * @param tableName the table name.
     * @return this object.
     */
    public Select from(@NonNull String database, @NonNull String tableName) {
        SQLUtil.validateIdentifier(tableName);
        this.database = database;
        this.table = tableName;
        return this;
    }

    /**
     * Adds an <code>ORDER BY column</code> clause to the query.
     *
     * @param column the column name.
     * @return this object.
     */
    public Select orderBy(@NonNull String column) {
        SQLUtil.validateIdentifier(column);
        return orderByExpr("`" + column + "`");
    }

    /**
     * Adds an <code>ORDER BY expression</code> clause to the query.
     *
     * @param expr   the SQL expression.
     * @param params the expression parameters (substituted in place of <code>?</code> placeholders).
     * @return this object.
     */
    public Select orderByExpr(@NonNull String expr, @NonNull Object... params) {
        SQLUtil.validatePlaceholderCount(expr, params);
        this.orderBy = expr;
        this.orderByParams = params;
        return this;
    }

    /**
     * Adds the <code>DESC</code> keyword to the <code>ORDER BY</code> clause.
     *
     * @return this object.
     */
    public Select desc() {
        if (this.orderBy == null) {
            throw new IllegalStateException("Specify order expression first");
        }

        this.desc = true;

        return this;
    }

    /**
     * Adds a <code>LIMIT</code> clause to the query.
     *
     * @param limit the row limit; must be a positive number.
     * @return this object.
     */
    public Select limit(long limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("" + limit);
        }

        this.limit = limit;

        return this;
    }

    /**
     * Adds an <code>OFFSET</code> clause to the query.
     *
     * @param offset the row offset; must be a non-negative number.
     * @return this object.
     */
    public Select offset(long offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("" + offset);
        }

        this.offset = offset;

        return this;
    }

    /**
     * Adds a <code>FOR UPDATE</code> clause at the end of the query.
     *
     * @return this object.
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

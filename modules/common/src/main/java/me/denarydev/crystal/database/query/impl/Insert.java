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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An <code>INSERT</code> query.
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
     * Adds the <code>IGNORE</code> clause to the query.
     *
     * @return this object.
     */
    public Insert ignore() {
        this.ignore = true;
        return this;
    }

    /**
     * Adds a value to insert into the given column.
     *
     * @param column the column name.
     * @param value  the column value.
     * @return this object.
     */
    public Insert value(@NonNull String column, @NonNull Object value) {
        SQLUtil.validateIdentifier(column);
        return valueExpr(column, "?", value);
    }

    /**
     * Adds a SQL expression as the value to insert into the given column.
     *
     * @param column the column name.
     * @param expr   the SQL expression.
     * @param params the expression parameters (substituted in place of <code>?</code> placeholders).
     * @return this object.
     */
    public Insert valueExpr(@NonNull String column, @NonNull String expr, @NonNull Object... params) {
        SQLUtil.validateIdentifier(column);
        SQLUtil.validatePlaceholderCount(expr, params);
        this.insert.put(column, new Expression(expr, params));
        return this;
    }

    /**
     * Inserts a <code>NULL</code> value into the given column.
     *
     * @param column the column name.
     * @return this object.
     */
    public Insert valueNull(@NonNull String column) {
        return valueExpr(column, "NULL");
    }

    /**
     * Inserts either <code>NULL</code> or the given value into the given column,
     * depending on whether the value is null.
     *
     * @param column the column name.
     * @param value  the nullable value.
     * @return this object.
     */
    public Insert valueNullable(@NonNull String column, @Nullable Object value) {
        return value == null ? valueNull(column) : value(column, value);
    }

    /**
     * Adds an <code>ON DUPLICATE KEY UPDATE</code> clause for all values added via the
     * <code>value*()</code> methods, excluding the columns designated as keys.
     *
     * @param keys the key columns that should not be updated.
     * @return this object.
     */
    public Insert onDuplicateKeyUpdateExcept(@NonNull String... keys) {
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

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.util.SQLUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SQL query that supports a <code>WHERE</code> clause.
 */
public abstract class ConditionalQuery<T extends ConditionalQuery<T>> extends AbstractQuery {

    protected final List<Expression> conditions = new ArrayList<>();

    @ApiStatus.Internal
    protected ConditionalQuery(ConnectionPool pool) {
        super(pool);
    }

    /**
     * Adds a <code>WHERE</code> condition that checks whether the given column equals the given value.
     * Conditions are combined with <code>AND</code>.
     *
     * @param column the column name.
     * @param value  the value.
     * @return this object.
     */
    public final T where(@NonNull String column, @NonNull Object value) {
        SQLUtil.validateIdentifier(column);
        return whereExpr("`" + column + "` = ?", value);
    }

    /**
     * Adds an arbitrary SQL expression as a <code>WHERE</code> condition, for example <code>`mycolumn` / 100 = ?</code>.
     * Conditions are combined with <code>AND</code>.
     *
     * @param expr   the SQL expression.
     * @param params the expression parameters (substituted in place of <code>?</code> placeholders).
     * @return this object.
     */
    @SuppressWarnings("unchecked")
    public final T whereExpr(@NonNull String expr, @NonNull Object... params) {
        SQLUtil.validatePlaceholderCount(expr, params);
        this.conditions.add(new Expression(expr, params));
        return (T) this;
    }

    /**
     * Adds a <code>WHERE</code> condition that checks whether the given column is <code>NULL</code>.
     * Conditions are combined with <code>AND</code>.
     *
     * @param column the column name.
     * @return this object.
     */
    public final T whereNull(@NonNull String column) {
        return whereExpr("`" + column + "` IS NULL");
    }

    /**
     * Adds a <code>WHERE</code> condition that checks the given column against a possibly null value.
     * If the value is null, an <code>IS NULL</code> check is added; otherwise an equality check is used.
     * Conditions are combined with <code>AND</code>.
     *
     * @param column the column name.
     * @param value  the value to check against, may be null.
     * @return this object.
     */
    public final T whereNullable(@NonNull String column, @Nullable Object value) {
        return value == null ? whereNull(column) : where(column, value);
    }

    protected void appendConditions(@NonNull StringBuilder builder) {
        if (this.conditions.isEmpty()) {
            return;
        }

        builder.append("WHERE ");

        for (final Expression condition : this.conditions) {
            builder.append("(").append(condition.expr()).append(") AND ");
        }

        builder.setLength(builder.length() - 5);
        builder.append(" ");
    }
}

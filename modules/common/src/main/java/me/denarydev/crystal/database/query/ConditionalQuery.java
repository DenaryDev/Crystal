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
 * Представляет SQL-запрос с предложением <code>WHERE</code>.
 */
public abstract class ConditionalQuery<T extends ConditionalQuery<T>> extends AbstractQuery {

    protected final List<Expression> conditions = new ArrayList<>();

    @ApiStatus.Internal
    protected ConditionalQuery(ConnectionPool pool) {
        super(pool);
    }

    /**
     * Добавляет условие <code>WHERE</code>, которое проверяет, что указанный столбец имеет указанное значение.
     * Условия объединяются операцией <code>AND</code>.
     *
     * @param column Имя столбца.
     * @param value  Значение.
     * @return Этот объект.
     */
    public final T where(@NonNull String column, @NonNull Object value) {
        SQLUtil.validateIdentifier(column);
        return whereExpr("`" + column + "` = ?", value);
    }

    /**
     * Добавляет SQL-выражение в качестве условия <code>WHERE</code>, например <code>`mycolumn` / 100 = ?</code>.
     * Условия объединяются операцией <code>AND</code>.
     *
     * @param expr   SQL-выражение.
     * @param params Параметры выражения (заполняются вместо плейсхолдеров <code>?</code>).
     * @return Этот объект.
     */
    @SuppressWarnings("unchecked")
    public final T whereExpr(@NonNull String expr, @NonNull Object... params) {
        SQLUtil.validatePlaceholderCount(expr, params);
        this.conditions.add(new Expression(expr, params));
        return (T) this;
    }

    /**
     * Добавляет условие <code>WHERE</code>, которое проверяет, имеет ли указанный столбец значение <code>NULL</code>.
     * Условия объединяются операцией <code>AND</code>.
     *
     * @param column Имя столбца.
     * @return Этот объект.
     */
    public final T whereNull(@NonNull String column) {
        return whereExpr("`" + column + "` IS NULL");
    }

    /**
     * Добавляет условие <code>WHERE</code>, которое проверяет, имеет ли указанный столбец указанное значение,
     * которое может быть <code>NULL</code>.
     * Условия объединяются операцией <code>AND</code>.
     *
     * @param column Имя столбца.
     * @param value  Добавляемое значение.
     * @return Этот объект.
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

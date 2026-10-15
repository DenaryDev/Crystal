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
import me.denarydev.crystal.database.query.Dialect;
import me.denarydev.crystal.database.query.Expression;
import me.denarydev.crystal.database.util.SQLUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An <code>INSERT</code> query.
 * <p>
 * {@link #ignore()} and the <code>onDuplicateKeyUpdate</code> family are rendered in the syntax of the
 * pool's {@link Dialect}:
 * <ul>
 *     <li>MySQL / MariaDB: <code>INSERT IGNORE</code> and <code>ON DUPLICATE KEY UPDATE</code>;</li>
 *     <li>SQLite: <code>INSERT OR IGNORE</code> and <code>ON CONFLICT DO UPDATE SET</code>;</li>
 *     <li>PostgreSQL: <code>ON CONFLICT DO NOTHING</code> and <code>ON CONFLICT (...) DO UPDATE SET</code>;</li>
 *     <li>H2: <code>MERGE INTO ... USING</code> with <code>WHEN [NOT] MATCHED</code> branches.</li>
 * </ul>
 * PostgreSQL requires the conflicting columns for an update, and H2 requires them for both; declare them
 * with {@link #keys(String...)} or via {@link #onDuplicateKeyUpdateExcept(String...)}. Other dialects
 * ignore the keys where their syntax does not need them.
 */
public final class Insert extends AbstractQuery {

    private static final String SOURCE_ALIAS = "src";

    private final String table;

    private boolean ignore;

    private final Set<String> keys = new LinkedHashSet<>();
    private final Map<String, Expression> insert = new LinkedHashMap<>();
    private final Map<String, Expression> update = new LinkedHashMap<>();

    @ApiStatus.Internal
    public Insert(ConnectionPool pool, String table) {
        super(pool);
        this.table = table;
    }

    /**
     * Makes the query silently skip rows that would violate a unique or primary key constraint.
     * <p>
     * Cannot be combined with <code>onDuplicateKeyUpdate</code>.
     *
     * @return this object.
     */
    public Insert ignore() {
        this.ignore = true;
        return this;
    }

    /**
     * Declares the columns whose unique or primary key constraint detects a duplicate row.
     * <p>
     * Required by PostgreSQL for an update and by H2 for both {@link #ignore()} and an update;
     * on H2 each key must also be one of the inserted columns. MySQL and MariaDB do not use them.
     * Replaces any keys declared earlier.
     *
     * @param columns the key column names.
     * @return this object.
     */
    public Insert keys(@NonNull String... columns) {
        this.keys.clear();

        for (String column : columns) {
            SQLUtil.validateIdentifier(column);
            this.keys.add(column);
        }

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
     * Adds a <code>NULL</code> value to insert into the given column.
     *
     * @param column the column name.
     * @return this object.
     */
    public Insert valueNull(@NonNull String column) {
        return valueExpr(column, "NULL");
    }

    /**
     * Adds a value to insert into the given column, or <code>NULL</code> if the value is <code>null</code>.
     *
     * @param column the column name.
     * @param value  the column value, may be <code>null</code>.
     * @return this object.
     */
    public Insert valueNullable(@NonNull String column, @Nullable Object value) {
        return value == null ? valueNull(column) : value(column, value);
    }

    /**
     * Sets the given column to the given value when the row already exists.
     * <p>
     * Cannot be combined with {@link #ignore()}.
     *
     * @param column the column name.
     * @param value  the new value.
     * @return this object.
     */
    public Insert onDuplicateKeyUpdate(@NonNull String column, @NonNull Object value) {
        return onDuplicateKeyUpdateExpr(column, "?", value);
    }

    /**
     * Sets the given column to the given SQL expression when the row already exists,
     * for example <code>`score` + ?</code> to increment a counter.
     * <p>
     * Cannot be combined with {@link #ignore()}.
     *
     * @param column the column name.
     * @param expr   the SQL expression.
     * @param params the expression parameters (substituted in place of <code>?</code> placeholders).
     * @return this object.
     */
    public Insert onDuplicateKeyUpdateExpr(@NonNull String column, @NonNull String expr, @NonNull Object... params) {
        SQLUtil.validateIdentifier(column);
        SQLUtil.validatePlaceholderCount(expr, params);
        this.update.put(column, new Expression(expr, params));
        return this;
    }

    /**
     * Sets every inserted column except the given keys to its inserted value when the row already exists,
     * and declares the given columns as {@link #keys(String...) keys}.
     * <p>
     * Columns that already have an update set via {@link #onDuplicateKeyUpdate(String, Object)} or
     * {@link #onDuplicateKeyUpdateExpr(String, String, Object...)} are left as they are, so the two
     * can be combined in any order. Call this after all values have been added.
     * <p>
     * Cannot be combined with {@link #ignore()}.
     *
     * @param keys the key column names.
     * @return this object.
     */
    public Insert onDuplicateKeyUpdateExcept(@NonNull String... keys) {
        keys(keys);

        this.insert.forEach((column, expression) -> {
            if (!this.keys.contains(column)) {
                this.update.putIfAbsent(column, expression);
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
            throw new IllegalStateException("Can't use IGNORE with ON DUPLICATE KEY UPDATE");
        }

        final Dialect dialect = dialect();

        if (dialect == Dialect.H2 && (this.ignore || !this.update.isEmpty())) {
            return mergeSQL();
        }

        final StringBuilder builder = new StringBuilder("INSERT");

        if (this.ignore) {
            switch (dialect) {
                case SQLITE -> builder.append(" OR IGNORE");
                case POSTGRES -> {
                }
                default -> builder.append(" IGNORE");
            }
        }

        builder.append(" INTO ").append(quote(this.table)).append(" (");
        appendColumns(builder, this.insert.keySet());
        builder.append(") VALUES (");
        this.insert.values().forEach(expression -> builder.append(expression.expr()).append(", "));
        builder.setLength(builder.length() - 2);
        builder.append(")");

        if (this.ignore && dialect == Dialect.POSTGRES) {
            builder.append(" ON CONFLICT");
            appendConflictTarget(builder);
            builder.append(" DO NOTHING");
        }

        if (!this.update.isEmpty()) {
            switch (dialect) {
                case SQLITE, POSTGRES -> {
                    if (dialect == Dialect.POSTGRES) {
                        requireKeys("PostgreSQL requires the key columns for ON CONFLICT DO UPDATE");
                    }

                    builder.append(" ON CONFLICT");
                    appendConflictTarget(builder);
                    builder.append(" DO UPDATE SET ");
                }
                default -> builder.append(" ON DUPLICATE KEY UPDATE ");
            }

            appendAssignments(builder);
        }

        builder.append(";");

        return builder.toString();
    }

    @Override
    public List<Object> getParams() {
        final List<Object> params = new ArrayList<>();

        for (Expression expression : this.insert.values()) {
            Collections.addAll(params, expression.params());
        }

        for (Expression expression : this.update.values()) {
            Collections.addAll(params, expression.params());
        }

        return params;
    }

    /**
     * H2 has neither <code>INSERT IGNORE</code> nor <code>ON DUPLICATE KEY UPDATE</code> outside of its
     * compatibility modes, so both are expressed as a <code>MERGE</code> whose source row is the inserted
     * values. Source columns are named <code>c0, c1, ...</code> so that update expressions referring to
     * unqualified target columns stay unambiguous.
     */
    private String mergeSQL() {
        requireKeys("H2 requires the key columns for IGNORE and ON DUPLICATE KEY UPDATE");

        final List<String> columns = new ArrayList<>(this.insert.keySet());

        for (String key : this.keys) {
            if (!columns.contains(key)) {
                throw new IllegalStateException("Key column `" + key + "` is not inserted; H2 needs it to detect a duplicate row");
            }
        }

        final StringBuilder builder = new StringBuilder("MERGE INTO ").append(quote(this.table)).append(" USING (VALUES (");
        this.insert.values().forEach(expression -> builder.append(expression.expr()).append(", "));
        builder.setLength(builder.length() - 2);
        builder.append(")) AS ").append(quote(SOURCE_ALIAS)).append("(");

        for (int i = 0; i < columns.size(); i++) {
            builder.append(quote("c" + i)).append(", ");
        }

        builder.setLength(builder.length() - 2);
        builder.append(") ON ");

        for (String key : this.keys) {
            builder.append(quote(this.table)).append(".").append(quote(key)).append(" = ")
                .append(sourceColumn(columns.indexOf(key))).append(" AND ");
        }

        builder.setLength(builder.length() - 5);

        if (!this.update.isEmpty()) {
            builder.append(" WHEN MATCHED THEN UPDATE SET ");
            appendAssignments(builder);
        }

        builder.append(" WHEN NOT MATCHED THEN INSERT (");
        appendColumns(builder, columns);
        builder.append(") VALUES (");

        for (int i = 0; i < columns.size(); i++) {
            builder.append(sourceColumn(i)).append(", ");
        }

        builder.setLength(builder.length() - 2);
        builder.append(");");

        return builder.toString();
    }

    private String sourceColumn(int index) {
        return quote(SOURCE_ALIAS) + "." + quote("c" + index);
    }

    private void requireKeys(String message) {
        if (this.keys.isEmpty()) {
            throw new IllegalStateException(message + "; declare them with keys(...)");
        }
    }

    private void appendConflictTarget(StringBuilder builder) {
        if (this.keys.isEmpty()) {
            return;
        }

        builder.append(" (");
        appendColumns(builder, this.keys);
        builder.append(")");
    }

    private void appendAssignments(StringBuilder builder) {
        this.update.forEach((column, expression) -> builder.append(quote(column)).append(" = ").append(expression.expr()).append(", "));
        builder.setLength(builder.length() - 2);
    }

    private void appendColumns(StringBuilder builder, Iterable<String> columns) {
        for (String column : columns) {
            builder.append(quote(column)).append(", ");
        }

        builder.setLength(builder.length() - 2);
    }
}

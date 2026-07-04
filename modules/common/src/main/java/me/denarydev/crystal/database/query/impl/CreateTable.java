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
import me.denarydev.crystal.database.util.SQLUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * A <code>CREATE TABLE</code> query.
 */
public final class CreateTable extends AbstractQuery {

    private final String table;

    private boolean ifNotExists;

    private final List<Column> columns = new ArrayList<>();

    @ApiStatus.Internal
    public CreateTable(ConnectionPool pool, String table) {
        super(pool);
        this.table = table;
    }

    /**
     * Adds the <code>IF NOT EXISTS</code> clause to the query.
     *
     * @return this object.
     */
    public CreateTable ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * Adds a column to the table.
     *
     * @param name the column name.
     * @param type the column type.
     * @return this object.
     */
    public CreateTable column(@NonNull String name, @NonNull String type) {
        SQLUtil.validateIdentifier(name);

        if (this.columns.stream().anyMatch(c -> c.name.equals(name))) {
            throw new IllegalArgumentException("Column " + name + " already exists");
        }

        this.columns.add(new Column(name, type.toUpperCase(Locale.ENGLISH)));

        return this;
    }

    /**
     * Adds an <code>INT</code> column.
     *
     * @param name the column name.
     * @return this object.
     */
    public CreateTable integer(@NonNull String name) {
        return column(name, "INT");
    }

    /**
     * Adds a <code>BIGINT</code> column.
     *
     * @param name the column name.
     * @return this object.
     */
    public CreateTable bigint(@NonNull String name) {
        return column(name, "BIGINT");
    }

    /**
     * Adds a <code>TINYINT(1)</code> column.
     *
     * @param name the column name.
     * @return this object.
     */
    public CreateTable bool(@NonNull String name) {
        return column(name, "TINYINT(1)");
    }

    /**
     * Adds a <code>VARCHAR(size)</code> column.
     *
     * @param name the column name.
     * @param size the maximum number of characters.
     * @return this object.
     */
    public CreateTable varchar(@NonNull String name, int size) {
        return column(name, "VARCHAR(" + size + ")");
    }

    /**
     * Adds a <code>CHAR(size)</code> column.
     *
     * @param name the column name.
     * @param size the maximum number of characters.
     * @return this object.
     */
    public CreateTable character(@NonNull String name, int size) {
        return column(name, "CHAR(" + size + ")");
    }

    /**
     * Adds a <code>TEXT</code> column.
     *
     * @param name the column name.
     * @return this object.
     */
    public CreateTable text(@NonNull String name) {
        return column(name, "TEXT");
    }

    /**
     * Adds a <code>SERIAL</code> column.
     * <p>
     * <b>PostgreSQL only</b>
     *
     * @param name the column name.
     * @return this object.
     */
    public CreateTable serial(@NonNull String name) {
        return column(name, "SERIAL");
    }

    /**
     * Adds a <code>BIGSERIAL</code> column.
     * <p>
     * <b>PostgreSQL only</b>
     *
     * @param name the column name.
     * @return this object.
     */
    public CreateTable bigSerial(@NonNull String name) {
        return column(name, "BIGSERIAL");
    }

    /**
     * Sets <code>NOT NULL</code> on the most recently added column.
     *
     * @return this object.
     */
    public CreateTable notNull() {
        lastColumn().notNull = true;
        return this;
    }

    /**
     * Sets <code>AUTO_INCREMENT</code> on the most recently added column.
     *
     * @return this object.
     */
    public CreateTable autoIncrement() {
        lastColumn().autoIncrement = true;
        return this;
    }

    /**
     * Sets <code>PRIMARY KEY</code> on the most recently added column.
     *
     * @return this object.
     */
    public CreateTable primaryKey() {
        lastColumn().primaryKey = true;
        return this;
    }

    /**
     * Sets the default value for the most recently added column.
     *
     * @param value the default value; must not be null.
     * @return this object.
     */
    public CreateTable defaultValue(@NonNull Object value) {
        lastColumn().defaultValue = value;
        return this;
    }

    /**
     * Adds an <code>INT AUTO_INCREMENT PRIMARY KEY</code> column.
     *
     * @param name the column name.
     * @return this object.
     */
    public CreateTable intKey(@NonNull String name) {
        return integer(name).autoIncrement().primaryKey();
    }

    private Column lastColumn() {
        if (this.columns.isEmpty()) {
            throw new IllegalStateException("No columns added");
        }

        return this.columns.getLast();
    }

    @Override
    public String getSQL() {
        final Dialect dialect = pool.implementationType().dialect();
        final StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE ");

        if (this.ifNotExists) {
            builder.append("IF NOT EXISTS ");
        }

        builder.append("`").append(this.table).append("` (");

        final List<String> primaryKeys = new ArrayList<>();

        for (final Column col : this.columns) {
            builder.append("`").append(col.name).append("` ").append(col.type(dialect));

            if (col.notNull) {
                if (dialect != Dialect.SQLITE || !col.autoIncrement) {
                    builder.append(" NOT NULL");
                }
            }

            if (col.autoIncrement) {
                if (dialect == Dialect.DEFAULT) {
                    builder.append(" AUTO_INCREMENT");
                }
            }

            if (col.primaryKey) {
                if (dialect == Dialect.SQLITE || dialect == Dialect.POSTGRES) {
                    builder.append(" PRIMARY KEY");
                } else {
                    primaryKeys.add(col.name);
                }
            }

            if (col.defaultValue != null) {
                builder.append(" DEFAULT ").append(SQLUtil.valueToSqlString(col.defaultValue));
            }

            builder.append(", ");
        }

        if (!primaryKeys.isEmpty()) {
            builder.append("PRIMARY KEY (");

            for (final String col : primaryKeys) {
                builder.append("`").append(col).append("`, ");
            }

            builder.setLength(builder.length() - 2);

            builder.append(")");
        } else if (!this.columns.isEmpty()) {
            builder.setLength(builder.length() - 2);
        }

        builder.append(");");

        return builder.toString();
    }

    @Override
    public List<Object> getParams() {
        return Collections.emptyList();
    }

    @ApiStatus.Internal
    private static final class Column {
        private final String name;
        private final String type;

        private boolean notNull;
        private boolean autoIncrement;
        private boolean primaryKey;
        private Object defaultValue;

        public Column(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String type(Dialect dialect) {
            switch (dialect) {
                case SQLITE -> {
                    if (autoIncrement && primaryKey) {
                        return "INTEGER";
                    }
                }
                case POSTGRES -> {
                    if (autoIncrement) {
                        switch (this.type) {
                            case "SMALLINT" -> {
                                return "SMALLSERIAL";
                            }
                            case "INT", "INTEGER" -> {
                                return "SERIAL";
                            }
                            case "BIGINT" -> {
                                return "BIGSERIAL";
                            }
                        }
                    }
                }
            }

            return this.type;
        }
    }
}

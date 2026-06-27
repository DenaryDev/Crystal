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
 * Запрос <code>CREATE TABLE</code>.
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
     * Добавляет предложение <code>IF NOT EXISTS</code> в запрос.
     *
     * @return Этот объект.
     */
    public CreateTable ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * Добавляет столбец в таблицу.
     *
     * @param name Имя столбца.
     * @param type Тип столбца.
     * @return Этот объект.
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
     * Добавляет столбец типа <code>INT</code>.
     *
     * @param name Имя столбца.
     * @return Этот объект.
     */
    public CreateTable integer(@NonNull String name) {
        return column(name, "INT");
    }

    /**
     * Добавляет столбец типа <code>BIGINT</code>.
     *
     * @param name Имя столбца.
     * @return Этот объект.
     */
    public CreateTable bigint(@NonNull String name) {
        return column(name, "BIGINT");
    }

    /**
     * Добавляет столбец типа <code>TINYINT(1)</code>.
     *
     * @param name Имя столбца.
     * @return Этот объект.
     */
    public CreateTable bool(@NonNull String name) {
        return column(name, "TINYINT(1)");
    }

    /**
     * Добавляет столбец типа <code>VARCHAR(size)</code>.
     *
     * @param name Имя столбца.
     * @param size Максимальное количество символов.
     * @return Этот объект.
     */
    public CreateTable varchar(@NonNull String name, int size) {
        return column(name, "VARCHAR(" + size + ")");
    }

    /**
     * Добавляет столбец типа <code>CHAR(size)</code>.
     *
     * @param name Имя столбца.
     * @param size Максимальное количество символов.
     * @return Этот объект.
     */
    public CreateTable character(@NonNull String name, int size) {
        return column(name, "CHAR(" + size + ")");
    }

    /**
     * Добавляет столбец типа <code>TEXT</code>.
     *
     * @param name Имя столбца.
     * @return Этот объект.
     */
    public CreateTable text(@NonNull String name) {
        return column(name, "TEXT");
    }

    /**
     * Добавляет столбец типа <code>SERIAL</code>.
     * <p>
     * <b>Только для PostgreSQL</b>
     *
     * @param name Имя столбца
     * @return Этот объект.
     */
    public CreateTable serial(@NonNull String name) {
        return column(name, "SERIAL");
    }

    /**
     * Добавляет столбец типа <code>BIGSERIAL</code>.
     * <p>
     * <b>Только для PostgreSQL</b>
     *
     * @param name Имя столбца
     * @return Этот объект.
     */
    public CreateTable bigSerial(@NonNull String name) {
        return column(name, "BIGSERIAL");
    }

    /**
     * Устанавливает <code>NOT NULL</code> для последнего добавленного столбца.
     *
     * @return Этот объект.
     */
    public CreateTable notNull() {
        lastColumn().notNull = true;
        return this;
    }

    /**
     * Добавляет предложение <code>AUTO_INCREMENT</code> для последнего добавленного столбца.
     *
     * @return Этот объект.
     */
    public CreateTable autoIncrement() {
        lastColumn().autoIncrement = true;
        return this;
    }

    /**
     * Добавляет предложение <code>PRIMARY KEY</code> для последнего добавленного столбца.
     *
     * @return Этот объект.
     */
    public CreateTable primaryKey() {
        lastColumn().primaryKey = true;
        return this;
    }

    /**
     * Устанавливает значение по умолчанию для последнего добавленного столбца.
     *
     * @param value Устанавливаемое значение, не должно быть null.
     * @return Этот объект.
     */
    public CreateTable defaultValue(@NonNull Object value) {
        lastColumn().defaultValue = value;
        return this;
    }

    /**
     * Добавляет столбец <code>INT AUTO_INCREMENT PRIMARY KEY</code>.
     *
     * @param name Имя столбца.
     * @return Этот объект.
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

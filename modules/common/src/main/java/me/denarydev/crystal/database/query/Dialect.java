/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query;

/**
 * Enumeration of supported SQL dialects.
 */
public enum Dialect {
    /**
     * The standard SQL dialect (typically MySQL/MariaDB).
     */
    DEFAULT,

    /**
     * The SQL dialect for H2 in its regular (non-compatibility) mode.
     * <p>
     * DDL is MySQL-like, but upserts are expressed through <code>MERGE</code>.
     */
    H2,

    /**
     * The SQL dialect for SQLite.
     */
    SQLITE,

    /**
     * The SQL dialect for PostgreSQL.
     */
    POSTGRES;

    /**
     * Returns the identifier quoting character of this dialect:
     * a double quote for PostgreSQL and a backtick for everything else.
     *
     * @return the quote character.
     */
    public char quoteChar() {
        return this == POSTGRES ? '"' : '`';
    }

    /**
     * Quotes a table or column name for this dialect, e.g. <code>`name`</code> or <code>"name"</code>.
     *
     * @param identifier the identifier to quote.
     * @return the quoted identifier.
     */
    public String quote(String identifier) {
        final char quote = quoteChar();
        return quote + identifier + quote;
    }
}

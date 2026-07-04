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
     * The SQL dialect for SQLite.
     */
    SQLITE,

    /**
     * The SQL dialect for PostgreSQL.
     */
    POSTGRES
}

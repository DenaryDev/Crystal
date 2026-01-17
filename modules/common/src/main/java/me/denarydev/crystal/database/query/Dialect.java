/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query;

/**
 * Перечисление поддерживаемых диалектов SQL.
 *
 * @author DenaryDev
 * @since 4:35 17.01.2026
 */
public enum Dialect {
    /**
     * Стандартный диалект SQL (обычно MySQL/MariaDB).
     */
    DEFAULT,

    /**
     * Диалект для базы данных SQLite.
     */
    SQLITE,

    /**
     * Диалект для базы данных PostgreSQL.
     */
    POSTGRES
}

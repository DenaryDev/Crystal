/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database;

import me.denarydev.crystal.database.query.Dialect;

/**
 * Supported database types.
 */
public enum DatabaseType {
    SQLITE(false),
    H2(false),
    MYSQL(true),
    MARIADB(true),
    POSTGRESQL(true);

    private final boolean remote;

    DatabaseType(boolean remote) {
        this.remote = remote;
    }

    /**
     * Returns whether this database type uses a remote network connection.
     *
     * @return {@code true} if this database type uses a remote connection
     */
    public boolean remote() {
        return remote;
    }

    /**
     * Returns the SQL dialect for this database type.
     *
     * @return the SQL dialect
     */
    public Dialect dialect() {
        return switch (this) {
            case SQLITE -> Dialect.SQLITE;
            case POSTGRESQL -> Dialect.POSTGRES;
            default -> Dialect.DEFAULT;
        };
    }
}

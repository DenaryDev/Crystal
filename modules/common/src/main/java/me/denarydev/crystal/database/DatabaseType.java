/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database;

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
     * @return true, если база данных использует удалённое подключение
     */
    public boolean remote() {
        return remote;
    }
}

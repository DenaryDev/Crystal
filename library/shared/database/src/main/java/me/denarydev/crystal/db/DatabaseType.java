/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("2.1.0")
public enum DatabaseType {
    SQLITE("SQLite", false),
    H2("H2", false),
    MYSQL("MySQL", true),
    MARIADB("MariaDB", true),
    POSTGRESQL("PostgreSQL", true);

    private final String friendlyName;
    private final boolean remote;
    DatabaseType(String friendlyName, boolean remote) {
        this.friendlyName = friendlyName;
        this.remote = remote;
    }

    public String friendlyName() {
        return friendlyName;
    }

    public boolean remote() {
        return remote;
    }
}

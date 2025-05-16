/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.settings;

import me.denarydev.crystal.db.DatabaseType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * @author DenaryDev
 * @since 0:58 24.11.2023
 */
@ApiStatus.AvailableSince("2.1.0")
public sealed interface ConnectionSettings permits FlatfileConnectionSettings, HikariConnectionSettings {

    /**
     * Prefix for pool names in hikari.
     * <p>
     * Unique prefix will allow you to distinguish
     * hikari logs from this plugin from hikari logs of other plugins.
     * <p>
     * <u>It's best to use your plugin name as pool name prefix.
     */
    @NotNull
    String pluginName();

    /**
     * Database type
     * <p>
     * For SQLite or H2, see {@link FlatfileConnectionSettings}
     * <p>
     * For MySQL, MariaDB or PostgreSQL, see {@link HikariConnectionSettings}
     *
     * @see DatabaseType
     */
    @NotNull
    DatabaseType databaseType();

    /**
     * {@link org.slf4j.Logger} impl from your plugin
     */
    @NotNull
    Logger logger();
}

/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.settings;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * This class is used to configure connections to local databases.
 * Currently only SQLite and H2 are supported.
 *
 * @author DenaryDev
 * @since 0:58 24.11.2023
 */
public non-sealed interface FlatfileConnectionSettings extends ConnectionSettings {

    /**
     * Path to the plugin's data folder.
     */
    @NotNull
    Path dataFolder();
}

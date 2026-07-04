/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.internal;

import io.sapphiremc.lib.configurate.objectmapping.ConfigSerializable;
import io.sapphiremc.lib.configurate.objectmapping.meta.Comment;
import me.denarydev.crystal.Crystal;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
@ConfigSerializable
public final class CrystalConfig {
    public static final String HEADER = """
        +--------------------------------+
        |             Crystal            |
        |          by DenaryDev          |
        +--------------------------------+
        |- This config defines paths to all other config files.
        |- If a config file is located outside the plugin folder, provide its full path.
        """;

    @Comment("Path to the connection pool settings file.")
    private Path poolsPath = Crystal.instance().dataFolder().resolve("pools.yml");
    @Comment("Path to the messages settings file.")
    private Path messagesPath = Crystal.instance().dataFolder().resolve("messages.yml");

    public Path poolsPath() {
        return poolsPath;
    }

    public Path messagesPath() {
        return messagesPath;
    }
}

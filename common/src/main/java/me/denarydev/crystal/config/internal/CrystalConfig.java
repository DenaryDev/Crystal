/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.internal;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import me.denarydev.crystal.Crystal;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

/**
 * @author DenaryDev
 * @since 1:44 28.10.2025
 */
@ApiStatus.Internal
@Configuration
public final class CrystalConfig {
    public static final String HEADER = """
                                        +--------------------------------+
                                        |             Crystal            |
                                        |          by DenaryDev          |
                                        +--------------------------------+
                                        |- В этом конфиге настраиваются пути ко всем остальным конфигам.
                                        |- Если конфиг лежит не в папке плагина, указывайте полный путь к нему!
                                        """;

    @Comment("Путь к файлу настроек пулов бд.")
    private Path poolsPath = Crystal.instance().dataFolder().resolve("pools.yml");

    public Path poolsPath() {
        return poolsPath;
    }
}

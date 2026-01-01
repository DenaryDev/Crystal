/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.core;

import me.denarydev.crystal.Crystal;
import org.slf4j.Logger;
import ru.prostocraft.core.Async;

import java.nio.file.Path;

/**
 * @author DenaryDev
 * @since 19:03 23.11.2025
 */
public final class CrystalCore extends Crystal {

    private final CorePlugin plugin;

    public CrystalCore(CorePlugin plugin) {
        this.plugin = plugin;

        setInstance(this);
    }

    @Override
    public Logger logger() {
        return plugin.getSLF4JLogger();
    }

    @Override
    public Path dataFolder() {
        return plugin.getDataFolder().toPath();
    }

    @Override
    public void runAsync(Runnable task) {
        Async.run(task);
    }
}

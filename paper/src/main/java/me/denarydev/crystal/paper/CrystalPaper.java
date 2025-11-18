/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper;

import me.denarydev.crystal.Crystal;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * @author DenaryDev
 * @since 3:46 28.10.2025
 */
@ApiStatus.Internal
public final class CrystalPaper extends Crystal {

    private final PaperPlugin plugin;

    public CrystalPaper(PaperPlugin plugin) {
        this.plugin = plugin;

        setInstance(this);
    }

    @Override
    public Logger logger() {
        return plugin.getSLF4JLogger();
    }

    @Override
    public Path dataFolder() {
        return plugin.getDataPath();
    }

    @Override
    public void runAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }
}

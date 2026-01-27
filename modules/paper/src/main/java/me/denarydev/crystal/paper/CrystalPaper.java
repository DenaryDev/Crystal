/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.Platform;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.file.Path;

@ApiStatus.Internal
public final class CrystalPaper extends Crystal {
    private final PaperPlugin plugin;

    CrystalPaper(PaperPlugin plugin) {
        super(Platform.PAPER);
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

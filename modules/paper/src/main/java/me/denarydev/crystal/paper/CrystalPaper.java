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
import me.denarydev.crystal.paper.scheduler.PaperScheduler;
import me.denarydev.crystal.scheduler.Scheduler;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.file.Path;

@ApiStatus.Internal
public final class CrystalPaper extends Crystal {
    private final PaperPlugin plugin;
    private final PaperScheduler scheduler;

    CrystalPaper(PaperPlugin plugin) {
        super(Platform.PAPER);
        this.plugin = plugin;
        this.scheduler = new PaperScheduler(plugin);

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
    public Scheduler scheduler() {
        return scheduler;
    }
}

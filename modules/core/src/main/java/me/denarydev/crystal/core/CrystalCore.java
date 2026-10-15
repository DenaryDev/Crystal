/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.core;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.Platform;
import me.denarydev.crystal.core.scheduler.CoreScheduler;
import me.denarydev.crystal.scheduler.Scheduler;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class CrystalCore extends Crystal {
    private final CorePlugin plugin;
    private final CoreScheduler scheduler = new CoreScheduler();

    CrystalCore(CorePlugin plugin) {
        super(Platform.CORE);
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
    public Scheduler scheduler() {
        return scheduler;
    }
}

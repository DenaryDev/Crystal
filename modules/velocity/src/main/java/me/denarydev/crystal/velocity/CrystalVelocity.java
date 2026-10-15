/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.velocity;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.Platform;
import me.denarydev.crystal.scheduler.Scheduler;
import me.denarydev.crystal.velocity.scheduler.VelocityScheduler;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.file.Path;

@ApiStatus.Internal
public final class CrystalVelocity extends Crystal {
    private final VelocityPlugin plugin;
    private final VelocityScheduler scheduler;

    CrystalVelocity(VelocityPlugin plugin) {
        super(Platform.VELOCITY);
        this.plugin = plugin;
        this.scheduler = new VelocityScheduler(plugin.proxy(), plugin);

        setInstance(this);
    }

    @Override
    public Logger logger() {
        return plugin.logger();
    }

    @Override
    public Path dataFolder() {
        return plugin.dataFolder();
    }

    @Override
    public Scheduler scheduler() {
        return scheduler;
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.velocity.scheduler;

import com.velocitypowered.api.proxy.ProxyServer;
import me.denarydev.crystal.scheduler.Scheduler;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.Executor;

/**
 * {@link Scheduler} backed by the Velocity scheduler.
 * <p>
 * Velocity has no main thread, so {@link #sync()} and {@link #async()} both submit
 * the task to the proxy scheduler.
 */
@NullMarked
public final class VelocityScheduler implements Scheduler {

    private final Executor executor;

    /**
     * Creates a scheduler whose tasks are owned by the given plugin.
     *
     * @param proxy  the proxy server.
     * @param plugin the plugin instance registered with Velocity.
     */
    public VelocityScheduler(ProxyServer proxy, Object plugin) {
        this.executor = task -> proxy.getScheduler().buildTask(plugin, task).schedule();
    }

    @Override
    public Executor async() {
        return executor;
    }

    @Override
    public Executor sync() {
        return executor;
    }
}

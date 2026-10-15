/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.scheduler;

import me.denarydev.crystal.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.Executor;

/**
 * {@link Scheduler} backed by Paper's region-aware schedulers, so it works on both Paper and Folia.
 * <p>
 * {@link #async()} uses the {@link org.bukkit.Server#getAsyncScheduler() async scheduler} and
 * {@link #sync()} the {@link org.bukkit.Server#getGlobalRegionScheduler() global region scheduler},
 * which is the main thread on Paper. On Folia, anything touching an entity (opening a menu, teleporting)
 * must run on that entity's region instead: use {@link #entity(Entity)} for that.
 * <p>
 * {@link #current()} returns the scheduler bound to the Crystal plugin. Create your own instance
 * when tasks should be owned by, and cancelled together with, your plugin.
 */
@NullMarked
public final class PaperScheduler implements Scheduler {

    private final Plugin plugin;
    private final Executor async;
    private final Executor sync;

    /**
     * Creates a scheduler whose tasks are owned by the given plugin.
     *
     * @param plugin the plugin to schedule tasks under.
     */
    public PaperScheduler(Plugin plugin) {
        this.plugin = plugin;
        this.async = task -> Bukkit.getAsyncScheduler().runNow(plugin, ignored -> task.run());
        this.sync = task -> Bukkit.getGlobalRegionScheduler().run(plugin, ignored -> task.run());
    }

    /**
     * Returns the scheduler bound to the Crystal plugin.
     *
     * @return the current Paper scheduler.
     */
    public static PaperScheduler current() {
        return (PaperScheduler) Scheduler.current();
    }

    /**
     * Returns the plugin this scheduler schedules tasks under.
     *
     * @return the owning plugin.
     */
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public Executor async() {
        return async;
    }

    @Override
    public Executor sync() {
        return sync;
    }

    /**
     * Returns an executor that runs tasks on the thread owning the given entity.
     * <p>
     * On Paper this is the main thread; on Folia it is the entity's region thread.
     * Tasks submitted after the entity has been removed are silently dropped.
     *
     * @param entity the entity whose thread to run on.
     * @return the entity executor.
     */
    public Executor entity(Entity entity) {
        return task -> entity.getScheduler().run(plugin, ignored -> task.run(), null);
    }
}

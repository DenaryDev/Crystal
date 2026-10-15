/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.denarydev.crystal.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * {@link Scheduler} backed by Paper's region-aware schedulers, so it works on both Paper and Folia.
 * <p>
 * {@link #async()} uses the {@link org.bukkit.Server#getAsyncScheduler() async scheduler} and
 * {@link #sync()} the {@link org.bukkit.Server#getGlobalRegionScheduler() global region scheduler},
 * which is the main thread on Paper. On Folia, anything touching an entity (opening a menu, teleporting)
 * must run on that entity's region instead: use {@link #entity(Entity)} for that.
 * <p>
 * Delayed and repeating tasks are available for each of the three schedulers; they return the
 * {@link ScheduledTask} so the caller can cancel them. Delays for the global and entity schedulers are
 * in ticks and must be at least 1, delays for the async scheduler are in a {@link TimeUnit}.
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

    //region Delayed and repeating tasks

    /**
     * Runs the task on the main thread (global region) after the given delay.
     *
     * @param task       the task to run.
     * @param delayTicks the delay in ticks, at least 1.
     * @return the scheduled task.
     */
    public ScheduledTask runDelayed(Runnable task, long delayTicks) {
        return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, ignored -> task.run(), delayTicks);
    }

    /**
     * Runs the task on the main thread (global region) repeatedly.
     *
     * @param task              the task to run; it receives its own {@link ScheduledTask} so it can cancel itself.
     * @param initialDelayTicks the delay before the first run in ticks, at least 1.
     * @param periodTicks       the period between runs in ticks, at least 1.
     * @return the scheduled task.
     */
    public ScheduledTask runAtFixedRate(Consumer<ScheduledTask> task, long initialDelayTicks, long periodTicks) {
        return Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task, initialDelayTicks, periodTicks);
    }

    /**
     * Runs the task on the main thread (global region) repeatedly.
     *
     * @param task              the task to run.
     * @param initialDelayTicks the delay before the first run in ticks, at least 1.
     * @param periodTicks       the period between runs in ticks, at least 1.
     * @return the scheduled task.
     */
    public ScheduledTask runAtFixedRate(Runnable task, long initialDelayTicks, long periodTicks) {
        return runAtFixedRate(ignored -> task.run(), initialDelayTicks, periodTicks);
    }

    /**
     * Runs the task asynchronously after the given delay.
     *
     * @param task  the task to run.
     * @param delay the delay.
     * @param unit  the unit of the delay.
     * @return the scheduled task.
     */
    public ScheduledTask runAsyncDelayed(Runnable task, long delay, TimeUnit unit) {
        return Bukkit.getAsyncScheduler().runDelayed(plugin, ignored -> task.run(), delay, unit);
    }

    /**
     * Runs the task asynchronously and repeatedly.
     *
     * @param task         the task to run; it receives its own {@link ScheduledTask} so it can cancel itself.
     * @param initialDelay the delay before the first run.
     * @param period       the period between runs.
     * @param unit         the unit of the delay and period.
     * @return the scheduled task.
     */
    public ScheduledTask runAsyncAtFixedRate(Consumer<ScheduledTask> task, long initialDelay, long period, TimeUnit unit) {
        return Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task, initialDelay, period, unit);
    }

    /**
     * Runs the task asynchronously and repeatedly.
     *
     * @param task         the task to run.
     * @param initialDelay the delay before the first run.
     * @param period       the period between runs.
     * @param unit         the unit of the delay and period.
     * @return the scheduled task.
     */
    public ScheduledTask runAsyncAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return runAsyncAtFixedRate(ignored -> task.run(), initialDelay, period, unit);
    }

    /**
     * Runs the task on the thread owning the given entity after the given delay.
     * <p>
     * On Paper this is the main thread; on Folia it is the entity's region thread.
     *
     * @param entity     the entity whose thread to run on.
     * @param task       the task to run.
     * @param delayTicks the delay in ticks, at least 1.
     * @return the scheduled task, or {@code null} if the entity has already been removed.
     */
    public @Nullable ScheduledTask runDelayed(Entity entity, Runnable task, long delayTicks) {
        return entity.getScheduler().runDelayed(plugin, ignored -> task.run(), null, delayTicks);
    }

    /**
     * Runs the task on the thread owning the given entity repeatedly.
     * The task stops when the entity is removed.
     * <p>
     * On Paper this is the main thread; on Folia it is the entity's region thread.
     *
     * @param entity            the entity whose thread to run on.
     * @param task              the task to run; it receives its own {@link ScheduledTask} so it can cancel itself.
     * @param initialDelayTicks the delay before the first run in ticks, at least 1.
     * @param periodTicks       the period between runs in ticks, at least 1.
     * @return the scheduled task, or {@code null} if the entity has already been removed.
     */
    public @Nullable ScheduledTask runAtFixedRate(Entity entity, Consumer<ScheduledTask> task, long initialDelayTicks, long periodTicks) {
        return entity.getScheduler().runAtFixedRate(plugin, task, null, initialDelayTicks, periodTicks);
    }

    /**
     * Runs the task on the thread owning the given entity repeatedly.
     * The task stops when the entity is removed.
     *
     * @param entity            the entity whose thread to run on.
     * @param task              the task to run.
     * @param initialDelayTicks the delay before the first run in ticks, at least 1.
     * @param periodTicks       the period between runs in ticks, at least 1.
     * @return the scheduled task, or {@code null} if the entity has already been removed.
     */
    public @Nullable ScheduledTask runAtFixedRate(Entity entity, Runnable task, long initialDelayTicks, long periodTicks) {
        return runAtFixedRate(entity, ignored -> task.run(), initialDelayTicks, periodTicks);
    }

    //endregion
}

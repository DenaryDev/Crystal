/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.scheduler;

import me.denarydev.crystal.Crystal;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Platform-independent access to the asynchronous and synchronous executors of the current platform.
 * <p>
 * {@link #async()} runs tasks off the main thread, {@link #sync()} runs them on it.
 * On platforms without a main thread (Velocity) {@link #sync()} simply runs the task on the platform
 * scheduler; on platforms without a synchronous scheduler (Core) it runs the task in the calling thread.
 * <p>
 * Both executors are plain {@link Executor}s, so they compose with {@link CompletableFuture}:
 * <pre>{@code
 * Scheduler.current().asyncThenSync(() -> repository.load(uuid), data -> menu.show(player));
 *
 * CompletableFuture.supplyAsync(() -> repository.load(uuid), scheduler.async())
 *     .thenAcceptAsync(data -> menu.show(player), scheduler.sync());
 * }</pre>
 */
@NullMarked
public interface Scheduler {

    /**
     * Returns the scheduler of the current platform.
     *
     * @return the current scheduler.
     */
    static Scheduler current() {
        return Crystal.instance().scheduler();
    }

    /**
     * Returns an executor that runs tasks asynchronously, off the main thread.
     *
     * @return the asynchronous executor.
     */
    Executor async();

    /**
     * Returns an executor that runs tasks on the main (server) thread.
     * <p>
     * See the class documentation for the behaviour on platforms without a main thread.
     *
     * @return the synchronous executor.
     */
    Executor sync();

    /**
     * Runs the task asynchronously.
     *
     * @param task the task to run.
     * @return a future completed when the task finishes.
     */
    default CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, async());
    }

    /**
     * Computes a value asynchronously.
     *
     * @param supplier the computation.
     * @param <T>      the value type.
     * @return a future completed with the computed value.
     */
    default <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, async());
    }

    /**
     * Runs the task on the main thread.
     *
     * @param task the task to run.
     * @return a future completed when the task finishes.
     */
    default CompletableFuture<Void> runSync(Runnable task) {
        return CompletableFuture.runAsync(task, sync());
    }

    /**
     * Computes a value asynchronously and hands it to the consumer on the main thread.
     * <p>
     * If the supplier throws, the consumer is not called and the returned future
     * completes exceptionally.
     *
     * @param supplier the asynchronous computation.
     * @param consumer the synchronous continuation.
     * @param <T>      the value type.
     * @return a future completed when the consumer finishes.
     */
    default <T> CompletableFuture<Void> asyncThenSync(Supplier<T> supplier, Consumer<? super T> consumer) {
        return supplyAsync(supplier).thenAcceptAsync(consumer, sync());
    }
}

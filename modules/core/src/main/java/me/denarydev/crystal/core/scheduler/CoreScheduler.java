/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.core.scheduler;

import me.denarydev.crystal.scheduler.Scheduler;
import org.jspecify.annotations.NullMarked;
import ru.prostocraft.core.Async;

import java.util.concurrent.Executor;

/**
 * {@link Scheduler} for ProstoCraft Core.
 * <p>
 * {@link #async()} delegates to {@link Async#run(Runnable)}. Core has no synchronous scheduler,
 * so {@link #sync()} runs the task in the calling thread.
 */
@NullMarked
public final class CoreScheduler implements Scheduler {

    private final Executor async = Async::run;
    private final Executor sync = Runnable::run;

    @Override
    public Executor async() {
        return async;
    }

    @Override
    public Executor sync() {
        return sync;
    }
}

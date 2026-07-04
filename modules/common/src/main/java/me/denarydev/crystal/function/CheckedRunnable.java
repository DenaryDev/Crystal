/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.function;

/**
 * A runnable that may throw a checked exception.
 */
@FunctionalInterface
public interface CheckedRunnable<E extends Exception> {

    /**
     * Executes the action.
     *
     * @throws E if an error occurs.
     */
    void run() throws E;

    /**
     * Wraps a standard JDK {@link Runnable} into a checked runnable.
     *
     * @param runnable the runnable to wrap.
     * @return a checked runnable backed by the given runnable.
     */
    static CheckedRunnable<RuntimeException> from(Runnable runnable) {
        return runnable::run;
    }
}

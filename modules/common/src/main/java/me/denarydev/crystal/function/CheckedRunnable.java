/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.function;

/**
 * Исполняемая функция, которая может выкинуть отслеживаемое исключение.
 */
@FunctionalInterface
public interface CheckedRunnable<E extends Exception> {

    /**
     * Выполняет действие.
     *
     * @throws E может выкинуть указанное исключение
     */
    void run() throws E;

    /**
     * Оборачивает {@link Runnable} из JDK в проверяемый вариант.
     *
     * @param runnable исполняемая функция
     * @return проверяемая исполняемая функция
     */
    static CheckedRunnable<RuntimeException> from(Runnable runnable) {
        return runnable::run;
    }
}

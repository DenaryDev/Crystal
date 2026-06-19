/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal;

/**
 * Перечисление поддерживаемых платформ для работы Crystal.
 * <p>
 * Определяет среду выполнения, позволяя библиотеке адаптировать логику
 * под конкретное серверное ядро или прокси-сервер.
 */
public enum Platform {
    /**
     * Платформа Paper и её производные.
     */
    PAPER,

    /**
     * Прокси-сервер Velocity.
     */
    VELOCITY,

    /**
     * Самописное ядро ProstoCraft Core.
     */
    CORE;

    static Platform current;

    /**
     * Возвращает текущую платформу, на которой запущен Crystal.
     *
     * @return текущая платформа
     */
    public static Platform current() {
        return current;
    }

    /**
     * Проверяет, является ли данная платформа текущей средой выполнения.
     * <p>
     * Пример использования: {@code Platform.CORE.isCurrent()}
     *
     * @return {@code true}, если платформа активна
     */
    public boolean isCurrent() {
        return this == current;
    }
}

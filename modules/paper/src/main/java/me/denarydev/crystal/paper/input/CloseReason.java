/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.input;

/**
 * @author DenaryDev
 * @since 5:21 27.11.2025
 */
public enum CloseReason {
    /**
     * От игрока успешно получен ответ на запрос.
     */
    SUCCESS,
    /**
     * Запрос отменён игроком.
     */
    CANCELLED,
    /**
     * Истекло время ожидания ответа на запрос.
     */
    TIMEOUT,
    /**
     * При попытке обработать ответ на запрос произошла ошибка.
     */
    ERROR
}

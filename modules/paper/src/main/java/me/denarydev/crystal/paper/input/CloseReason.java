/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.input;

/**
 * Причина закрытия {@link ChatPrompt}.
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

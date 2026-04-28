/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.error;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Универсальный интерфейс для обработки и регистрации ошибок в экосистеме Crystal.
 * Обеспечивает единый формат уведомления пользователей и логирования инцидентов.
 *
 * @param <T> тип получателя уведомления (например, Player в Paper или Velocity)
 */
public interface ErrorReporter<T> {

    /**
     * Регистрирует ошибку и отправляет уведомление пользователю.
     *
     * @param target получатель уведомления об ошибке
     * @param error  возникшее исключение
     */
    void report(@NonNull T target, @NonNull Throwable error);

    /**
     * Регистрирует ошибку с дополнительным контекстом и отправляет уведомление пользователю.
     *
     * @param target     получатель уведомления об ошибке
     * @param error      возникшее исключение
     * @param logMessage сообщение для логгера (поддерживает плейсхолдеры {})
     * @param params     аргументы для форматирования сообщения
     */
    void report(@NonNull T target, @NonNull Throwable error, @Nullable String logMessage, Object... params);
}

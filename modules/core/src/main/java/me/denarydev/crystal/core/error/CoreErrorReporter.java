/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.core.error;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.error.ErrorReporter;
import me.denarydev.crystal.random.StringGenerator;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import ru.prostocraft.core.data.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Обработчик ошибок для платформы Core.
 * Регистрирует инцидент в логах и отправляет игроку сообщение с уникальным кодом ошибки.
 */
public final class CoreErrorReporter implements ErrorReporter<Player> {
    private final Logger logger;

    /**
     * Создает новый экземпляр репортера для указанного логгера.
     *
     * @param logger логгер, в который будут записываться ошибки
     * @return новый экземпляр {@link CoreErrorReporter}
     */
    public static CoreErrorReporter create(@NonNull Logger logger) {
        return new CoreErrorReporter(logger);
    }

    private CoreErrorReporter(Logger logger) {
        this.logger = logger;
    }

    /**
     * Регистрирует ошибку и отправляет уведомление пользователю.
     *
     * @param target получатель уведомления об ошибке
     * @param error  возникшее исключение
     */
    @Override
    public void report(@NonNull Player target, @NonNull Throwable error) {
        report(target, error, null);
    }

    /**
     * Регистрирует ошибку с дополнительным контекстом и отправляет уведомление пользователю.
     *
     * @param target     получатель уведомления об ошибке
     * @param error      возникшее исключение
     * @param logMessage сообщение для логгера (поддерживает плейсхолдеры {})
     * @param params     аргументы для форматирования сообщения
     */
    @Override
    public void report(@NonNull Player target, @NonNull Throwable error, @Nullable String logMessage, Object... params) {
        final String errorCode = StringGenerator.generateRandomString(8);

        {
            final String messageWithCode = Objects.requireNonNullElse(logMessage, "An exception was thrown") +
                " (error code: " + errorCode + " )";

            final List<Object> allParams = new ArrayList<>(List.of(params));
            allParams.add(error);
            this.logger.error(messageWithCode, allParams.toArray(new Object[0]));
        }

        {
            final String playerMessage = "<hover:show_text:'%s'><click:copy_to_clipboard:'%s'>%s</click></hover>".formatted(
                Crystal.instance().messages().errors().errorCodeHover(),
                errorCode,
                Crystal.instance().messages().errors().errorWithCode()
            );

            target.sendMessage(playerMessage);
        }
    }
}

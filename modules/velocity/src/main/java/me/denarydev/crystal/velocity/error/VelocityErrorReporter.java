/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.velocity.error;

import com.velocitypowered.api.proxy.Player;
import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.error.ErrorReporter;
import me.denarydev.crystal.random.StringGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Обработчик ошибок для платформы Velocity.
 * Регистрирует инцидент в логах и отправляет игроку сообщение с уникальным кодом ошибки.
 */
public final class VelocityErrorReporter implements ErrorReporter<Player> {
    private final Logger logger;

    /**
     * Создает новый экземпляр репортера для указанного логгера.
     *
     * @param logger логгер, в который будут записываться ошибки
     * @return новый экземпляр {@link VelocityErrorReporter}
     */
    public static VelocityErrorReporter of(@NonNull Logger logger) {
        return new VelocityErrorReporter(logger);
    }

    private VelocityErrorReporter(Logger logger) {
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
            final Component playerMessage = MiniMessage.miniMessage().deserialize(
                Crystal.instance().messages().errors().errorWithCode(),
                Placeholder.unparsed("code", errorCode)
            ).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(
                    Crystal.instance().messages().errors().errorCodeHover()
                ))
            ).clickEvent(ClickEvent.copyToClipboard(errorCode));

            target.sendMessage(playerMessage);
        }
    }
}

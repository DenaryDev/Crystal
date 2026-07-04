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
 * Error reporter for the Core platform.
 * Logs the incident and sends the player a message containing a unique error code.
 */
public final class CoreErrorReporter implements ErrorReporter<Player> {
    private final Logger logger;

    /**
     * Creates a new reporter backed by the given logger.
     *
     * @param logger the logger to write errors to.
     * @return a new {@link CoreErrorReporter} instance.
     */
    public static CoreErrorReporter create(@NonNull Logger logger) {
        return new CoreErrorReporter(logger);
    }

    private CoreErrorReporter(Logger logger) {
        this.logger = logger;
    }

    /**
     * Reports an error and sends a notification to the given recipient.
     *
     * @param target the recipient to notify.
     * @param error  the exception that occurred.
     */
    @Override
    public void report(@NonNull Player target, @NonNull Throwable error) {
        report(target, error, null);
    }

    /**
     * Reports an error with additional context and sends a notification to the given recipient.
     *
     * @param target     the recipient to notify.
     * @param error      the exception that occurred.
     * @param logMessage the message to log (supports <code>{}</code> placeholders).
     * @param params     the arguments used to format the log message.
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

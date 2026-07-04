/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.error;

import me.denarydev.crystal.random.StringGenerator;
import org.slf4j.Logger;

/**
 * Prefer using {@code PaperErrorLogger} when using {@code crystal-paper},
 * or {@code VelocityErrorLogger} when using {@code crystal-velocity}.
 * <p>
 * This class will be removed in the near future.
 */
@Deprecated(forRemoval = true)
public final class ErrorLogger {

    /**
     * Logs the given error to the console via the given logger, appending a generated
     * error code to the message, and returns that code.
     * <p>
     * The error code can then be sent to the player so that logs can be more easily
     * identified from a player's support report.
     *
     * @param logger  the logger to write the error to
     * @param message the error message
     * @param params  the message parameters (as in a standard SLF4J logger)
     * @return the error code that was logged
     */
    public static String logError(Logger logger, String message, Object... params) {
        final StringBuilder builder = new StringBuilder();
        builder.append(message);

        final String code = StringGenerator.generateRandomString(8);
        builder.append(" (error code: ").append(code).append(" )");

        logger.error(builder.toString(), params);

        return code;
    }
}

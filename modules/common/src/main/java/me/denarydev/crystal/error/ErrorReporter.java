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
 * A generic interface for handling and reporting errors in the Crystal ecosystem.
 * Provides a consistent format for notifying users and logging incidents.
 *
 * @param <T> the type of the notification recipient (e.g., Player in Paper or Velocity).
 */
public interface ErrorReporter<T> {

    /**
     * Reports an error and sends a notification to the given recipient.
     *
     * @param target the recipient to notify.
     * @param error  the exception that occurred.
     */
    void report(@NonNull T target, @NonNull Throwable error);

    /**
     * Reports an error with additional context and sends a notification to the given recipient.
     *
     * @param target     the recipient to notify.
     * @param error      the exception that occurred.
     * @param logMessage the message to log (supports <code>{}</code> placeholders).
     * @param params     the arguments used to format the log message.
     */
    void report(@NonNull T target, @NonNull Throwable error, @Nullable String logMessage, Object... params);
}

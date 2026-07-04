/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils.time;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * Utilities for formatting time durations into human-readable strings.
 */
public final class TimeFormatter {

    /**
     * Converts the given duration to a human-readable string.
     *
     * @param time   the time value.
     * @param unit   the time unit of the value.
     * @param day    the localized label for days.
     * @param hour   the localized label for hours.
     * @param minute the localized label for minutes.
     * @param second the localized label for seconds.
     * @return the formatted time string.
     */
    public static String timeToString(long time, @NonNull TimeUnit unit, @NonNull String day, @NonNull String hour, @NonNull String minute, @NonNull String second) {
        long millis = unit.toMillis(time);

        return millisToString(millis, day, hour, minute, second);
    }

    /**
     * Converts game ticks (1 tick = 50 ms) to a human-readable string.
     *
     * @param ticks  the number of ticks.
     * @param day    the localized label for days.
     * @param hour   the localized label for hours.
     * @param minute the localized label for minutes.
     * @param second the localized label for seconds.
     * @return the formatted time string.
     */
    public static String ticksToString(long ticks, @NonNull String day, @NonNull String hour, @NonNull String minute, @NonNull String second) {
        return millisToString(ticks * 50L, day, hour, minute, second);
    }

    /**
     * Converts milliseconds to a human-readable string.
     *
     * @param millis the number of milliseconds.
     * @param day    the localized label for days.
     * @param hour   the localized label for hours.
     * @param minute the localized label for minutes.
     * @param second the localized label for seconds.
     * @return the formatted time string.
     */
    public static String millisToString(long millis, @NonNull String day, @NonNull String hour, @NonNull String minute, @NonNull String second) {
        final StringBuilder builder = new StringBuilder();

        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        if (days > 0) {
            builder.append(days).append(" ").append(day);
        }

        if (hours > 0) {
            if (!builder.isEmpty()) builder.append(" ");
            builder.append(hours).append(" ").append(hour);
        }

        if (minutes > 0) {
            if (!builder.isEmpty()) builder.append(" ");
            builder.append(minutes).append(" ").append(minute);
        }

        if (seconds > 0) {
            if (!builder.isEmpty()) builder.append(" ");
            builder.append(seconds).append(" ").append(second);
        }

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            builder.append(0).append(" ").append(second);
        }

        return builder.toString();
    }
}

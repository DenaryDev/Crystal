/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import java.util.concurrent.TimeUnit;

public final class TimeUtils {

    public static String timeToStr(final long time, final TimeUnit unit, final String day, final String hour, final String minute, final String second) {
        final long millis = unit.toMillis(time);

        return toString(millis, day, hour, minute, second);
    }

    public static String ticksToStr(final long ticks, final String day, final String hour, final String minute, final String second) {
        return toString(ticks * 50L, day, hour, minute, second);
    }

    private static String toString(final long millis, final String day, final String hour, final String minute, final String second) {
        final StringBuilder builder = new StringBuilder();

        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        if (days > 0) {
            builder.append(days);
            builder.append(day);
        }

        if (hours > 0) {
            if (!builder.isEmpty()) builder.append(" ");
            builder.append(hours);
            builder.append(hour);
        }

        if (minutes > 0) {
            if (!builder.isEmpty()) builder.append(" ");
            builder.append(minutes);
            builder.append(minute);
        }

        if (seconds > 0) {
            if (!builder.isEmpty()) builder.append(" ");
            builder.append(seconds);
            builder.append(second);
        }

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            builder.append(0);
            builder.append(second);
        }

        return builder.toString();
    }
}

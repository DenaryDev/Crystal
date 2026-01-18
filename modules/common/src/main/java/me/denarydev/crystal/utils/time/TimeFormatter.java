/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils.time;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Утилиты для форматирования времени в текстовую строку.
 */
public final class TimeFormatter {

    /**
     * Преобразует заданный промежуток времени в читаемую строку.
     *
     * @param time   значение времени
     * @param unit   единица измерения времени
     * @param day    локализация для дней
     * @param hour   локализация для часов
     * @param minute локализация для минут
     * @param second локализация для секунд
     * @return отформатированная строка времени
     */
    public static String timeToString(long time, @NotNull TimeUnit unit, @NotNull String day, @NotNull String hour, @NotNull String minute, @NotNull String second) {
        long millis = unit.toMillis(time);

        return millisToString(millis, day, hour, minute, second);
    }

    /**
     * Преобразует игровые тики (1 тик = 50 мс) в читаемую строку.
     *
     * @param ticks  количество тиков
     * @param day    локализация для дней
     * @param hour   локализация для часов
     * @param minute локализация для минут
     * @param second локализация для секунд
     * @return отформатированная строка времени
     */
    public static String ticksToString(long ticks, @NotNull String day, @NotNull String hour, @NotNull String minute, @NotNull String second) {
        return millisToString(ticks * 50L, day, hour, minute, second);
    }

    /**
     * Преобразует миллисекунды в читаемую строку.
     *
     * @param millis количество миллисекунд
     * @param day    локализация для дней
     * @param hour   локализация для часов
     * @param minute локализация для минут
     * @param second локализация для секунд
     * @return отформатированная строка времени
     */
    public static String millisToString(long millis, @NotNull String day, @NotNull String hour, @NotNull String minute, @NotNull String second) {
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

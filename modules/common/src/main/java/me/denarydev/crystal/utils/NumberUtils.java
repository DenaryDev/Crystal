/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.jspecify.annotations.NonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Различные методы для работы с числами.
 */
public final class NumberUtils {
    private static final String NUMBER_REGEX = "[-+]?\\d*\\.?\\d+";

    /**
     * Форматирует число используя формат по умолчанию
     * в зависимости от типа числа.
     * Для целых чисел используется формат "#,###",
     * для дробных чисел используется формат "#,###.00".
     *
     * @param number число для форматирования
     * @return форматированное число в виде строки
     */
    @NonNull
    public static String formatNumber(final double number) {
        final DecimalFormat format = new DecimalFormat(number == Math.ceil(number) ? "#,###" : "#,###.00");

        return formatNumber(number, format);
    }

    /**
     * Форматирует число используя указанный формат.
     *
     * @param number число для форматирования
     * @param format формат {@link DecimalFormat}
     * @return форматированное число в виде строки
     */
    @NonNull
    public static String formatNumber(final double number, @NonNull final DecimalFormat format) {
        // This is done to specifically prevent the NBSP character from printing in foreign languages.
        final DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        format.setDecimalFormatSymbols(symbols);

        return format.format(number);
    }

    /**
     * Проверяет, является ли указанная строка целым числом.
     * <p>
     * Проверка происходит методом {@link Integer#parseInt(String)}
     *
     * @param string строка для проверки
     * @return true, если является, в ином случае false
     */
    public static boolean isInteger(@NonNull final String string) {
        try {
            Integer.parseInt(string);

            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    /**
     * Проверяет, является ли указанная строка числом.
     * <p>
     * Проверка происходит через регулярное выражение (Regex).
     *
     * @param string строка для проверки
     * @return true, если является, в ином случае false
     */
    public static boolean isNumeric(@NonNull final String string) {
        return string.matches(NUMBER_REGEX);
    }

    /**
     * Получает из указанного словаря случайное значение.
     * <p>
     * Ключ в словаре - шанс выпадения значения,
     * которое к нему привязано.
     * <p>
     * <b>Сумма шансов всегда должна быть равна 100</b>
     *
     * @param map словарь с шансами
     * @param <T> тип значения
     * @return случайное значение
     */
    @NonNull
    public static <T> T randomValue(@NonNull final Map<Integer, T> map) {
        final List<T> list = new ArrayList<>();

        for (Map.Entry<Integer, T> entry : map.entrySet()) {
            final int amount = entry.getKey() * 10;
            for (int i = 0; i < amount; i++) {
                list.add(entry.getValue());
            }
        }

        final int choice = ThreadLocalRandom.current().nextInt(list.size());

        return list.get(choice);
    }

    /**
     * Округляет число до заданного количества знаков после запятой.
     *
     * @param value  значение для округления
     * @param places до какого знака после запятой округлять
     * @return округлённое значение
     */
    public static double roundAvoid(final double value, final int places) {
        final double scale = Math.pow(10, places);

        return Math.round(value * scale) / scale;
    }
}

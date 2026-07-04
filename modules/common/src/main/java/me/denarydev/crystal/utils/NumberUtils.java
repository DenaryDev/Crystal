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
 * Utility methods for working with numbers.
 */
public final class NumberUtils {
    private static final String NUMBER_REGEX = "[-+]?\\d*\\.?\\d+";

    /**
     * Formats a number using a default pattern based on its type.
     * Whole numbers use the pattern {@code "#,###"};
     * fractional numbers use the pattern {@code "#,###.00"}.
     *
     * @param number the number to format.
     * @return the formatted number as a string.
     */
    @NonNull
    public static String formatNumber(final double number) {
        final DecimalFormat format = new DecimalFormat(number == Math.ceil(number) ? "#,###" : "#,###.00");

        return formatNumber(number, format);
    }

    /**
     * Formats a number using the given {@link DecimalFormat}.
     *
     * @param number the number to format.
     * @param format the decimal format to apply.
     * @return the formatted number as a string.
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
     * Returns whether the given string represents a valid integer.
     * <p>
     * Checked via {@link Integer#parseInt(String)}.
     *
     * @param string the string to check.
     * @return {@code true} if the string is a valid integer; {@code false} otherwise.
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
     * Returns whether the given string represents a valid number.
     * <p>
     * Checked via a regular expression.
     *
     * @param string the string to check.
     * @return {@code true} if the string is numeric; {@code false} otherwise.
     */
    public static boolean isNumeric(@NonNull final String string) {
        return string.matches(NUMBER_REGEX);
    }

    /**
     * Returns a random value from the given weighted map.
     * <p>
     * Each key in the map represents the percentage chance of the corresponding value being selected.
     * <p>
     * <b>The sum of all chance values must equal 100.</b>
     *
     * @param map the map of chances to values.
     * @param <T> the value type.
     * @return a randomly selected value.
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
     * Rounds a number to the given number of decimal places.
     *
     * @param value  the value to round.
     * @param places the number of decimal places to round to.
     * @return the rounded value.
     */
    public static double roundAvoid(final double value, final int places) {
        final double scale = Math.pow(10, places);

        return Math.round(value * scale) / scale;
    }
}

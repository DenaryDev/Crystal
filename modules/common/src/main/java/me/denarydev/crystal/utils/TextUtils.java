/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for working with strings.
 */
public final class TextUtils {

    /**
     * Capitalizes the first character of the given string.
     *
     * @param string the string to capitalize.
     * @return the string with its first character uppercased.
     */
    @NonNull
    public static String capitalize(@NonNull final String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Capitalizes the first character of each string in the given list.
     *
     * @param strings the list of strings to capitalize.
     * @return a new list with each string's first character uppercased.
     */
    @NonNull
    public static List<String> capitalizeAll(@NonNull final List<String> strings) {
        return strings.stream().map(TextUtils::capitalize).toList();
    }

    /**
     * Capitalizes the first character of each string in the given array.
     *
     * @param text the array of strings to capitalize.
     * @return a new array with each string's first character uppercased.
     */
    @NonNull
    public static String[] capitalizeAll(@NonNull final String... text) {
        return Arrays.stream(text).map(TextUtils::capitalize).toArray(String[]::new);
    }

    /**
     * Wraps the given text by breaking it into lines of at most {@code maxLength} characters,
     * splitting only at word boundaries.
     *
     * @param text      the source text.
     * @param maxLength the maximum line length in characters.
     * @return a list of wrapped lines.
     */
    @NonNull
    public static List<String> wrapText(@NonNull String text, int maxLength) {
        if (text.isEmpty()) return Collections.emptyList();

        final List<String> result = new ArrayList<>();

        final String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            if (currentLine.isEmpty() || (currentLine.length() + 1 + word.length()) <= maxLength) {
                if (!currentLine.isEmpty()) {
                    currentLine.append(' ');
                }

                currentLine.append(word);
            } else {
                result.add(currentLine.toString());

                currentLine = new StringBuilder(word);
            }
        }

        if (!currentLine.isEmpty()) {
            result.add(currentLine.toString());
        }

        return result;
    }

    /**
     * Picks the Russian plural form of a noun for the given number:
     * {@code one} for 1, 21, 31..., {@code few} for 2-4, 22-24..., {@code many} for 0, 5-20, 25-30...
     * <pre>{@code
     * TextUtils.pluralForm(3, "очко", "очка", "очков"); // "очка"
     * }</pre>
     *
     * @param number the number the noun follows; the sign is ignored.
     * @param one    the form for numbers ending in 1, except 11 ("очко").
     * @param few    the form for numbers ending in 2-4, except 12-14 ("очка").
     * @param many   the form for all other numbers ("очков").
     * @return one of the given forms.
     */
    @NonNull
    public static String pluralForm(long number, @NonNull final String one, @NonNull final String few, @NonNull final String many) {
        final long abs = Math.abs(number % 100);
        final long last = abs % 10;

        if (abs >= 11 && abs <= 14) return many;
        if (last == 1) return one;
        if (last >= 2 && last <= 4) return few;
        return many;
    }

    /**
     * Returns the number followed by a space and the Russian plural form of a noun picked by {@link #pluralForm}.
     * <pre>{@code
     * TextUtils.plural(5, "очко", "очка", "очков"); // "5 очков"
     * }</pre>
     *
     * @param number the number.
     * @param one    the form for numbers ending in 1, except 11.
     * @param few    the form for numbers ending in 2-4, except 12-14.
     * @param many   the form for all other numbers.
     * @return the number and the noun, e.g. {@code "21 очко"}.
     */
    @NonNull
    public static String plural(long number, @NonNull final String one, @NonNull final String few, @NonNull final String many) {
        return number + " " + pluralForm(number, one, few, many);
    }
}

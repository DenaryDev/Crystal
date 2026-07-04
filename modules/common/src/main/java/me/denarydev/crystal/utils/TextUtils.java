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
}

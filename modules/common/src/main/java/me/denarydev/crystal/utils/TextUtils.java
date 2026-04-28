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
 * Методы для работы со строками.
 */
public final class TextUtils {

    /**
     * Делает первый символ строки заглавным.
     *
     * @param string строка со строчной буквы
     * @return Строка с заглавной буквы
     */
    @NonNull
    public static String capitalize(@NonNull final String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Делает первый символ каждой строки в списке заглавным.
     *
     * @param strings список строк со строчной буквы
     * @return список Строк с заглавной буквы
     */
    @NonNull
    public static List<String> capitalizeAll(@NonNull final List<String> strings) {
        return strings.stream().map(TextUtils::capitalize).toList();
    }

    /**
     * Делает первый символ каждой строки в массиве заглавным.
     *
     * @param text массив строк со строчной буквы
     * @return массив Строк с заглавной буквы
     */
    @NonNull
    public static String[] capitalizeAll(@NonNull final String... text) {
        return Arrays.stream(text).map(TextUtils::capitalize).toArray(String[]::new);
    }

    /**
     * Переносит слишком длинный текст по словам с учётом указанной длинны строки.
     *
     * @param text      исходный текст
     * @param maxLength максимальная длина строки в символах
     * @return список строк
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

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.random;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации случайных строк
 */
public final class StringGenerator {
    private static final String DEFAULT_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Создаёт случайную строку указанной длины, которая может содержать
     * только заглавные и строчные английские символы, а так же цифры.
     *
     * @param length длина строки
     * @return случайная строка указанной длины
     */
    public static String generateRandomString(int length) {
        return generateRandomString(DEFAULT_CHARACTERS, length);
    }

    /**
     * Создаёт случайную строку указанной длины, которая может содержать
     * любые символы из указанной строки.
     *
     * @param length длина строки
     * @return случайная строка указанной длины
     */
    public static String generateRandomString(@NotNull String characters, int length) {
        if (length < 1) throw new IllegalArgumentException("Length must be positive");

        final ThreadLocalRandom random = ThreadLocalRandom.current();

        final StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            final int index = random.nextInt(characters.length());
            builder.append(characters.charAt(index));
        }

        return builder.toString();
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.random;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating random strings.
 */
public final class StringGenerator {
    private static final String DEFAULT_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Generates a random string of the given length using only uppercase letters,
     * lowercase letters, and digits.
     *
     * @param length the length of the string.
     * @return a random string of the given length.
     */
    public static String generateRandomString(int length) {
        return generateRandomString(DEFAULT_CHARACTERS, length);
    }

    /**
     * Generates a random string of the given length using characters drawn from the given character set.
     *
     * @param characters the pool of allowed characters.
     * @param length     the length of the string.
     * @return a random string of the given length.
     */
    public static String generateRandomString(@NonNull String characters, int length) {
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

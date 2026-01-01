/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author DenaryDev
 * @since 18:24 01.01.2026
 */
public final class Wildcards {

    /**
     * Проверяет строку на соответствие указанному "дикому" шаблону
     *
     * @param wildcard шаблон
     * @param string строка
     * @return true, если строка соответствует шаблону, в ином случае false
     */
    public static boolean matches(@NotNull String wildcard, @NotNull String string) {
        if (wildcard.isEmpty()) {
            return string.isEmpty();
        }

        if (wildcard.equals("*")) {
            return true;
        }

        return matchesRegex(wildcard, string);
    }

    private static boolean matchesRegex(@NotNull String wildcard, @NotNull String s) {
        final String regex = "\\Q" + wildcard
            .replace("\\E", "\\E\\\\E\\Q")
            .replace("?", "\\E.\\Q")
            .replace("*", "\\E.*\\Q") + "\\E";

        return Pattern.compile(regex).matcher(s).matches();
    }

    private static boolean matches(@NotNull String wildcard, @NotNull String s, int wcIdx, int sIdx) {
        for (; wcIdx < wildcard.length(); wcIdx++) {
            final char wcChar = wildcard.charAt(wcIdx);

            if (wcChar == '*') {
                wcIdx++;

                for (int checkIndex = sIdx; checkIndex < s.length(); checkIndex++) {
                    if (matches(wildcard, s, wcIdx, checkIndex)) {
                        return true;
                    }
                }

                return false;
            } else {
                if (sIdx >= s.length()) {
                    return false;
                }

                if (wcChar != '?' && wcChar != s.charAt(sIdx)) {
                    return false;
                }

                sIdx++;
            }
        }

        return sIdx >= s.length() || wildcard.charAt(wildcard.length() - 1) == '*';
    }
}

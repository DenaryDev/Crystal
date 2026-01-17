/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Внутренний класс, не используйте напрямую.
 *
 * @author DenaryDev
 * @since 2:17 17.01.2026
 */
@ApiStatus.Internal
public final class SQLUtil {

    /**
     * Преобразует переданный объект в строку, подходящую для использования в SQL-запросах.
     *
     * @param value значение
     * @return строка, отформатированная для SQL.
     */
    public static String valueToSqlString(@NotNull Object value) {
        if (value instanceof Boolean bool) {
            return bool ? "1" : "0";
        } else if (value instanceof Number num) {
            return num.toString();
        } else { // Strings, Characters and other types just converts to string
            return "'" + value.toString().replace("'", "''") + "'";
        }
    }

    /**
     * Проверяет, что указанная строка не пуста и не содержит символов <code>`</code>.
     *
     * @param s Строка для проверки.
     */
    public static void validateIdentifier(@NotNull String s) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException("An empty string can't be an identifier");
        }

        if (s.contains("`")) {
            throw new IllegalArgumentException("Invalid identifier \"" + s + "\"");
        }
    }

    /**
     * Проверяет, что количество символов <code>?</code> в строке совпадает с длиной массива значений.
     *
     * @param expr   Строка для проверки.
     * @param values Массив значений.
     */
    public static void validatePlaceholderCount(@NotNull String expr, @NotNull Object[] values) {
        int count = placeholderCount(expr);

        if (count != values.length) {
            throw new IllegalArgumentException("Expected " + values.length + " placeholders, got " + count + " in expression \"" + expr + "\"");
        }
    }

    private static int placeholderCount(@NotNull String expr) {
        int count = 0;

        for (int i = 0; i < expr.length(); i++) {
            if (expr.charAt(i) == '?') {
                count++;
            }
        }

        return count;
    }
}

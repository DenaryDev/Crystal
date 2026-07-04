/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.util;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

/**
 * Internal class; do not use directly.
 */
@ApiStatus.Internal
public final class SQLUtil {

    /**
     * Converts the given object to a string suitable for use in SQL queries.
     *
     * @param value the value.
     * @return the SQL-formatted string.
     */
    public static String valueToSqlString(@NonNull Object value) {
        if (value instanceof Boolean bool) {
            return bool ? "1" : "0";
        } else if (value instanceof Number num) {
            return num.toString();
        } else { // Strings, Characters and other types just converts to string
            return "'" + value.toString().replace("'", "''") + "'";
        }
    }

    /**
     * Validates that the given string is not empty and does not contain <code>`</code> characters.
     *
     * @param s the string to validate.
     */
    public static void validateIdentifier(@NonNull String s) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException("An empty string can't be an identifier");
        }

        if (s.contains("`")) {
            throw new IllegalArgumentException("Invalid identifier \"" + s + "\"");
        }
    }

    /**
     * Validates that the number of <code>?</code> placeholder characters in the expression matches the
     * length of the values array.
     *
     * @param expr   the expression to check.
     * @param values the values array.
     */
    public static void validatePlaceholderCount(@NonNull String expr, @NonNull Object[] values) {
        int count = placeholderCount(expr);

        if (count != values.length) {
            throw new IllegalArgumentException("Expected " + values.length + " placeholders, got " + count + " in expression \"" + expr + "\"");
        }
    }

    private static int placeholderCount(@NonNull String expr) {
        int count = 0;

        for (int i = 0; i < expr.length(); i++) {
            if (expr.charAt(i) == '?') {
                count++;
            }
        }

        return count;
    }
}

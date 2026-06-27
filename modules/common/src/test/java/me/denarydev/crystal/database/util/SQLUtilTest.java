/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SQLUtilTest {

    // --- valueToSqlString ---

    @Test
    void booleanTrue() {
        assertEquals("1", SQLUtil.valueToSqlString(true));
    }

    @Test
    void booleanFalse() {
        assertEquals("0", SQLUtil.valueToSqlString(false));
    }

    @Test
    void integerValue() {
        assertEquals("42", SQLUtil.valueToSqlString(42));
    }

    @Test
    void longValue() {
        assertEquals("1000000", SQLUtil.valueToSqlString(1_000_000L));
    }

    @Test
    void doubleValue() {
        assertEquals("3.14", SQLUtil.valueToSqlString(3.14));
    }

    @Test
    void stringValue() {
        assertEquals("'hello'", SQLUtil.valueToSqlString("hello"));
    }

    @Test
    void stringWithSingleQuote() {
        assertEquals("'it''s'", SQLUtil.valueToSqlString("it's"));
    }

    // --- validateIdentifier ---

    @Test
    void validIdentifier() {
        SQLUtil.validateIdentifier("column_name"); // must not throw
    }

    @Test
    void emptyIdentifierThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SQLUtil.validateIdentifier("")
        );
    }

    @Test
    void backtickInIdentifierThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SQLUtil.validateIdentifier("col`name")
        );
    }

    // --- validatePlaceholderCount ---

    @Test
    void matchingPlaceholders() {
        SQLUtil.validatePlaceholderCount("? AND ?", new Object[]{1, 2}); // must not throw
    }

    @Test
    void tooFewPlaceholders() {
        assertThrows(IllegalArgumentException.class, () ->
            SQLUtil.validatePlaceholderCount("?", new Object[]{1, 2})
        );
    }

    @Test
    void tooManyPlaceholders() {
        assertThrows(IllegalArgumentException.class, () ->
            SQLUtil.validatePlaceholderCount("? AND ?", new Object[]{1})
        );
    }

    @Test
    void noPlaceholdersNoValues() {
        SQLUtil.validatePlaceholderCount("NOW()", new Object[]{}); // must not throw
    }
}

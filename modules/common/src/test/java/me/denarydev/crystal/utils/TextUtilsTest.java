/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextUtilsTest {

    @ParameterizedTest
    @CsvSource({
        "0, очков", "1, очко", "2, очка", "4, очка", "5, очков", "10, очков",
        "11, очков", "12, очков", "14, очков", "15, очков", "20, очков",
        "21, очко", "22, очка", "25, очков", "101, очко", "111, очков", "112, очков", "1001, очко",
        "-1, очко", "-3, очка", "-11, очков"
    })
    void pluralForm(long number, String expected) {
        assertEquals(expected, TextUtils.pluralForm(number, "очко", "очка", "очков"));
    }

    @ParameterizedTest
    @CsvSource({"1, 1 очко", "3, 3 очка", "7, 7 очков", "-21, -21 очко"})
    void plural(long number, String expected) {
        assertEquals(expected, TextUtils.plural(number, "очко", "очка", "очков"));
    }
}

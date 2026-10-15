/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SlotsTest {

    @Test
    public void at_isZeroBasedRowMajor() {
        assertEquals(0, Slots.at(0, 0));
        assertEquals(8, Slots.at(0, 8));
        assertEquals(9, Slots.at(1, 0));
        assertEquals(22, Slots.at(2, 4));
        assertEquals(53, Slots.at(5, 8));
    }

    @Test
    public void at_customWidth() {
        assertEquals(4, Slots.at(1, 1, 3));
        assertEquals(8, Slots.at(2, 2, 3));
    }

    @Test
    public void at_rejectsOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> Slots.at(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> Slots.at(0, -1));
        assertThrows(IllegalArgumentException.class, () -> Slots.at(0, 9));
        assertThrows(IllegalArgumentException.class, () -> Slots.at(0, 3, 3));
        assertThrows(IllegalArgumentException.class, () -> Slots.at(0, 0, 0));
    }

    @Test
    public void rowOfAndColumnOf_invertAt() {
        for (int slot = 0; slot < 54; slot++) {
            assertEquals(slot, Slots.at(Slots.rowOf(slot), Slots.columnOf(slot)));
        }
    }

    @Test
    public void row() {
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, Slots.row(0));
        assertArrayEquals(new int[] {18, 19, 20, 21, 22, 23, 24, 25, 26}, Slots.row(2));
    }

    @Test
    public void column() {
        assertArrayEquals(new int[] {0, 9, 18}, Slots.column(0, 27));
        assertArrayEquals(new int[] {8, 17, 26, 35, 44, 53}, Slots.column(8, 54));
        assertThrows(IllegalArgumentException.class, () -> Slots.column(9, 27));
        assertThrows(IllegalArgumentException.class, () -> Slots.column(0, 10));
    }

    @Test
    public void range() {
        assertArrayEquals(new int[] {3, 4, 5}, Slots.range(3, 5));
        assertArrayEquals(new int[] {7}, Slots.range(7, 7));
        assertThrows(IllegalArgumentException.class, () -> Slots.range(5, 3));
        assertThrows(IllegalArgumentException.class, () -> Slots.range(-1, 3));
    }

    @Test
    public void all() {
        assertEquals(9, Slots.all(9).length);
        assertEquals(54, Slots.all(54).length);
        assertEquals(0, Slots.all(27)[0]);
        assertEquals(26, Slots.all(27)[26]);
        assertThrows(IllegalArgumentException.class, () -> Slots.all(63));
        assertThrows(IllegalArgumentException.class, () -> Slots.all(0));
    }

    @Test
    public void border_threeRows() {
        assertArrayEquals(
            new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26},
            Slots.border(27));
    }

    @Test
    public void border_singleRowIsEverything() {
        assertArrayEquals(Slots.all(9), Slots.border(9));
        assertArrayEquals(Slots.all(18), Slots.border(18));
    }

    @Test
    public void inner_threeRows() {
        assertArrayEquals(new int[] {10, 11, 12, 13, 14, 15, 16}, Slots.inner(27));
    }

    @Test
    public void inner_singleRowIsEmpty() {
        assertArrayEquals(new int[0], Slots.inner(9));
        assertArrayEquals(new int[0], Slots.inner(18));
    }

    @Test
    public void borderAndInner_partitionInventory() {
        for (int size = 9; size <= 54; size += 9) {
            assertEquals(size, Slots.border(size).length + Slots.inner(size).length);
        }
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui;

import com.google.common.base.Preconditions;

import java.util.stream.IntStream;

/**
 * Helpers for computing inventory slot indices from row and column coordinates.
 * <p>
 * All coordinates are zero-based: the top-left slot of a menu is {@code at(0, 0)},
 * matching the slot indices used by Bukkit. Unless a width is given, methods assume
 * a chest-style inventory that is {@value #CHEST_WIDTH} slots wide.
 * <p>
 * Every method returns plain slot indices, so the results can be passed straight to
 * {@link Menu#addItem(org.bukkit.inventory.ItemStack, int...)} and friends.
 */
public final class Slots {

    /**
     * The width of a chest-style inventory, in slots.
     */
    public static final int CHEST_WIDTH = 9;

    private Slots() {
    }

    /**
     * Returns the slot index at the given row and column of a chest-style inventory.
     *
     * @param row    the zero-based row.
     * @param column the zero-based column, {@code 0} to {@code 8}.
     * @return the slot index.
     */
    public static int at(int row, int column) {
        return at(row, column, CHEST_WIDTH);
    }

    /**
     * Returns the slot index at the given row and column of an inventory with the given width.
     *
     * @param row    the zero-based row.
     * @param column the zero-based column, {@code 0} to {@code width - 1}.
     * @param width  the inventory width in slots.
     * @return the slot index.
     */
    public static int at(int row, int column, int width) {
        Preconditions.checkArgument(width > 0, "width must be positive");
        Preconditions.checkArgument(row >= 0, "row must not be negative");
        Preconditions.checkArgument(column >= 0 && column < width, "column must be between 0 and %s", width - 1);

        return row * width + column;
    }

    /**
     * Returns the row of the given slot in a chest-style inventory.
     *
     * @param slot the slot index.
     * @return the zero-based row.
     */
    public static int rowOf(int slot) {
        Preconditions.checkArgument(slot >= 0, "slot must not be negative");

        return slot / CHEST_WIDTH;
    }

    /**
     * Returns the column of the given slot in a chest-style inventory.
     *
     * @param slot the slot index.
     * @return the zero-based column.
     */
    public static int columnOf(int slot) {
        Preconditions.checkArgument(slot >= 0, "slot must not be negative");

        return slot % CHEST_WIDTH;
    }

    /**
     * Returns all slots of the given row in a chest-style inventory.
     *
     * @param row the zero-based row.
     * @return the slot indices of the row, left to right.
     */
    public static int[] row(int row) {
        Preconditions.checkArgument(row >= 0, "row must not be negative");

        return range(row * CHEST_WIDTH, row * CHEST_WIDTH + CHEST_WIDTH - 1);
    }

    /**
     * Returns all slots of the given column in a chest-style inventory of the given size.
     *
     * @param column the zero-based column, {@code 0} to {@code 8}.
     * @param size   the inventory size, a multiple of 9.
     * @return the slot indices of the column, top to bottom.
     */
    public static int[] column(int column, int size) {
        checkChestSize(size);
        Preconditions.checkArgument(column >= 0 && column < CHEST_WIDTH, "column must be between 0 and %s", CHEST_WIDTH - 1);

        return IntStream.range(0, size / CHEST_WIDTH)
            .map(row -> row * CHEST_WIDTH + column)
            .toArray();
    }

    /**
     * Returns every slot from {@code from} to {@code to}, both inclusive.
     *
     * @param from the first slot.
     * @param to   the last slot.
     * @return the slot indices in ascending order.
     */
    public static int[] range(int from, int to) {
        Preconditions.checkArgument(from >= 0, "from must not be negative");
        Preconditions.checkArgument(to >= from, "to must not be less than from");

        return IntStream.rangeClosed(from, to).toArray();
    }

    /**
     * Returns every slot of a chest-style inventory of the given size.
     *
     * @param size the inventory size, a multiple of 9.
     * @return all slot indices in ascending order.
     */
    public static int[] all(int size) {
        checkChestSize(size);

        return range(0, size - 1);
    }

    /**
     * Returns the slots along the outer edge of a chest-style inventory of the given size:
     * the top and bottom rows plus the leftmost and rightmost columns.
     * <p>
     * For inventories of one or two rows this is every slot.
     *
     * @param size the inventory size, a multiple of 9.
     * @return the border slot indices in ascending order.
     */
    public static int[] border(int size) {
        checkChestSize(size);

        final int rows = size / CHEST_WIDTH;

        return IntStream.range(0, size)
            .filter(slot -> {
                final int row = slot / CHEST_WIDTH;
                final int column = slot % CHEST_WIDTH;

                return row == 0 || row == rows - 1 || column == 0 || column == CHEST_WIDTH - 1;
            })
            .toArray();
    }

    /**
     * Returns the slots strictly inside the border of a chest-style inventory of the given size.
     * <p>
     * For inventories of one or two rows this is empty.
     *
     * @param size the inventory size, a multiple of 9.
     * @return the inner slot indices in ascending order.
     */
    public static int[] inner(int size) {
        checkChestSize(size);

        final int rows = size / CHEST_WIDTH;

        return IntStream.range(0, size)
            .filter(slot -> {
                final int row = slot / CHEST_WIDTH;
                final int column = slot % CHEST_WIDTH;

                return row > 0 && row < rows - 1 && column > 0 && column < CHEST_WIDTH - 1;
            })
            .toArray();
    }

    private static void checkChestSize(int size) {
        Preconditions.checkArgument(size % CHEST_WIDTH == 0, "size must be multiple of 9");
        Preconditions.checkArgument(size >= CHEST_WIDTH && size <= 54, "size must be between 9 and 54");
    }
}

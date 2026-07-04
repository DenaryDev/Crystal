/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui.template;

import com.google.common.base.Preconditions;
import me.denarydev.crystal.paper.gui.Template;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A menu template that defines the layout via a character matrix.
 * <p>
 * Lets you visually design the interface using a text mask (rows of 9 characters),
 * where each character maps to a specific item.
 * <p>
 * Well-suited for borders, patterns, and complex inventory layouts.
 */
@ApiStatus.Experimental
public final class MatrixTemplate extends Template {

    private final List<String> matrix;
    private final Map<Character, ItemStack> elements;

    /**
     * Creates a new builder for a matrix template.
     * Allows defining the menu layout using a character mask.
     *
     * @return a {@link MatrixTemplate.Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    private MatrixTemplate(@Nullable Component title, long cooldown, List<String> matrix, Map<Character, ItemStack> elements) {
        super(title, matrix.size() * 9, parseItems(matrix, elements), cooldown);
        this.matrix = matrix;
        this.elements = elements;
    }

    /**
     * Returns the character matrix defined in this template.
     *
     * @return the matrix as an immutable list of row strings.
     */
    @NonNull
    public List<String> matrix() {
        return Collections.unmodifiableList(matrix);
    }

    /**
     * Returns the character-to-item mappings (matrix elements).
     *
     * @return an immutable map of characters to items.
     */
    @NonNull
    public Map<Character, ItemStack> elements() {
        return Collections.unmodifiableMap(elements);
    }

    /**
     * Returns all slot indices where the given character appears in the matrix.
     *
     * @param character the character to look up.
     * @return an array of matching slot indices.
     */
    public int[] slotsByCharacter(char character) {
        final List<Integer> slots = new ArrayList<>();

        int slot = 0;
        for (final String row : matrix) {
            final char[] chars = row.toCharArray();

            for (char ch : chars) {
                if (ch == character) {
                    slots.add(slot);
                }

                slot++;
            }
        }

        return slots.stream().mapToInt(Integer::intValue).toArray();
    }

    private static Map<Integer, ItemStack> parseItems(List<String> matrix, Map<Character, ItemStack> elements) {
        final Map<Integer, ItemStack> items = new LinkedHashMap<>();

        int slot = 0;
        for (String row : matrix) {
            final char[] chars = row.toCharArray();

            for (char ch : chars) {
                final ItemStack item = elements.get(ch);

                if (item != null) {
                    items.put(slot, item);
                }

                slot++;
            }
        }

        return items;
    }

    @ApiStatus.Experimental
    public static final class Builder extends Template.Builder<Builder, MatrixTemplate> {
        private List<String> matrix;
        private final Map<Character, ItemStack> elements = new LinkedHashMap<>();

        private Builder() {
        }

        /**
         * Returns the matrix currently set in this builder, or {@code null} if not yet set.
         *
         * @return the matrix rows, or {@code null}.
         */
        @Nullable
        public List<String> matrix() {
            return matrix;
        }

        /**
         * Sets the matrix from a newline-delimited string.
         *
         * @param matrix the matrix string with rows separated by {@code \n}.
         */
        public Builder matrix(@NonNull String matrix) {
            final List<String> rows = List.of(matrix.split("\n"));

            return this.matrix(rows);
        }

        /**
         * Sets the matrix from a list of rows.
         * <p>
         * The matrix must have between 1 and 6 rows,
         * and each row must be exactly 9 characters long.
         *
         * @param matrix the list of row strings.
         */
        public Builder matrix(@NonNull List<String> matrix) {
            Preconditions.checkArgument(!matrix.isEmpty(), "matrix cannot be empty");
            Preconditions.checkArgument(matrix.size() <= 6, "matrix cannot contain more than 6 rows");
            final boolean validateRows = matrix.stream().noneMatch(row -> row.length() != 9);
            Preconditions.checkArgument(validateRows, "each row of the matrix must be 9 characters long");

            this.matrix = Collections.unmodifiableList(matrix);

            return this;
        }

        /**
         * Returns all character-to-item mappings currently configured in this builder.
         *
         * @return the element map.
         */
        @NonNull
        public Map<Character, ItemStack> elements() {
            return elements;
        }

        /**
         * Maps an item to a matrix character.
         * <p>
         * Overwrites any item already mapped to the given character.
         *
         * @param character the character.
         * @param item      the item.
         */
        public Builder element(char character, @NonNull ItemStack item) {
            this.elements.put(character, item);

            return this;
        }

        /**
         * Adds all the given character-to-item mappings to this builder.
         * <p>
         * Overwrites any existing mappings for conflicting characters.
         *
         * @param elements the mappings to add.
         */
        public Builder elements(Map<Character, ItemStack> elements) {
            if (!elements.isEmpty()) {
                this.elements.putAll(elements);
            }

            return this;
        }

        /**
         * Builds and returns a {@link MatrixTemplate} from the configured parameters.
         *
         * @return the created template.
         */
        @Override
        public MatrixTemplate build() {
            Preconditions.checkNotNull(matrix, "matrix not specified");
            Preconditions.checkArgument(!elements.isEmpty(), "matrix elements is empty");

            return new MatrixTemplate(title, cooldown, matrix, elements);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}

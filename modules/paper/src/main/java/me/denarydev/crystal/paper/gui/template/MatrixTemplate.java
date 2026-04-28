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
 * Реализация шаблона меню на основе символьной матрицы.
 * <p>
 * Позволяет визуально проектировать интерфейс, используя текстовую маску (строки по 9 символов),
 * где каждый символ сопоставляется с определенным предметом.
 * <p>
 * Идеально подходит для создания рамок, паттернов и сложных макетов инвентаря.
 */
@ApiStatus.Experimental
public final class MatrixTemplate extends Template {

    private final List<String> matrix;
    private final Map<Character, ItemStack> elements;

    /**
     * Создает новый билдер для настройки матричного шаблона.
     * Позволяет задавать структуру меню с помощью символьной маски.
     *
     * @return экземпляр {@link MatrixTemplate.Builder}
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
     * Возвращает матрицу, указанную в этом шаблоне
     *
     * @return матрица в виде списка строк
     */
    @NonNull
    public List<String> matrix() {
        return Collections.unmodifiableList(matrix);
    }

    /**
     * Возвращает все привязки предметов к символам матрицы (элементы матрицы).
     *
     * @return элементы матрицы
     */
    @NonNull
    public Map<Character, ItemStack> elements() {
        return Collections.unmodifiableMap(elements);
    }

    /**
     * Возвращает массив всех слотов, в которых находится указанный символ.
     *
     * @param character символ
     * @return массив слотов
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
         * Возвращает матрицу, указанную в этом создателе
         *
         * @return матрица в виде списка строк, если указана, иначе null
         */
        @Nullable
        public List<String> matrix() {
            return matrix;
        }

        /**
         * Устанавливает матрицу из текста, разделённого на строки
         * символом новой строки (\n)
         *
         * @param matrix матрица
         */
        public Builder matrix(@NonNull String matrix) {
            final List<String> rows = List.of(matrix.split("\n"));

            return this.matrix(rows);
        }

        /**
         * Устанавливает матрицу в виде списка строк к шаблону.
         * <p>
         * Матрица должна содержать от 1 до 6 строк,
         * и каждая строка матрицы должна быть длиной строго 9 символов.
         *
         * @param matrix матрица
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
         * Возвращает все элементы матрицы.
         *
         * @return элементы матрицы
         */
        @NonNull
        public Map<Character, ItemStack> elements() {
            return elements;
        }

        /**
         * Привязывает предмет к символу матрицы.
         * <p>
         * Если к указанному символу уже привязан предмет, перезаписывает его.
         *
         * @param character символ
         * @param item      предмет
         */
        public Builder element(char character, @NonNull ItemStack item) {
            this.elements.put(character, item);

            return this;
        }

        /**
         * Добавляет все указанные привязки предметов к символам матрицы.
         * <p>
         * Если к какому-либо символу уже есть привязанный предмет, он перезапишется.
         *
         * @param elements элементы
         */
        public Builder elements(Map<Character, ItemStack> elements) {
            if (!elements.isEmpty()) {
                this.elements.putAll(elements);
            }

            return this;
        }

        /**
         * Собирает указанные параметры "до кучи" и
         * создаёт из них шаблон.
         *
         * @return созданный шаблон
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

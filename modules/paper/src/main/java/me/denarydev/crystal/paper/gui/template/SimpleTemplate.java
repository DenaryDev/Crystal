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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Реализация шаблона меню на основе прямого распределения слотов.
 * <p>
 * Позволяет точно настраивать размер инвентаря, тип контейнера (например, HOPPER или DISPENSER)
 * и привязывать предметы к конкретным числовым индексам слотов.
 * <p>
 * Оптимален для создания стандартных сундуков или меню с фиксированной структурой элементов.
 */
public final class SimpleTemplate extends Template {

    @Nullable
    private final InventoryType type;

    /**
     * Создаёт новый билдер для настройки простого шаблона.
     * Позволяет создать меню с заданным типом, разместить предметы в конкретных
     * слотах и настроить заголовок меню и кулдаун кликов по предметам в нём.
     *
     * @return экземпляр {@link SimpleTemplate.Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    private SimpleTemplate(@Nullable Component title, int size, Map<Integer, ItemStack> items, long cooldown, @Nullable InventoryType type) {
        super(title, size, items, cooldown);
        this.type = type;
    }

    /**
     * Проверяет, указан ли в шаблоне тип меню.
     *
     * @return true, если тип указан, иначе false
     */
    public boolean hasType() {
        return type != null;
    }

    /**
     * Возвращает тип инвентаря, если он был указан.
     *
     * @return тип меню или null, если не указан
     */
    @Nullable
    public InventoryType type() {
        return type;
    }

    public static final class Builder extends Template.Builder<Builder, SimpleTemplate> {
        private int size;
        private InventoryType type;
        private final Map<Integer, ItemStack> items = new LinkedHashMap<>();

        private Builder() {
        }

        /**
         * Возвращает размер меню в количестве слотов.
         *
         * @return размер меню, или 0, если не указано
         */
        public int size() {
            return size;
        }

        /**
         * Устанавливает размер меню в количестве слотов.
         * <p>
         * Указанное значение должно быть кратно 9 и в диапазоне от 9 до 54!
         * <p>
         * Игнорируется, если указан тип меню через {@link Builder#type(InventoryType)}!
         *
         * @param size кол-во слотов
         */
        public Builder size(int size) {
            Preconditions.checkArgument(size % 9 == 0, "size must be multiple of 9");
            Preconditions.checkArgument(size >= 9 && size <= 54, "size must be between 9 and 54");

            this.size = size;

            return this;
        }

        /**
         * Возвращает тип инвентаря, если установлен.
         *
         * @return тип меню или null, если не установлен
         */
        @Nullable
        public InventoryType type() {
            return type;
        }

        /**
         * Устанавливает тип меню.
         * <p>
         * Установка типа меню игнорирует указанный методом {@link Builder#size(int)} размер меню.
         *
         * @param type тип меню
         * @see InventoryType
         */
        public Builder type(@NonNull InventoryType type) {
            this.type = type;

            return this;
        }

        /**
         * Возвращает все указанные предметы, привязанные к слотам.
         *
         * @return все указанные в этом создателе предметы.
         */
        public Map<Integer, ItemStack> items() {
            return items;
        }

        /**
         * Добавляет предмет в указанные слоты.
         * <p>
         * Если в каком-то из указанных слотов уже есть предмет,
         * перезаписывает его.
         *
         * @param item  предмет
         * @param slots слот или несколько слотов
         */
        public Builder item(@NonNull ItemStack item, int... slots) {
            Preconditions.checkArgument(slots.length > 0, "you must specify at least one slot");

            for (int slot : slots) {
                items.put(slot, item);
            }

            return this;
        }

        /**
         * Добавляет все указанные предметы в этот создатель шаблона.
         *
         * @param items предметы
         */
        public Builder items(Map<Integer, ItemStack> items) {
            this.items.putAll(items);

            return this;
        }

        /**
         * Собирает указанные параметры "до кучи" и
         * создаёт из них шаблон.
         *
         * @return созданный шаблон
         */
        @Override
        public SimpleTemplate build() {
            return new SimpleTemplate(title, size, items, cooldown, type);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}

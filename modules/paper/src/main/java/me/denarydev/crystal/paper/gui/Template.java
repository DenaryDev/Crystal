/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Шаблон меню.
 * <p>
 * Для создания шаблона инициализируйте билдер, используя {@link Template#builder()}
 * <p>
 * Шаблоны нужны для того, чтобы можно было хранить
 * параметры меню и не доставать их каждый раз из конфига
 * при отображении меню игроку.
 */
public class Template {
    private final Component title;
    private final int size;
    private final InventoryType type;
    private final Map<Integer, ItemStack> items;
    private final long cooldown;

    @ApiStatus.Internal
    Template(@Nullable Component title, int size, @Nullable InventoryType type, @NotNull Map<Integer, ItemStack> items, long cooldown) {
        this.title = title;
        this.size = size;
        this.type = type;
        this.items = items;
        this.cooldown = cooldown;
    }

    /**
     * Возвращает заголовок меню, если он был указан.
     *
     * @return заголовок меню или null, если не указан
     */
    @Nullable
    public Component title() {
        return title;
    }

    /**
     * Возвращает кол-во слотов в меню.
     *
     * @return кол-во слотов в меню
     */
    public int size() {
        return size;
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

    /**
     * Возвращает словарь с указанием предметов в слотах.
     *
     * @return словарь предметов
     */
    public Map<Integer, ItemStack> items() {
        return items;
    }

    /**
     * Возвращает задержку между обработкой кликов.
     *
     * @return задержка обработки кликов
     */
    public long cooldown() {
        return cooldown;
    }

    /**
     * Запускает создатель шаблона.
     *
     * @return {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Создатель шаблонов
     */
    public static class Builder {
        private Component title;
        private int size;
        private InventoryType type;
        private final Map<Integer, ItemStack> items = new HashMap<>();
        private long cooldown;

        /**
         * Устанавливает заголовок меню из компонента MiniMessage.
         * <p>
         * Если указать null, заголовок будет сброшен!
         *
         * @param title заголовок
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder title(@Nullable final Component title) {
            this.title = title;

            return this;
        }

        /**
         * Устанавливает заголовок меню из строки, применяя к ней
         * форматирование MiniMessage и указанные плейсхолдеры.
         *
         * @param title     заголовок
         * @param resolvers плейсхолдеры
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder titleRich(@NotNull final String title, @NotNull final TagResolver... resolvers) {
            this.title = MiniMessage.miniMessage().deserialize(title, resolvers);

            return this;
        }

        /**
         * Устанавливает заголовок меню из строки без форматирования.
         *
         * @param title заголовок
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder titlePlain(@NotNull final String title) {
            this.title = PlainTextComponentSerializer.plainText().deserialize(title);

            return this;
        }

        /**
         * Устанавливает размер меню в количестве слотов.
         * <p>
         * <b><u>Указанное значение должно быть кратно 9 и в диапазоне от 9 до 54!</u></b>
         *
         * @param size кол-во слотов
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder size(final int size) {
            Preconditions.checkArgument(size % 9 == 0, "Size must be multiple of 9!");
            Preconditions.checkArgument(size >= 9 && size <= 54, "Size must be between 9 and 54!");

            this.size = size;

            return this;
        }

        /**
         * Устанавливает тип меню.
         *
         * @param type тип меню
         * @return текущий {@link Builder} для продолжения создания
         * @see InventoryType
         */
        public Builder type(@NotNull final InventoryType type) {
            this.type = type;

            return this;
        }

        /**
         * Добавляет предмет в указанные слоты.
         * <p>
         * Если в каком-то из указанных слотов уже есть предмет,
         * перезаписывает его.
         *
         * @param item  предмет
         * @param slots слот или несколько слотов
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder item(@NotNull final ItemStack item, final int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");

            for (final int slot : slots) {
                items.put(slot, item);
            }

            return this;
        }

        /**
         * Устанавливает задержку между обработкой кликов.
         *
         * @param cooldown задержка
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder cooldown(final long cooldown) {
            Preconditions.checkArgument(cooldown >= 0, "Cooldown must be positive!");

            this.cooldown = cooldown;

            return this;
        }

        /**
         * Собирает указанные параметры "до кучи" и
         * создаёт из них шаблон.
         *
         * @return созданный шаблон
         */
        public Template build() {
            return new Template(title, size, type, items, cooldown);
        }
    }
}

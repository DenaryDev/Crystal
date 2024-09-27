/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.gui;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Для создания экземпляра данного класса вы должны
 * использовать создатель {@link Builder}.
 */
public class GUI implements InventoryHolder {
    private final Template template;
    private final Inventory inventory;
    @Nullable
    private final CloseAction closeAction;
    private final Map<Integer, ClickAction> clickActions = new HashMap<>();

    private Player viewer;

    @ApiStatus.Internal
    GUI(final Template template, final Map<Integer, ClickAction> actions, @Nullable final CloseAction closeAction) {
        this.template = template;
        this.closeAction = closeAction;
        this.inventory = setupBukkitInventory(template);
        template.items().forEach((slot, item) -> addItemInternal(item, slot));
        actions.forEach((slot, action) -> addActionInternal(action, slot));
    }

    /**
     * Запускает создатель меню без шаблона.
     *
     * @return {@link Builder} без параметров
     */
    public static Builder builder() {
        return new Builder(null);
    }

    /**
     * Запускает создатель меню по указанному шаблону.
     *
     * @param template шаблон для билдера
     * @return {@link Builder} на основе шаблона.
     */
    public static Builder builder(@NotNull final Template template) {
        return new Builder(template);
    }

    /**
     * Возвращает шаблон меню.
     * <p>
     * Если вы создавали меню без шаблона, то вернёт
     * шаблон, созданный из указанных вами параметров.
     *
     * @return шаблон меню
     */
    @NotNull
    public Template template() {
        return template;
    }

    /**
     * Возвращает игрока, которому открыто меню, если он есть,
     * или null, если меню никому не открыто.
     *
     * @return игрок, которому открыто меню, или null, если меню не открыто
     */
    @Nullable
    public Player viewer() {
        return viewer;
    }

    /**
     * Проверяет, есть ли предмет в указанном слоте.
     *
     * @param slot слот для проверки
     * @return true, если в слоте есть предмет, иначе false
     */
    public boolean hasItem(final int slot) {
        return inventory.getItem(slot) != null;
    }

    /**
     * Добавляет предмет в указанные слоты.
     * <p>
     * <b>Если меню уже отображается игроку, для применения
     * изменений нужно обновить его, вызвав метод {@link #update()}.</b>
     *
     * @param item  предмет
     * @param slots слот или несколько слотов
     */
    public void addItem(@NotNull final ItemStack item, final int... slots) {
        addItemInternal(item, slots);
    }

    /**
     * Добавляет предмет и действие к нему в указанные слоты.
     * <p>
     * <b>Если меню уже отображается игроку, для применения
     * изменений нужно обновить его, вызвав метод {@link #update()}.</b>
     *
     * @param item   предмет
     * @param action действие
     * @param slots  слот или несколько слотов
     */
    public void addItem(@NotNull final ItemStack item, @Nullable final ClickAction action, final int... slots) {
        addItemInternal(item, slots);
        addActionInternal(action, slots);
    }

    /**
     * Добавляет действие при клике по указанным слотам.
     * <p>
     * <b>Действия обрабатываются даже для пустых слотов!</b>
     *
     * @param action действие
     * @param slots  слот или несколько слотов
     */
    public void addAction(@Nullable final ClickAction action, final int... slots) {
        addActionInternal(action, slots);
    }

    /**
     * Привязывает игрока к этому экземпляру меню и
     * отображает его.
     *
     * @param viewer игрок
     */
    public void show(@NotNull final Player viewer) {
        this.viewer = viewer;
        viewer.openInventory(inventory);
    }

    /**
     * Обновляет меню для игрока, если он привязан.
     */
    public void update() {
        if (viewer == null) return;
        viewer.updateInventory();
    }

    /**
     * Возвращает инвентарь этого меню.
     *
     * @return инвентарь
     * @see Inventory
     */
    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    //<editor-fold defaultstate="collapsed" desc="Internal methods">
    @ApiStatus.Internal
    private Inventory setupBukkitInventory(final Template template) {
        final var type = template.type();
        final var title = template.title();
        if (type != null) {
            return title != null ? Bukkit.createInventory(this, type, title) : Bukkit.createInventory(this, type);
        } else {
            return title != null ? Bukkit.createInventory(this, template.size(), title) : Bukkit.createInventory(this, template.size());
        }
    }

    @ApiStatus.Internal
    void clickInternal(final InventoryClickEvent event) {
        final var action = clickActions.get(event.getSlot());

        if (action != null) {
            action.click(event);
        }
    }

    @ApiStatus.Internal
    void closeInternal(final InventoryCloseEvent event) {
        if (closeAction != null) {
            closeAction.close(event);
        }
    }

    @ApiStatus.Internal
    private void addItemInternal(final ItemStack item, final int... slots) {
        for (final int slot : slots) {
            if (slot < 0 || slot >= template.size()) continue;
            inventory.setItem(slot, item);
        }
    }

    @ApiStatus.Internal
    private void addActionInternal(final ClickAction action, final int... slots) {
        for (final int slot : slots) {
            if (slot < 0 || slot >= template.size()) continue;
            clickActions.put(slot, action);
        }
    }
    //</editor-fold>

    /**
     * Создатель экземпляров класса {@link GUI}.
     * <p>
     * Для вызова используйте метод {@link GUI#builder}.
     */
    public static final class Builder {
        private Component title;
        private int size;
        private InventoryType type;
        private final Map<Integer, ItemStack> items = new HashMap<>();
        private final Map<Integer, ClickAction> actions = new HashMap<>();
        private long cooldown;
        private CloseAction closeAction;

        private Builder(@Nullable final Template template) {
            if (template != null) {
                this.title = template.title();
                this.size = template.size();
                this.type = template.type();
                this.items.putAll(template.items());
            }
        }

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
         * Добавляет предмет с действием при клике по нему
         * в указанные слоты.
         * <p>
         * Если в каком-то из указанных слотов уже есть предмет,
         * перезаписывает его.
         *
         * @param item   предмет
         * @param action действие
         * @param slots  слот или несколько слотов
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder item(@NotNull final ItemStack item, @NotNull final ClickAction action, final int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");
            for (final int slot : slots) {
                items.put(slot, item);
                actions.put(slot, action);
            }
            return this;
        }

        /**
         * Добавляет действие при клике по указанным слотам.
         * <p>
         * Если в каком-то из указанных слотов уже есть действие,
         * перезаписывает его.
         * <p>
         * <b>Действия обрабатываются даже для пустых слотов!</b>
         *
         * @param action действие
         * @param slots  слот или несколько слотов
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder action(@NotNull final ClickAction action, final int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");
            for (final int slot : slots) {
                actions.put(slot, action);
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
            this.cooldown = cooldown;
            return this;
        }

        /**
         * Добавляет действие, выполняемое при закрытии меню.
         *
         * @param action действие
         * @return текущий {@link Builder} для продолжения создания
         */
        public Builder closeAction(@Nullable final CloseAction action) {
            this.closeAction = action;
            return this;
        }

        /**
         * Собирает указанные параметры "до кучи" и
         * создаёт из них экземпляр меню.
         *
         * @return созданный экземпляр меню
         */
        public GUI build() {
            final var template = new Template(title, size, type, items, cooldown);
            return new GUI(template, actions, closeAction);
        }
    }
}

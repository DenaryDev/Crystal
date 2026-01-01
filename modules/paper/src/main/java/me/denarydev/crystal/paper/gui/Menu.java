/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui;

import com.google.common.base.Preconditions;
import me.denarydev.crystal.paper.gui.actions.ClickAction;
import me.denarydev.crystal.paper.gui.actions.CloseAction;
import me.denarydev.crystal.paper.gui.template.SimpleTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
 * Экземпляр меню.
 * <p>
 * Используйте {@link Menu#builder()} для создания меню нуля,
 * или {@link Menu#builder(Template)} для создания меню по шаблону
 */
public class Menu implements InventoryHolder {

    private final Template template;
    private final Inventory inventory;

    private final Map<Integer, ClickAction> clickActions = new HashMap<>();
    @Nullable
    private final CloseAction closeAction;

    @Nullable
    private Player viewer;

    @ApiStatus.Internal
    Menu(Template template, Map<Integer, ClickAction> actions, @Nullable CloseAction closeAction) {
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
    public static Builder builder(@NotNull Template template) {
        return new Builder(template);
    }

    /**
     * Возвращает шаблон этого меню.
     * <p>
     * Если вы создавали меню без шаблона, то вернёт шаблон,
     * созданный из параметров, указанных при создании этого меню.
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
    public boolean hasItem(int slot) {
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
    public void addItem(@NotNull ItemStack item, int... slots) {
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
    public void addItem(@NotNull ItemStack item, @Nullable ClickAction action, int... slots) {
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
    public void addAction(@Nullable ClickAction action, int... slots) {
        addActionInternal(action, slots);
    }

    /**
     * Открывает это меню для указанного игрока.
     *
     * @param viewer игрок
     */
    public void show(@NotNull Player viewer) {
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
     * Закрывает меню, если оно открыто игроку.
     */
    public void close() {
        if (viewer == null) return;

        viewer.closeInventory();
    }

    /**
     * @see InventoryHolder#getInventory()
     */
    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    private Inventory setupBukkitInventory(Template template) {
        final Component title = template.title();

        final int size = template.size() == 0 ? 54 : template.size();

        if (template instanceof SimpleTemplate simple && simple.type() != null) {
            return title != null ?
                Bukkit.createInventory(this, simple.type(), title) :
                Bukkit.createInventory(this, simple.type());
        } else {
            return title != null ?
                Bukkit.createInventory(this, size, title) :
                Bukkit.createInventory(this, size);
        }
    }

    private void addItemInternal(ItemStack item, int... slots) {
        for (int slot : slots) {
            if (slot < 0 || slot >= template.size()) continue;

            inventory.setItem(slot, item);
        }
    }

    private void addActionInternal(ClickAction action, int... slots) {
        for (int slot : slots) {
            if (slot < 0 || slot >= template.size()) continue;

            clickActions.put(slot, action);
        }
    }

    @ApiStatus.Internal
    public void clickInternal(InventoryClickEvent event) {
        final ClickAction action = clickActions.get(event.getSlot());

        if (action != null) {
            action.click(event);
        }
    }

    @ApiStatus.Internal
    public void closeInternal(InventoryCloseEvent event) {
        if (closeAction != null) {
            closeAction.close(event);
        }
    }

    /**
     * Создатель экземпляров класса {@link Menu}.
     * <p>
     * Для вызова используйте метод {@link Menu#builder}.
     */
    public static final class Builder {
        private Component title;
        private int size;
        private InventoryType type;
        private final Map<Integer, ItemStack> items = new HashMap<>();
        private final Map<Integer, ClickAction> actions = new HashMap<>();
        private long cooldown;
        private CloseAction closeAction;

        private Builder(@Nullable Template template) {
            if (template != null) {
                this.title = template.title();
                this.size = template.size();
                this.type = template instanceof SimpleTemplate simple ? simple.type() : null;

                this.items.putAll(template.items());
            }
        }

        /**
         * Возвращает заголовок меню, если установлен.
         *
         * @return заголовок меню, или null, если он не установлен.
         */
        @Nullable
        public Component title() {
            return title;
        }

        /**
         * Устанавливает заголовок меню из компонента MiniMessage.
         * <p>
         * Если указать null, заголовок будет сброшен!
         *
         * @param title заголовок
         */
        public Builder title(@Nullable Component title) {
            this.title = title;

            return this;
        }

        /**
         * Устанавливает заголовок меню из строки, применяя к ней
         * форматирование MiniMessage и указанные плейсхолдеры.
         *
         * @param title     заголовок
         * @param resolvers плейсхолдеры
         */
        public Builder titleRich(@NotNull String title, @NotNull TagResolver... resolvers) {
            this.title = MiniMessage.miniMessage().deserialize(title, resolvers);

            return this;
        }

        /**
         * Устанавливает заголовок меню из строки без форматирования.
         *
         * @param title заголовок
         */
        public Builder titlePlain(@NotNull String title) {
            this.title = Component.text(title);

            return this;
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
            Preconditions.checkArgument(size % 9 == 0, "Size must be multiple of 9!");
            Preconditions.checkArgument(size >= 9 && size <= 54, "Size must be between 9 and 54!");

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
        public Builder type(@NotNull InventoryType type) {
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
        public Builder item(@NotNull ItemStack item, int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");

            for (int slot : slots) {
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
         */
        public Builder item(@NotNull ItemStack item, @NotNull ClickAction action, int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");

            for (int slot : slots) {
                items.put(slot, item);
                actions.put(slot, action);
            }

            return this;
        }

        /**
         * Возвращает все указанные действия при клике по предметам, привязанные к слотам.
         *
         * @return все указанные действия
         */
        public Map<Integer, ClickAction> actions() {
            return actions;
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
         */
        public Builder action(@NotNull ClickAction action, int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");

            for (int slot : slots) {
                actions.put(slot, action);
            }

            return this;
        }

        /**
         * Возвращает задержку обработки кликов меню.
         *
         * @return задержка обработки кликов
         */
        public long cooldown() {
            return cooldown;
        }

        /**
         * Устанавливает задержку между обработкой кликов.
         *
         * @param cooldown задержка
         */
        public Builder cooldown(long cooldown) {
            this.cooldown = cooldown;

            return this;
        }

        /**
         * Возвращает действие, выполняемое при закрытии меню.
         *
         * @return действие, если установлено, иначе null.
         */
        @Nullable
        public CloseAction closeAction() {
            return closeAction;
        }

        /**
         * Добавляет действие, выполняемое при закрытии меню.
         *
         * @param action действие
         */
        public Builder closeAction(@Nullable CloseAction action) {
            this.closeAction = action;

            return this;
        }

        /**
         * Собирает указанные параметры "до кучи" и
         * создаёт из них экземпляр меню.
         *
         * @return созданный экземпляр меню
         */
        public Menu build() {
            final SimpleTemplate template = SimpleTemplate.builder()
                .title(title)
                .size(size)
                .type(type)
                .items(items)
                .cooldown(cooldown)
                .build();

            return new Menu(template, actions, closeAction);
        }
    }
}

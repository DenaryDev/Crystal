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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A menu instance backed by a Bukkit inventory.
 * <p>
 * Use {@link Menu#builder()} to create a menu from scratch,
 * or {@link Menu#builder(Template)} to create a menu from a template.
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
     * Creates a menu builder with no pre-set template.
     *
     * @return a blank {@link Builder}.
     */
    public static Builder builder() {
        return new Builder(null);
    }

    /**
     * Creates a menu builder pre-populated from the given template.
     *
     * @param template the template to base the builder on.
     * @return a {@link Builder} seeded from the template.
     */
    public static Builder builder(@NonNull Template template) {
        return new Builder(template);
    }

    /**
     * Returns the template associated with this menu.
     * <p>
     * If the menu was created without a template, returns the template
     * that was generated from the builder's parameters.
     *
     * @return the menu's template.
     */
    @NonNull
    public Template template() {
        return template;
    }

    /**
     * Returns the player currently viewing this menu,
     * or {@code null} if the menu is not open.
     *
     * @return the viewer, or {@code null} if none.
     */
    @Nullable
    public Player viewer() {
        return viewer;
    }

    /**
     * Returns whether the given slot contains an item.
     *
     * @param slot the slot to check.
     * @return {@code true} if the slot contains an item; {@code false} otherwise.
     */
    public boolean hasItem(int slot) {
        return inventory.getItem(slot) != null;
    }

    /**
     * Places an item in the given slot(s).
     * <p>
     * <b>If the menu is already open, call {@link #update()} to push the change to the viewer.</b>
     *
     * @param item  the item to place.
     * @param slots the slot or slots to place it in.
     */
    public void addItem(@NonNull ItemStack item, int... slots) {
        addItemInternal(item, slots);
    }

    /**
     * Places an item with a click action in the given slot(s).
     * <p>
     * <b>If the menu is already open, call {@link #update()} to push the change to the viewer.</b>
     *
     * @param item   the item to place.
     * @param action the click action for the item.
     * @param slots  the slot or slots to place it in.
     */
    public void addItem(@NonNull ItemStack item, @Nullable ClickAction action, int... slots) {
        addItemInternal(item, slots);
        addActionInternal(action, slots);
    }

    /**
     * Registers a click action for the given slot(s).
     * <p>
     * <b>Actions are fired even for empty slots!</b>
     *
     * @param action the click action.
     * @param slots  the slot or slots to register the action for.
     */
    public void addAction(@Nullable ClickAction action, int... slots) {
        addActionInternal(action, slots);
    }

    /**
     * Opens this menu for the given player.
     *
     * @param viewer the player to show the menu to.
     */
    public void show(@NonNull Player viewer) {
        this.viewer = viewer;

        viewer.openInventory(inventory);
    }

    /**
     * Refreshes the menu for the current viewer, if any.
     */
    public void update() {
        if (viewer == null) return;

        viewer.updateInventory();
    }

    /**
     * Closes this menu if it is currently open.
     */
    public void close() {
        if (viewer == null) return;

        viewer.closeInventory();
    }

    /**
     * @see InventoryHolder#getInventory()
     */
    @Override
    @NonNull
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
     * Builder for {@link Menu} instances.
     * <p>
     * Obtain via {@link Menu#builder} or {@link Menu#builder(Template)}.
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
         * Returns the menu title, or {@code null} if not set.
         *
         * @return the title, or {@code null}.
         */
        @Nullable
        public Component title() {
            return title;
        }

        /**
         * Sets the menu title from an Adventure {@link Component}.
         * <p>
         * Pass {@code null} to clear the title.
         *
         * @param title the title, or {@code null} to clear.
         */
        public Builder title(@Nullable Component title) {
            this.title = title;

            return this;
        }

        /**
         * Sets the menu title from a MiniMessage-formatted string.
         *
         * @param title     the title string.
         * @param resolvers the tag resolvers to apply.
         */
        public Builder titleRich(@NonNull String title, @NonNull TagResolver... resolvers) {
            this.title = MiniMessage.miniMessage().deserialize(title, resolvers);

            return this;
        }

        /**
         * Sets the menu title from a plain (unformatted) string.
         *
         * @param title the title string.
         */
        public Builder titlePlain(@NonNull String title) {
            this.title = Component.text(title);

            return this;
        }

        /**
         * Returns the menu size in slots.
         *
         * @return the slot count, or {@code 0} if not set.
         */
        public int size() {
            return size;
        }

        /**
         * Sets the menu size in slots.
         * <p>
         * The value must be a multiple of 9 and between 9 and 54 inclusive.
         * <p>
         * Ignored if an inventory type is set via {@link Builder#type(InventoryType)}.
         *
         * @param size the number of slots.
         */
        public Builder size(int size) {
            Preconditions.checkArgument(size % 9 == 0, "Size must be multiple of 9!");
            Preconditions.checkArgument(size >= 9 && size <= 54, "Size must be between 9 and 54!");

            this.size = size;

            return this;
        }

        /**
         * Returns the inventory type, or {@code null} if not set.
         *
         * @return the inventory type, or {@code null}.
         */
        @Nullable
        public InventoryType type() {
            return type;
        }

        /**
         * Sets the inventory type for this menu.
         * <p>
         * Setting a type overrides any size set via {@link Builder#size(int)}.
         *
         * @param type the inventory type.
         * @see InventoryType
         */
        public Builder type(@NonNull InventoryType type) {
            this.type = type;

            return this;
        }

        /**
         * Returns all items currently configured in this builder, keyed by slot.
         *
         * @return a map of slot indices to items.
         */
        public Map<Integer, ItemStack> items() {
            return items;
        }

        /**
         * Places an item in the given slot(s).
         * <p>
         * Overwrites any item already set in the specified slots.
         *
         * @param item  the item to place.
         * @param slots the slot or slots to place it in.
         */
        public Builder item(@NonNull ItemStack item, int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");

            for (int slot : slots) {
                items.put(slot, item);
            }

            return this;
        }

        /**
         * Places an item with a click action in the given slot(s).
         * <p>
         * Overwrites any item or action already set in the specified slots.
         *
         * @param item   the item to place.
         * @param action the click action for the item.
         * @param slots  the slot or slots to place it in.
         */
        public Builder item(@NonNull ItemStack item, @NonNull ClickAction action, int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");

            for (int slot : slots) {
                items.put(slot, item);
                actions.put(slot, action);
            }

            return this;
        }

        /**
         * Returns all click actions currently configured in this builder, keyed by slot.
         *
         * @return a map of slot indices to click actions.
         */
        public Map<Integer, ClickAction> actions() {
            return actions;
        }

        /**
         * Registers a click action for the given slot(s).
         * <p>
         * Overwrites any action already set in the specified slots.
         * <p>
         * <b>Actions are fired even for empty slots!</b>
         *
         * @param action the click action.
         * @param slots  the slot or slots to register the action for.
         */
        public Builder action(@NonNull ClickAction action, int... slots) {
            Preconditions.checkArgument(slots.length > 0, "You must specify at least one slot!");

            for (int slot : slots) {
                actions.put(slot, action);
            }

            return this;
        }

        /**
         * Returns the click cooldown for this menu.
         *
         * @return the cooldown in milliseconds.
         */
        public long cooldown() {
            return cooldown;
        }

        /**
         * Sets the minimum time between click events.
         *
         * @param cooldown the cooldown in milliseconds.
         */
        public Builder cooldown(long cooldown) {
            this.cooldown = cooldown;

            return this;
        }

        /**
         * Returns the action executed when the menu is closed, or {@code null} if not set.
         *
         * @return the close action, or {@code null}.
         */
        @Nullable
        public CloseAction closeAction() {
            return closeAction;
        }

        /**
         * Sets the action to execute when the menu is closed.
         *
         * @param action the close action, or {@code null} to remove.
         */
        public Builder closeAction(@Nullable CloseAction action) {
            this.closeAction = action;

            return this;
        }

        /**
         * Builds and returns a {@link Menu} from the configured parameters.
         *
         * @return the created menu instance.
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

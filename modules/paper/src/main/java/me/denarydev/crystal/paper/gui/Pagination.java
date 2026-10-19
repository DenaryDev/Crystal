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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Splits a list of entries across pages of a {@link Menu}.
 * <p>
 * The pagination owns a set of content slots and, optionally, previous/next buttons and
 * an empty-list placeholder. Re-rendering touches only those slots, so everything else in
 * the menu (borders, static buttons) is left to the menu itself. Navigation buttons are
 * hidden on the first and last page.
 * <p>
 * Create one with {@link #builder(Menu)}.
 *
 * @param <T> the entry type.
 */
public final class Pagination<T> {

    private final Menu menu;
    private final int[] slots;
    private final Function<T, ItemStack> renderer;
    @Nullable
    private final BiConsumer<T, InventoryClickEvent> clickAction;
    @Nullable
    private final Button previous;
    @Nullable
    private final Button next;
    @Nullable
    private final Button empty;

    private List<T> items;
    private int page;

    private Pagination(Builder<T> builder) {
        this.menu = builder.menu;
        this.slots = builder.slots;
        this.renderer = builder.renderer;
        this.clickAction = builder.clickAction;
        this.previous = builder.previous;
        this.next = builder.next;
        this.empty = builder.empty;
        this.items = builder.items;

        render();
    }

    /**
     * Creates a pagination builder for the given menu.
     *
     * @param menu the menu to paginate in.
     * @param <T>  the entry type.
     */
    public static <T> Builder<T> builder(@NonNull Menu menu) {
        return new Builder<>(menu);
    }

    //region State

    /**
     * Returns the menu this pagination draws into.
     */
    public Menu menu() {
        return menu;
    }

    /**
     * Returns the zero-based index of the current page.
     */
    public int page() {
        return page;
    }

    /**
     * Returns the number of pages, at least 1.
     */
    public int pages() {
        return Math.max(1, (items.size() + slots.length - 1) / slots.length);
    }

    /**
     * Returns whether there is a page before the current one.
     */
    public boolean hasPrevious() {
        return page > 0;
    }

    /**
     * Returns whether there is a page after the current one.
     */
    public boolean hasNext() {
        return page < pages() - 1;
    }

    /**
     * Returns an unmodifiable view of the entries.
     */
    public List<T> items() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns the number of entries per page.
     */
    public int pageSize() {
        return slots.length;
    }

    //endregion

    //region Navigation

    /**
     * Shows the menu to the player.
     *
     * @param viewer the player.
     * @see Menu#show(Player)
     */
    public void show(@NonNull Player viewer) {
        menu.show(viewer);
    }

    /**
     * Switches to the given page and redraws the content slots.
     * <p>
     * The page is clamped to the valid range.
     *
     * @param page the zero-based page index.
     */
    public Pagination<T> page(int page) {
        this.page = Math.clamp(page, 0, pages() - 1);
        render();
        menu.update();

        return this;
    }

    /**
     * Switches to the previous page, if any.
     */
    public Pagination<T> previous() {
        return page(page - 1);
    }

    /**
     * Switches to the next page, if any.
     */
    public Pagination<T> next() {
        return page(page + 1);
    }

    /**
     * Replaces the entries and redraws.
     * <p>
     * The current page is kept when it still exists, otherwise the last page is shown.
     *
     * @param items the new entries.
     */
    public Pagination<T> items(@NonNull List<T> items) {
        this.items = new ArrayList<>(items);

        return page(page);
    }

    /**
     * Redraws the current page without changing it, for example after an entry changed.
     */
    public Pagination<T> refresh() {
        return page(page);
    }

    //endregion

    private void render() {
        final int offset = page * slots.length;

        for (int i = 0; i < slots.length; i++) {
            final int index = offset + i;
            final int slot = slots[i];

            if (index < items.size()) {
                final T item = items.get(index);
                menu.addItem(renderer.apply(item), action(item), slot);
            } else {
                menu.removeItem(slot);
            }
        }

        if (empty != null) {
            if (items.isEmpty()) {
                menu.addItem(empty.item, null, empty.slot);
            } else {
                menu.removeItem(empty.slot);
            }
        }

        if (previous != null) {
            if (hasPrevious()) {
                menu.addItem(previous.item, event -> previous(), previous.slot);
            } else {
                menu.removeItem(previous.slot);
            }
        }

        if (next != null) {
            if (hasNext()) {
                menu.addItem(next.item, event -> next(), next.slot);
            } else {
                menu.removeItem(next.slot);
            }
        }
    }

    @Nullable
    private ClickAction action(T item) {
        if (clickAction == null) return null;

        return event -> clickAction.accept(item, event);
    }

    private record Button(ItemStack item, int slot) {
    }

    /**
     * Builder for {@link Pagination} instances.
     *
     * @param <T> the entry type.
     */
    public static final class Builder<T> {

        private final Menu menu;
        private int[] slots = new int[0];
        @Nullable
        private Function<T, ItemStack> renderer;
        @Nullable
        private BiConsumer<T, InventoryClickEvent> clickAction;
        @Nullable
        private Button previous;
        @Nullable
        private Button next;
        @Nullable
        private Button empty;
        private List<T> items = new ArrayList<>();

        private Builder(Menu menu) {
            this.menu = menu;
        }

        /**
         * Sets the slots that hold entries, in display order.
         * <p>
         * Combine with {@link Slots#inner(int)} or {@link Slots#range(int, int)}.
         *
         * @param slots the content slots.
         */
        public Builder<T> slots(int... slots) {
            Preconditions.checkArgument(slots.length > 0, "no content slots");
            this.slots = Arrays.copyOf(slots, slots.length);
            return this;
        }

        /**
         * Sets the function that turns an entry into the item shown for it.
         *
         * @param renderer the entry renderer.
         */
        public Builder<T> renderer(@NonNull Function<T, ItemStack> renderer) {
            this.renderer = renderer;
            return this;
        }

        /**
         * Sets the action run when an entry is clicked.
         *
         * @param action the click handler, given the entry and the event.
         */
        public Builder<T> onClick(@Nullable BiConsumer<T, InventoryClickEvent> action) {
            this.clickAction = action;
            return this;
        }

        /**
         * Sets the previous-page button; hidden on the first page.
         *
         * @param item the button item.
         * @param slot the slot to place it in.
         */
        public Builder<T> previous(@NonNull ItemStack item, int slot) {
            this.previous = new Button(item, slot);
            return this;
        }

        /**
         * Sets the next-page button; hidden on the last page.
         *
         * @param item the button item.
         * @param slot the slot to place it in.
         */
        public Builder<T> next(@NonNull ItemStack item, int slot) {
            this.next = new Button(item, slot);
            return this;
        }

        /**
         * Sets a placeholder shown when there are no entries at all.
         *
         * @param item the placeholder item.
         * @param slot the slot to place it in.
         */
        public Builder<T> empty(@NonNull ItemStack item, int slot) {
            this.empty = new Button(item, slot);
            return this;
        }

        /**
         * Sets the initial entries.
         *
         * @param items the entries.
         */
        public Builder<T> items(@NonNull List<T> items) {
            this.items = new ArrayList<>(items);
            return this;
        }

        /**
         * Builds the pagination and draws the first page into the menu.
         */
        public Pagination<T> build() {
            Preconditions.checkState(slots.length > 0, "content slots not set");
            Preconditions.checkState(renderer != null, "renderer not set");

            return new Pagination<>(this);
        }
    }
}

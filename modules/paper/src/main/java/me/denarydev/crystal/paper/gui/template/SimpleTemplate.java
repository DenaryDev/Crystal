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
 * A menu template that assigns items to slots by direct index.
 * <p>
 * Allows fine-grained control over inventory size, container type (e.g., HOPPER or DISPENSER),
 * and maps items to specific numeric slot indices.
 * <p>
 * Best suited for standard chest-style menus or menus with a fixed item layout.
 */
public final class SimpleTemplate extends Template {

    @Nullable
    private final InventoryType type;

    /**
     * Creates a new builder for a simple template.
     * Allows setting an inventory type, placing items in specific slots,
     * and configuring the title and click cooldown.
     *
     * @return a {@link SimpleTemplate.Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    private SimpleTemplate(@Nullable Component title, int size, Map<Integer, ItemStack> items, long cooldown, @Nullable InventoryType type) {
        super(title, size, items, cooldown);
        this.type = type;
    }

    /**
     * Returns whether this template has an inventory type set.
     *
     * @return {@code true} if a type is set; {@code false} otherwise.
     */
    public boolean hasType() {
        return type != null;
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

    public static final class Builder extends Template.Builder<Builder, SimpleTemplate> {
        private int size;
        private InventoryType type;
        private final Map<Integer, ItemStack> items = new LinkedHashMap<>();

        private Builder() {
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
            Preconditions.checkArgument(size % 9 == 0, "size must be multiple of 9");
            Preconditions.checkArgument(size >= 9 && size <= 54, "size must be between 9 and 54");

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
            Preconditions.checkArgument(slots.length > 0, "you must specify at least one slot");

            for (int slot : slots) {
                items.put(slot, item);
            }

            return this;
        }

        /**
         * Adds all given items to this builder.
         *
         * @param items a map of slot indices to items.
         */
        public Builder items(Map<Integer, ItemStack> items) {
            this.items.putAll(items);

            return this;
        }

        /**
         * Builds and returns a {@link SimpleTemplate} from the configured parameters.
         *
         * @return the created template.
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

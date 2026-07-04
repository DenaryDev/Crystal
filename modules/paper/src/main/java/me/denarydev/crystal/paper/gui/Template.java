/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui;

import com.google.common.base.Preconditions;
import me.denarydev.crystal.paper.gui.template.MatrixTemplate;
import me.denarydev.crystal.paper.gui.template.SimpleTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Base class for menu templates in the Crystal GUI system.
 * <p>
 * Templates let you pre-configure the structure and contents of a menu,
 * which is useful for caching parameters and simplifying display code.
 * <p>
 * <b>Note:</b> Templates are optional.
 * For dynamic or simple menus, you can create menus directly via {@code Menu.builder()}.
 */
public sealed abstract class Template permits SimpleTemplate, MatrixTemplate {

    @Nullable
    protected final Component title;
    protected final int size;
    protected final Map<Integer, ItemStack> items;
    protected final long cooldown;

    /**
     * Creates a new builder for a simple template.
     * Allows setting an inventory type, placing items in specific slots,
     * and configuring the title and click cooldown.
     *
     * @return a {@link SimpleTemplate.Builder} instance.
     */
    public static SimpleTemplate.Builder simpleBuilder() {
        return SimpleTemplate.builder();
    }

    /**
     * Creates a new builder for a matrix template.
     * Allows defining the menu layout using a character mask.
     *
     * @return a {@link MatrixTemplate.Builder} instance.
     */
    public static MatrixTemplate.Builder matrixBuilder() {
        return MatrixTemplate.builder();
    }

    protected Template(@Nullable Component title, int size, Map<Integer, ItemStack> items, long cooldown) {
        this.title = title;
        this.size = size;
        this.items = items;
        this.cooldown = cooldown;
    }

    /**
     * Returns whether this template has a title set.
     *
     * @return {@code true} if a title is set; {@code false} otherwise.
     */
    public boolean hasTitle() {
        return title != null;
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
     * Returns whether this template has a size set.
     *
     * @return {@code true} if a size is set; {@code false} otherwise.
     */
    public boolean hasSize() {
        return size > 0;
    }

    /**
     * Returns the number of slots in this menu.
     *
     * @return the slot count.
     */
    public int size() {
        return size;
    }

    /**
     * Returns whether at least one item is configured in this template.
     *
     * @return {@code true} if any items are set; {@code false} otherwise.
     */
    public boolean hasItems() {
        return !items.isEmpty();
    }

    /**
     * Returns a map of items keyed by slot index.
     *
     * @return the item map.
     */
    public Map<Integer, ItemStack> items() {
        return items;
    }

    /**
     * Returns the click cooldown for this template.
     *
     * @return the cooldown in milliseconds.
     */
    public long cooldown() {
        return cooldown;
    }

    public sealed abstract static class Builder<B extends Builder<B, T>, T extends Template> permits SimpleTemplate.Builder, MatrixTemplate.Builder {
        protected Component title;
        protected long cooldown;

        /**
         * Returns the menu title, or {@code null} if not set.
         *
         * @return the title, or {@code null}.
         */
        @Nullable
        public final Component title() {
            return title;
        }

        /**
         * Sets the menu title from an Adventure {@link Component}.
         * <p>
         * Pass {@code null} to reset the title to the default.
         *
         * @param title the title, or {@code null} to clear.
         */
        public final B title(@Nullable final Component title) {
            this.title = title;

            return self();
        }

        /**
         * Sets the menu title from a MiniMessage-formatted string.
         *
         * @param title     the title string.
         * @param resolvers the tag resolvers to apply.
         */
        public final B titleRich(@NonNull String title, @NonNull TagResolver... resolvers) {
            this.title = MiniMessage.miniMessage().deserialize(title, resolvers);

            return self();
        }

        /**
         * Sets the menu title from a plain (unformatted) string.
         *
         * @param title the title string.
         */
        public final B titlePlain(@NonNull String title) {
            this.title = Component.text(title);

            return self();
        }

        /**
         * Returns the click cooldown for this template.
         *
         * @return the cooldown in milliseconds.
         */
        public final long cooldown() {
            return cooldown;
        }

        /**
         * Sets the minimum time between click events.
         *
         * @param cooldown the cooldown in milliseconds.
         */
        public final B cooldown(long cooldown) {
            Preconditions.checkArgument(cooldown >= 0, "cooldown must be positive");

            this.cooldown = cooldown;

            return self();
        }

        /**
         * Builds and returns a template from the configured parameters.
         *
         * @return the created template.
         */
        public abstract T build();

        /**
         * Returns the concrete builder subtype to enable fluent chaining.
         */
        @ApiStatus.Internal
        protected abstract B self();
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.common;

import io.sapphiremc.lib.configurate.objectmapping.ConfigSerializable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * An item display name and lore pair, suitable for serialization via Configurate.
 */
@ConfigSerializable
public final class NameAndLore {

    /**
     * Creates a name-and-lore pair with no lore.
     *
     * @param name the item display name.
     * @return a new instance with no lore.
     */
    @Contract(
        value = "_ -> new",
        pure = true
    )
    public static @NonNull NameAndLore of(@NonNull String name) {
        return new NameAndLore(name, null);
    }

    /**
     * Creates a name-and-lore pair with the given lore lines.
     *
     * @param name the item display name.
     * @param lore the lore lines.
     * @return a new instance with the given name and lore.
     */
    @Contract(
        value = "_, _ -> new",
        pure = true
    )
    public static @NonNull NameAndLore of(@NonNull String name, @NonNull String... lore) {
        return new NameAndLore(name, Arrays.asList(lore));
    }

    /**
     * Creates a name-and-lore pair with the given lore list.
     *
     * @param name the item display name.
     * @param lore the lore lines.
     * @return a new instance with the given name and lore.
     */
    @Contract(
        value = "_, _ -> new",
        pure = true
    )
    public static @NonNull NameAndLore of(@NonNull String name, @NonNull List<String> lore) {
        return new NameAndLore(name, lore);
    }

    private String name;
    private List<String> lore;

    private NameAndLore() {
    }

    private NameAndLore(String name, List<String> lore) {
        this.name = name;
        this.lore = lore;
    }

    /**
     * Returns the raw (unparsed) display name string.
     *
     * @return the display name.
     * @see NameAndLore#name(TagResolver...)
     */
    @NonNull
    public String rawName() {
        return name;
    }

    /**
     * Returns the display name as a {@link Component} parsed by MiniMessage.
     *
     * @param placeholders the tag resolvers to apply.
     * @return the formatted display name.
     */
    @NonNull
    public Component name(@NonNull TagResolver... placeholders) {
        return MiniMessage.miniMessage().deserialize(name, placeholders);
    }

    /**
     * Returns the raw (unparsed) lore lines.
     *
     * @return the lore lines, or {@code null} if no lore was set.
     * @see NameAndLore#lore(TagResolver...)
     */
    @Nullable
    public List<String> rawLore() {
        return lore;
    }

    /**
     * Returns the lore as a list of {@link Component}s parsed by MiniMessage.
     *
     * @param tags the tag resolvers to apply.
     * @return the formatted lore lines, or {@code null} if no lore was set.
     */
    @Nullable
    public List<Component> lore(@NonNull TagResolver... tags) {
        if (lore == null) return null;

        return lore.stream()
            .map(line -> MiniMessage.miniMessage().deserialize(line, tags))
            .toList();
    }

    /**
     * Applies the display name and lore to the given item.
     *
     * @param item         the item to modify.
     * @param placeholders the tag resolvers to apply to the name and lore.
     * @return the same item with the updated display name and lore.
     */
    @NonNull
    public ItemStack apply(@NonNull ItemStack item, @NonNull TagResolver... placeholders) {
        item.editMeta(meta -> {
            meta.displayName(MiniMessage.miniMessage().deserialize(name, placeholders));
            if (lore != null) {
                meta.lore(lore.stream()
                    .map(line -> MiniMessage.miniMessage().deserialize(line, placeholders))
                    .toList());
            }
        });

        return item;
    }
}

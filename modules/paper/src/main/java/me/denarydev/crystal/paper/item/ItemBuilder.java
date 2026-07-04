/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.item;

import com.google.common.base.Preconditions;
import me.denarydev.crystal.paper.utils.HeadUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A fluent builder for constructing {@link ItemStack}s.
 */
public final class ItemBuilder {

    @NonNull
    private ItemStack itemStack;

    //region Builder factory methods

    /**
     * Creates an {@link ItemBuilder} for an air item (default type).
     */
    public static ItemBuilder empty() {
        return new ItemBuilder(ItemStack.of(Material.AIR));
    }

    /**
     * Creates an {@link ItemBuilder} for the given material type.
     *
     * @param material the item material.
     */
    public static ItemBuilder fromMaterial(@NonNull Material material) {
        return new ItemBuilder(ItemStack.of(material));
    }

    /**
     * Creates an {@link ItemBuilder} based on an existing item.
     * <p>
     * The original item is not modified.
     *
     * @param stack the item to copy.
     */
    public static ItemBuilder fromItem(@NonNull ItemStack stack) {
        return new ItemBuilder(stack.clone());
    }

    /**
     * Creates an {@link ItemBuilder} for a player head with the given skin texture.
     *
     * @param texture the Base64-encoded skin texture.
     */
    public static ItemBuilder playerHead(@NonNull String texture) {
        return new ItemBuilder(ItemStack.of(Material.PLAYER_HEAD))
            .texture(texture);
    }

    //endregion

    private ItemBuilder(@NonNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    //region Type, display name, lore

    /**
     * Sets the item material type.
     *
     * @param type the material type.
     */
    public ItemBuilder type(@NonNull Material type) {
        this.itemStack = this.itemStack.withType(type);

        return this;
    }

    /**
     * Sets the skin texture for a {@link Material#PLAYER_HEAD} item.
     * <p>
     * Ensure the item type is set to {@link Material#PLAYER_HEAD} before calling this method.
     *
     * @param texture the Base64-encoded skin texture.
     */
    public ItemBuilder texture(@NonNull String texture) {
        return editMeta(meta -> {
            if (meta instanceof SkullMeta skull) {
                HeadUtils.setTexture(skull, texture);
            }
        });
    }

    /**
     * Sets the stack size.
     *
     * @param amount the number of items (1–64).
     */
    public ItemBuilder amount(int amount) {
        Preconditions.checkArgument(amount > 0, "amount less than 1");
        Preconditions.checkArgument(amount <= 64, "amount greater than 64");

        this.itemStack.setAmount(amount);

        return this;
    }

    /**
     * Sets the display name from a {@link Component}.
     * <p>
     * Passing {@code null} leaves the display name unchanged.
     *
     * @param displayName the display name.
     */
    public ItemBuilder displayName(@Nullable Component displayName) {
        return editMeta(meta -> meta.displayName(displayName));
    }

    /**
     * Sets the display name from a MiniMessage-formatted string with optional tag resolvers.
     * <p>
     * Passing {@code null} leaves the display name unchanged.
     *
     * @param displayName the MiniMessage string.
     * @param tags        tag resolvers for placeholder substitution.
     */
    public ItemBuilder displayNameRich(@Nullable String displayName, @NonNull TagResolver... tags) {
        if (displayName != null) {
            return displayName(MiniMessage.miniMessage().deserialize(displayName, tags));
        }

        return this;
    }

    /**
     * Sets the display name from a plain (unformatted) string.
     * <p>
     * Passing {@code null} leaves the display name unchanged.
     *
     * @param displayName the plain string.
     */
    public ItemBuilder displayNamePlain(@Nullable String displayName) {
        if (displayName != null) {
            return displayName(Component.text(displayName));
        }

        return this;
    }

    /**
     * Sets the lore from a list of {@link Component}s.
     * <p>
     * Passing {@code null} leaves the lore unchanged.
     *
     * @param lore the lore lines.
     */
    public ItemBuilder lore(@Nullable List<Component> lore) {
        return editMeta(meta -> meta.lore(lore));
    }

    /**
     * Sets the lore from a list of MiniMessage-formatted strings with optional tag resolvers.
     * <p>
     * Passing {@code null} leaves the lore unchanged.
     *
     * @param lore the lore lines.
     * @param tags tag resolvers for placeholder substitution.
     */
    public ItemBuilder loreRich(@Nullable List<String> lore, @NonNull TagResolver... tags) {
        if (lore != null) {
            return lore(lore.stream()
                .map(line -> MiniMessage.miniMessage().deserialize(line, tags))
                .toList());
        }

        return this;
    }

    /**
     * Sets the lore from a list of plain (unformatted) strings.
     * <p>
     * Passing {@code null} leaves the lore unchanged.
     *
     * @param lore the plain lore lines.
     */
    public ItemBuilder lorePlain(@Nullable List<String> lore) {
        if (lore != null) {
            return lore(lore.stream().map(Component::text).collect(Collectors.toList()));
        }

        return this;
    }

    //endregion

    //region Flags, unbreakability, damage

    /**
     * Adds the given item flags to the item.
     *
     * @param flags the flags to add.
     */
    public ItemBuilder itemFlags(@NonNull ItemFlag... flags) {
        return editMeta(meta -> meta.addItemFlags(flags));
    }

    /**
     * Removes the given item flags from the item.
     *
     * @param flags the flags to remove.
     */
    public ItemBuilder removeFlags(@NonNull ItemFlag... flags) {
        return editMeta(meta -> meta.removeItemFlags(flags));
    }

    /**
     * Makes the item unbreakable.
     * <p>
     * Only applies to items that have durability.
     */
    public ItemBuilder unbreakable() {
        return unbreakable(true);
    }

    /**
     * Sets whether the item can take durability damage.
     * <p>
     * Only applies to items that have durability.
     *
     * @param unbreakable {@code true} to make the item unbreakable.
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        return editMeta(meta -> meta.setUnbreakable(unbreakable));
    }

    /**
     * Sets the item's damage value.
     * <p>
     * Only applies to items that have durability.
     *
     * @param damage the damage value.
     */
    public ItemBuilder damage(int damage) {
        return editMeta(meta -> {
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damage);
            }
        });
    }

    //endregion

    //region Item model and custom data

    /**
     * Sets the item model key.
     *
     * @param model the namespaced key of the item model.
     */
    public ItemBuilder itemModel(@NonNull NamespacedKey model) {
        return editMeta(meta -> meta.setItemModel(model));
    }

    /**
     * Sets the float list on the custom model data component.
     * <p>
     * Typically used in conjunction with a resource pack.
     *
     * @param floats the list of floats.
     * @see CustomModelDataComponent#setFloats(List)
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataFloats(@NonNull List<Float> floats) {
        return editMeta(meta -> {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFloats(floats);
            meta.setCustomModelDataComponent(component);
        });
    }

    /**
     * Sets the boolean flag list on the custom model data component.
     * <p>
     * Typically used in conjunction with a resource pack.
     *
     * @param flags the list of flags.
     * @see CustomModelDataComponent#setFlags(List)
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataFlags(@NonNull List<Boolean> flags) {
        return editMeta(meta -> {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFlags(flags);
            meta.setCustomModelDataComponent(component);
        });
    }

    /**
     * Sets the string list on the custom model data component.
     * <p>
     * Typically used in conjunction with a resource pack.
     *
     * @param strings the list of strings.
     * @see CustomModelDataComponent#setStrings(List)
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataStrings(@NonNull List<String> strings) {
        return editMeta(meta -> {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setStrings(strings);
            meta.setCustomModelDataComponent(component);
        });
    }

    /**
     * Sets the color list on the custom model data component.
     * <p>
     * Typically used in conjunction with a resource pack.
     *
     * @param colors the list of colors.
     * @see CustomModelDataComponent#setColors(List)
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataColors(@NonNull List<Color> colors) {
        return editMeta(meta -> {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setColors(colors);
            meta.setCustomModelDataComponent(component);
        });
    }

    /**
     * Sets the legacy custom model data integer used for resource-pack model selection.
     * <p>
     * Typically used in conjunction with a resource pack.
     *
     * @param data the custom model data value.
     * @deprecated Deprecated as of Minecraft 1.21.5. Use {@link #customModelDataFloats(List)} instead.
     */
    @Deprecated(since = "3.0.0")
    public ItemBuilder customModelData(@Nullable Integer data) {
        return editMeta(meta -> meta.setCustomModelData(data));
    }

    //endregion

    //region Enchantments

    /**
     * Adds the given enchantment at the given level.
     *
     * @param enchantment the enchantment to apply.
     * @param level       the enchantment level.
     */
    public ItemBuilder enchantment(@NonNull Enchantment enchantment, int level) {
        return enchantments(Map.of(enchantment, level));
    }

    /**
     * Adds the given enchantments, each at level 1.
     *
     * @param enchantments the enchantments to apply.
     */
    public ItemBuilder enchantments(@NonNull Enchantment... enchantments) {
        final Map<Enchantment, Integer> map = new HashMap<>();

        for (Enchantment enchantment : enchantments) {
            map.putIfAbsent(enchantment, 1);
        }

        return enchantments(map);
    }

    /**
     * Adds the given enchantments at their specified levels.
     *
     * @param enchantments a map of enchantments to levels.
     */
    public ItemBuilder enchantments(@NonNull Map<Enchantment, Integer> enchantments) {
        return editMeta(meta -> {
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
            }
        });
    }

    //endregion

    //region Potion effects

    /**
     * Sets the base potion type.
     * <p>
     * Only applies to {@link Material#POTION}, {@link Material#SPLASH_POTION},
     * and {@link Material#LINGERING_POTION} items.
     *
     * @param type the potion type.
     */
    public ItemBuilder potionType(@NonNull PotionType type) {
        return editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                potion.setBasePotionType(type);
            }
        });
    }

    /**
     * Adds potion effects, overwriting any existing effects of the same type.
     * <p>
     * Only applies to {@link Material#POTION}, {@link Material#SPLASH_POTION},
     * and {@link Material#LINGERING_POTION} items.
     *
     * @param effects the effects to add.
     */
    public ItemBuilder potionEffects(@NonNull PotionEffect... effects) {
        return potionEffects(true, effects);
    }

    /**
     * Adds potion effects with control over whether existing effects of the same type are overwritten.
     * <p>
     * Only applies to {@link Material#POTION}, {@link Material#SPLASH_POTION},
     * and {@link Material#LINGERING_POTION} items.
     *
     * @param overwrite whether to overwrite existing effects of the same type.
     * @param effects   the effects to add.
     */
    public ItemBuilder potionEffects(boolean overwrite, @NonNull PotionEffect... effects) {
        final Map<PotionEffect, Boolean> map = new HashMap<>();

        for (PotionEffect effect : effects) {
            map.put(effect, overwrite);
        }

        return potionEffects(map);
    }

    /**
     * Adds potion effects with per-effect overwrite control.
     * Each map entry pairs a {@link PotionEffect} with a boolean indicating whether
     * an existing effect of the same type should be overwritten.
     * <p>
     * Only applies to {@link Material#POTION}, {@link Material#SPLASH_POTION},
     * and {@link Material#LINGERING_POTION} items.
     *
     * @param effects a map of effects to their overwrite flags.
     */
    public ItemBuilder potionEffects(@NonNull Map<PotionEffect, Boolean> effects) {
        return editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                effects.forEach(potion::addCustomEffect);
            }
        });
    }

    /**
     * Removes the custom effect of the given type from the potion item.
     * <p>
     * Only applies to {@link Material#POTION}, {@link Material#SPLASH_POTION},
     * and {@link Material#LINGERING_POTION} items.
     *
     * @param type the effect type to remove.
     */
    public ItemBuilder removePotionEffect(@NonNull PotionEffectType type) {
        return editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                potion.removeCustomEffect(type);
            }
        });
    }

    /**
     * Removes all custom effects from the potion item.
     * <p>
     * Only applies to {@link Material#POTION}, {@link Material#SPLASH_POTION},
     * and {@link Material#LINGERING_POTION} items.
     */
    public ItemBuilder clearPotionEffects() {
        return editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                potion.clearCustomEffects();
            }
        });
    }

    //endregion

    //region Stored enchantments (enchanted books)

    /**
     * Stores the given enchantment at the given level in the item.
     * <p>
     * Only applies to {@link Material#ENCHANTED_BOOK} items.
     *
     * @param enchantment the enchantment to store.
     * @param level       the enchantment level.
     */
    public ItemBuilder storedEnchantment(@NonNull Enchantment enchantment, int level) {
        return storedEnchantments(Map.of(enchantment, level));
    }

    /**
     * Stores the given enchantments at level 1 each in the item.
     * <p>
     * Only applies to {@link Material#ENCHANTED_BOOK} items.
     *
     * @param enchantments the enchantments to store.
     */
    public ItemBuilder storedEnchantments(@NonNull Enchantment... enchantments) {
        final Map<Enchantment, Integer> map = new HashMap<>();

        for (Enchantment enchantment : enchantments) {
            map.putIfAbsent(enchantment, 1);
        }

        return storedEnchantments(map);
    }

    /**
     * Stores the given enchantments at their specified levels in the item.
     * <p>
     * Only applies to {@link Material#ENCHANTED_BOOK} items.
     *
     * @param enchantments a map of enchantments to levels.
     */
    public ItemBuilder storedEnchantments(@NonNull Map<Enchantment, Integer> enchantments) {
        return editMeta(meta -> {
            if (meta instanceof EnchantmentStorageMeta storage) {
                for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                    storage.addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                }
            }
        });
    }

    //endregion

    //region Persistent data container

    /**
     * Writes a value to the item's persistent data container under the given key.
     * <p>
     * Supported value types correspond to the constants in {@link PersistentDataType}.
     *
     * @param key   the key to store the value under.
     * @param value the value to store.
     */
    public ItemBuilder persistentData(@NonNull NamespacedKey key, @NonNull Object value) {
        return editMeta(meta -> {
            final PersistentDataContainer container = meta.getPersistentDataContainer();

            switch (value) {
                case Byte b -> container.set(key, PersistentDataType.BYTE, b);
                case Short s -> container.set(key, PersistentDataType.SHORT, s);
                case Integer i -> container.set(key, PersistentDataType.INTEGER, i);
                case Long l -> container.set(key, PersistentDataType.LONG, l);
                case Float f -> container.set(key, PersistentDataType.FLOAT, f);
                case Double d -> container.set(key, PersistentDataType.DOUBLE, d);
                case Boolean b -> container.set(key, PersistentDataType.BOOLEAN, b);
                case String s -> container.set(key, PersistentDataType.STRING, s);
                case Enum<?> e -> container.set(key, PersistentDataType.STRING, e.name());
                case byte[] ba -> container.set(key, PersistentDataType.BYTE_ARRAY, ba);
                case int[] ia -> container.set(key, PersistentDataType.INTEGER_ARRAY, ia);
                case long[] la -> container.set(key, PersistentDataType.LONG_ARRAY, la);
                case PersistentDataContainer pdc -> container.set(key, PersistentDataType.TAG_CONTAINER, pdc);
                default -> throw new IllegalArgumentException("Unknown persistent data type: " + value.getClass().getName());
            }
        });
    }

    //endregion

    /**
     * Applies the given consumer to the item's metadata directly.
     * <p>
     * Use this for modifications not covered by the other builder methods.
     *
     * @param editor the metadata editor.
     */
    public ItemBuilder editMeta(@NonNull Consumer<? super ItemMeta> editor) {
        this.itemStack.editMeta(editor);

        return this;
    }

    /**
     * Builds and returns the configured item stack.
     */
    @NonNull
    public ItemStack build() {
        return this.itemStack.clone();
    }

    /**
     * Returns a copy of this builder.
     */
    @NonNull
    public ItemBuilder duplicate() {
        return new ItemBuilder(this.itemStack.clone());
    }
}

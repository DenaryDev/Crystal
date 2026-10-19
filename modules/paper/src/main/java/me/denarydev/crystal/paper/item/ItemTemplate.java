/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * An immutable description of an item whose name and lore are kept as MiniMessage strings.
 * <p>
 * Unlike a deserialized {@link ItemStack}, a template is turned into an item every time
 * {@link #build} is called, so per-player text such as PlaceholderAPI placeholders or
 * {@code <player>} tags can be substituted on each build. The configuration format is the
 * same as for {@link ItemStack}: {@code material}, {@code amount}, {@code texture},
 * {@code name}, {@code lore}, {@code flags}, {@code enchants}, {@code unbreakable},
 * {@code damage}, {@code glow}, {@code custom-model-data} and {@code item-model}.
 * <p>
 * Templates without placeholders ({@link #hasPlaceholders()} is {@code false}) can be built
 * once and the resulting item cloned.
 */
public final class ItemTemplate {

    private final Material material;
    private final int amount;
    @Nullable
    private final String texture;
    @Nullable
    private final String name;
    private final List<String> lore;
    private final Set<ItemFlag> flags;
    private final Map<Enchantment, Integer> enchants;
    private final boolean unbreakable;
    private final int damage;
    @Nullable
    private final Boolean glow;
    @Nullable
    private final CustomModelData customModelData;
    @Nullable
    private final NamespacedKey itemModel;

    private ItemTemplate(Builder builder) {
        this.material = builder.material;
        this.amount = builder.amount;
        this.texture = builder.texture;
        this.name = builder.name;
        this.lore = List.copyOf(builder.lore);
        this.flags = builder.flags.isEmpty() ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(builder.flags));
        this.enchants = Collections.unmodifiableMap(new LinkedHashMap<>(builder.enchants));
        this.unbreakable = builder.unbreakable;
        this.damage = builder.damage;
        this.glow = builder.glow;
        this.customModelData = builder.customModelData;
        this.itemModel = builder.itemModel;
    }

    //region Factory methods

    /**
     * Creates a builder for a template of the given material.
     *
     * @param material the item material.
     */
    public static Builder builder(@NonNull Material material) {
        return new Builder(material);
    }

    /**
     * Creates a template from an existing item.
     * <p>
     * The display name and lore are serialized back to MiniMessage strings.
     *
     * @param item the item to copy.
     */
    public static ItemTemplate of(@NonNull ItemStack item) {
        final Builder builder = builder(item.getType()).amount(item.getAmount());

        if (!item.hasItemMeta()) {
            return builder.build();
        }

        final ItemMeta meta = item.getItemMeta();

        if (meta instanceof SkullMeta head) {
            final PlayerProfile profile = head.getPlayerProfile();
            if (profile != null) {
                final URL skin = profile.getTextures().getSkin();
                if (skin != null) {
                    builder.texture(skin.toExternalForm().substring(39));
                }
            }
        }

        final Component name = meta.displayName();
        if (name != null) {
            builder.name(serialize(name));
        }

        final List<Component> lore = meta.lore();
        if (lore != null) {
            builder.lore(lore.stream().map(ItemTemplate::serialize).toList());
        }

        builder.flags(meta.getItemFlags());
        builder.enchants(meta.getEnchants());
        builder.unbreakable(meta.isUnbreakable());

        if (meta instanceof Damageable damageable && damageable.hasDamage()) {
            builder.damage(damageable.getDamage());
        }

        if (meta.hasEnchantmentGlintOverride()) {
            builder.glow(meta.getEnchantmentGlintOverride());
        }

        final CustomModelData data = CustomModelData.of(meta);
        if (data != null && !data.isEmpty()) {
            builder.customModelData(data);
        }

        if (meta.hasItemModel()) {
            builder.itemModel(meta.getItemModel());
        }

        return builder.build();
    }

    /**
     * Serializes a component back to MiniMessage, dropping the explicit non-italic
     * decoration that {@link ItemBuilder} adds on build so round trips stay clean.
     */
    private static String serialize(Component component) {
        final Component cleaned = component.decoration(TextDecoration.ITALIC) == TextDecoration.State.FALSE
            ? component.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET)
            : component;

        return MiniMessage.miniMessage().serialize(cleaned);
    }

    //endregion

    //region Accessors

    /**
     * Returns the item material.
     */
    public Material material() {
        return material;
    }

    /**
     * Returns the stack size.
     */
    public int amount() {
        return amount;
    }

    /**
     * Returns the Base64-encoded head texture, or {@code null} for a regular item.
     */
    @Nullable
    public String texture() {
        return texture;
    }

    /**
     * Returns the display name as a MiniMessage string, or {@code null} if not set.
     */
    @Nullable
    public String name() {
        return name;
    }

    /**
     * Returns the lore lines as MiniMessage strings.
     */
    public List<String> lore() {
        return lore;
    }

    /**
     * Returns the item flags.
     */
    public Set<ItemFlag> flags() {
        return flags;
    }

    /**
     * Returns the enchantments and their levels.
     */
    public Map<Enchantment, Integer> enchants() {
        return enchants;
    }

    /**
     * Returns whether the item is unbreakable.
     */
    public boolean unbreakable() {
        return unbreakable;
    }

    /**
     * Returns the damage value, {@code 0} if undamaged.
     */
    public int damage() {
        return damage;
    }

    /**
     * Returns the enchantment glint override, or {@code null} for the default behaviour.
     */
    @Nullable
    public Boolean glow() {
        return glow;
    }

    /**
     * Returns the custom model data, or {@code null} if not set.
     */
    @Nullable
    public CustomModelData customModelData() {
        return customModelData;
    }

    /**
     * Returns the item model override, or {@code null} for the material's own model.
     */
    @Nullable
    public NamespacedKey itemModel() {
        return itemModel;
    }

    /**
     * Returns whether the name or any lore line contains a {@code %} placeholder.
     * <p>
     * A template without placeholders produces the same item on every build unless
     * resolvers are passed, so it may be built once and cloned.
     */
    public boolean hasPlaceholders() {
        if (name != null && name.indexOf('%') >= 0) return true;

        for (String line : lore) {
            if (line.indexOf('%') >= 0) return true;
        }

        return false;
    }

    //endregion

    //region Building

    /**
     * Builds the item, parsing the name and lore with MiniMessage.
     *
     * @param resolvers extra tag resolvers for the name and lore.
     */
    public ItemStack build(@NonNull TagResolver... resolvers) {
        return build(UnaryOperator.identity(), resolvers);
    }

    /**
     * Builds the item, passing the name and every lore line through the preprocessor
     * before parsing them with MiniMessage.
     * <p>
     * The preprocessor is where placeholder engines that work on raw strings, such as
     * PlaceholderAPI, are applied.
     *
     * @param preprocessor the string transformation applied before MiniMessage parsing.
     * @param resolvers    extra tag resolvers for the name and lore.
     */
    public ItemStack build(@NonNull UnaryOperator<String> preprocessor, @NonNull TagResolver... resolvers) {
        final ItemBuilder builder = texture != null
            ? ItemBuilder.playerHead(texture)
            : ItemBuilder.fromMaterial(material);

        builder.amount(amount);

        if (name != null) {
            builder.displayNameRich(preprocessor.apply(name), resolvers);
        }

        if (!lore.isEmpty()) {
            final List<String> lines = new ArrayList<>(lore.size());
            for (String line : lore) {
                lines.add(preprocessor.apply(line));
            }
            builder.loreRich(lines, resolvers);
        }

        if (!flags.isEmpty()) {
            builder.itemFlags(flags.toArray(ItemFlag[]::new));
        }

        if (!enchants.isEmpty()) {
            builder.enchantments(enchants);
        }

        if (unbreakable) {
            builder.unbreakable();
        }

        if (damage > 0) {
            builder.damage(damage);
        }

        if (glow != null) {
            builder.glow(glow);
        }

        if (customModelData != null) {
            builder.editMeta(customModelData::applyTo);
        }

        if (itemModel != null) {
            builder.itemModel(itemModel);
        }

        return builder.build();
    }

    //endregion

    /**
     * Returns a builder pre-filled with this template's values.
     */
    public Builder toBuilder() {
        return new Builder(material)
            .amount(amount)
            .texture(texture)
            .name(name)
            .lore(lore)
            .flags(flags)
            .enchants(enchants)
            .unbreakable(unbreakable)
            .damage(damage)
            .glow(glow)
            .customModelData(customModelData)
            .itemModel(itemModel);
    }

    /**
     * A builder for {@link ItemTemplate}s.
     */
    public static final class Builder {

        private Material material;
        private int amount = 1;
        @Nullable
        private String texture;
        @Nullable
        private String name;
        private List<String> lore = List.of();
        private Set<ItemFlag> flags = Set.of();
        private Map<Enchantment, Integer> enchants = Map.of();
        private boolean unbreakable;
        private int damage;
        @Nullable
        private Boolean glow;
        @Nullable
        private CustomModelData customModelData;
        @Nullable
        private NamespacedKey itemModel;

        private Builder(@NonNull Material material) {
            this.material = material;
        }

        /**
         * Sets the item material.
         */
        public Builder material(@NonNull Material material) {
            this.material = material;
            return this;
        }

        /**
         * Sets the stack size (1–64).
         */
        public Builder amount(int amount) {
            Preconditions.checkArgument(amount > 0, "amount less than 1");
            Preconditions.checkArgument(amount <= 64, "amount greater than 64");
            this.amount = amount;
            return this;
        }

        /**
         * Sets the Base64-encoded head texture; the item is built as a player head.
         */
        public Builder texture(@Nullable String texture) {
            this.texture = texture;
            return this;
        }

        /**
         * Sets the display name as a MiniMessage string.
         */
        public Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the lore lines as MiniMessage strings.
         */
        public Builder lore(@NonNull List<String> lore) {
            this.lore = List.copyOf(lore);
            return this;
        }

        /**
         * Sets the lore lines as MiniMessage strings.
         */
        public Builder lore(@NonNull String... lore) {
            return lore(List.of(lore));
        }

        /**
         * Sets the item flags.
         */
        public Builder flags(@NonNull Set<ItemFlag> flags) {
            this.flags = flags.isEmpty() ? Set.of() : EnumSet.copyOf(flags);
            return this;
        }

        /**
         * Sets the item flags.
         */
        public Builder flags(@NonNull ItemFlag... flags) {
            return flags(Set.of(flags));
        }

        /**
         * Sets the enchantments and their levels.
         */
        public Builder enchants(@NonNull Map<Enchantment, Integer> enchants) {
            this.enchants = new LinkedHashMap<>(enchants);
            return this;
        }

        /**
         * Sets whether the item is unbreakable.
         */
        public Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        /**
         * Sets the damage value.
         */
        public Builder damage(int damage) {
            Preconditions.checkArgument(damage >= 0, "damage less than 0");
            this.damage = damage;
            return this;
        }

        /**
         * Sets the enchantment glint override; {@code null} restores the default behaviour.
         *
         * @see ItemBuilder#glow(boolean)
         */
        public Builder glow(@Nullable Boolean glow) {
            this.glow = glow;
            return this;
        }

        /**
         * Sets the custom model data; {@code null} leaves the component untouched.
         */
        public Builder customModelData(@Nullable CustomModelData customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        /**
         * Sets the item model override, for example {@code minecraft:writable_book} to make a
         * {@link Material#PAPER} button look like a book; {@code null} keeps the material's model.
         *
         * @see ItemBuilder#itemModel(NamespacedKey)
         */
        public Builder itemModel(@Nullable NamespacedKey itemModel) {
            this.itemModel = itemModel;
            return this;
        }

        /**
         * Builds the template.
         */
        public ItemTemplate build() {
            return new ItemTemplate(this);
        }
    }
}

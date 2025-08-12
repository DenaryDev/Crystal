/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public final class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        final var material = node.node("material").get(Material.class);
        if (material == null) throw new SerializationException("Invalid ItemStack material");

        final int amount = node.node("amount").getInt(1);
        if (amount < 1 || amount > 64) throw new SerializationException("Invalid ItemStack amount");

        final var item = node.hasChild("texture") ?
            createHead(node.node("texture").getString(), amount)
            : new ItemStack(material, amount);
        final var meta = item.getItemMeta();

        if (node.hasChild("name")) {
            final String name = node.node("name").getString();
            if (name != null) meta.displayName(MiniMessage.miniMessage().deserialize(name));
        }

        if (node.hasChild("lore")) {
            final var lore = node.node("lore").getList(String.class);
            if (lore != null) meta.lore(lore.stream().map(MiniMessage.miniMessage()::deserialize).toList());
        }

        if (node.hasChild("unbreakable")) {
            meta.setUnbreakable(node.node("unbreakable").getBoolean(false));
        }

        if (node.hasChild("flags")) {
            node.node("flags").getList(String.class, Collections.emptyList()).stream()
                .map(s -> {
                    try {
                        return ItemFlag.valueOf(s);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(meta::addItemFlags);
        }

        if (node.hasChild("custom-model-data")) {
            meta.setCustomModelData(node.node("custom-model-data").getInt(0));
        }

        if (meta instanceof Damageable damageable) {
            damageable.setDamage(node.node("damage").getInt(0));
        }

        if (node.hasChild("enchants")) {
            node.node("enchants").childrenMap().forEach(((key, value) -> {
                final var ench = Enchantment.getByKey(NamespacedKey.minecraft(key.toString().toLowerCase()));
                if (ench == null) return;
                final int level = value.getInt();
                meta.addEnchant(ench, level, true);
            }));
        }

        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createHead(final String texture, final int amount) {
        final var head = new ItemStack(Material.PLAYER_HEAD, Math.max(Math.min(amount, 64), 1));
        if (texture == null)
            return head;

        final var skullMeta = (SkullMeta) head.getItemMeta();
        final var profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CustomHead");

        if (texture.endsWith("=")) {
            profile.setProperty(new ProfileProperty("textures", texture.replaceAll("=", "")));
        } else {
            final byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
            profile.setProperty(new ProfileProperty("textures", new String(encodedData)));
        }

        skullMeta.setPlayerProfile(profile);
        head.setItemMeta(skullMeta);
        return head;
    }

    @Override
    public void serialize(@NotNull Type type, @Nullable ItemStack item, @NotNull ConfigurationNode node) throws SerializationException {
        if (item != null) {
            node.node("material").set(item.getType());
            if (item.getAmount() > 1) node.node("amount").set(item.getAmount());

            final var meta = item.getItemMeta();

            if (meta instanceof SkullMeta head) {
                final var profile = head.getPlayerProfile();
                if (profile != null) {
                    final var skin = profile.getTextures().getSkin();
                    if (skin != null) node.node("texture").set(skin.toExternalForm().substring(39));
                }
            }

            if (item.hasItemMeta()) {
                if (meta.hasDisplayName()) {
                    final var name = meta.displayName();
                    if (name != null) node.node("name").set(String.class, MiniMessage.miniMessage().serialize(name));
                }

                if (meta.hasLore()) {
                    final var lore = meta.lore();
                    if (lore != null) node.node("lore").setList(String.class, lore.stream().map(MiniMessage.miniMessage()::serialize).toList());
                }

                if (meta.isUnbreakable()) node.node("unbreakable").set(true);
                if (meta.hasCustomModelData()) node.node("custom-model-data").set(meta.getCustomModelData());

                final var flags = new ArrayList<>(meta.getItemFlags());
                if (!flags.isEmpty()) node.node("flags").setList(ItemFlag.class, flags);

                if (meta.hasEnchants()) {
                    final var enchants = meta.getEnchants();
                    for (final var entry : enchants.entrySet()) {
                        node.node("enchants", entry.getKey().getKey().getKey().toLowerCase()).set(entry.getValue());
                    }
                }

                if (meta instanceof final Damageable damageable) {
                    if (damageable.hasDamage()) {
                        node.node("damage").set(damageable.getDamage());
                    }
                }
            }
        } else {
            node.set(null);
        }
    }
}

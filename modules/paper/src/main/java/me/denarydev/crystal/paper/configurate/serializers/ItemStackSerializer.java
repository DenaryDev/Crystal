/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.denarydev.crystal.paper.utils.HeadUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        final Material material = node.node("material").get(Material.class);
        if (material == null) throw new SerializationException("Invalid ItemStack material");

        final int amount = node.node("amount").getInt(1);
        if (amount < 1 || amount > 64) throw new SerializationException("Invalid ItemStack amount");

        final ItemStack item = node.hasChild("texture") ?
            HeadUtils.createHead(node.node("texture").getString(), amount)
            : new ItemStack(material, amount);

        final ItemMeta meta = item.getItemMeta();

        if (node.hasChild("name")) {
            final String name = node.node("name").getString();
            if (name != null) {
                meta.displayName(MiniMessage.miniMessage().deserialize(name));
            }
        }

        if (node.hasChild("lore")) {
            final List<String> lore = node.node("lore").getList(String.class);
            if (lore != null) {
                meta.lore(lore.stream().map(MiniMessage.miniMessage()::deserialize).toList());
            }
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

        //TODO: Update this to 1.21.5+ component api
        //if (node.hasChild("custom-model-data")) {
        //    meta.setCustomModelData(node.node("custom-model-data").getInt(0));
        //}

        if (meta instanceof Damageable damageable) {
            damageable.setDamage(node.node("damage").getInt(0));
        }

        if (node.hasChild("enchants")) {
            node.node("enchants").childrenMap().forEach(((key, value) -> {
                final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
                final Enchantment ench = registry.get(NamespacedKey.minecraft(key.toString().toLowerCase()));
                if (ench == null) return;

                final int level = value.getInt();
                meta.addEnchant(ench, level, true);
            }));
        }

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public void serialize(@NotNull Type type, @Nullable ItemStack item, @NotNull ConfigurationNode node) throws SerializationException {
        if (item != null) {
            node.node("material").set(item.getType());
            if (item.getAmount() > 1) node.node("amount").set(item.getAmount());

            final ItemMeta meta = item.getItemMeta();

            if (meta instanceof SkullMeta head) {
                final PlayerProfile profile = head.getPlayerProfile();
                if (profile != null) {
                    final URL skin = profile.getTextures().getSkin();
                    if (skin != null) {
                        node.node("texture").set(skin.toExternalForm().substring(39));
                    }
                }
            }

            if (item.hasItemMeta()) {
                if (meta.hasDisplayName()) {
                    final Component name = meta.displayName();
                    if (name != null) {
                        node.node("name").set(String.class, MiniMessage.miniMessage().serialize(name));
                    }
                }

                if (meta.hasLore()) {
                    final List<Component> lore = meta.lore();
                    if (lore != null) {
                        node.node("lore").setList(String.class, lore.stream().map(MiniMessage.miniMessage()::serialize).toList());
                    }
                }

                if (meta.isUnbreakable()) {
                    node.node("unbreakable").set(true);
                }

                //TODO: Update this to 1.21.5+ component api
                //if (meta.hasCustomModelData()) node.node("custom-model-data").set(meta.getCustomModelData());

                final List<ItemFlag> flags = new ArrayList<>(meta.getItemFlags());
                if (!flags.isEmpty()) {
                    node.node("flags").setList(ItemFlag.class, flags);
                }

                if (meta.hasEnchants()) {
                    final Map<Enchantment, Integer> enchants = meta.getEnchants();
                    for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                        node.node("enchants", entry.getKey().getKey().getKey().toLowerCase()).set(entry.getValue());
                    }
                }

                if (meta instanceof Damageable damageable) {
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

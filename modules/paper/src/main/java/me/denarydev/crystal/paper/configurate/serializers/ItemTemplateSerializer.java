/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.sapphiremc.lib.configurate.ConfigurationNode;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import io.sapphiremc.lib.configurate.serialize.TypeSerializer;
import me.denarydev.crystal.paper.item.CustomModelData;
import me.denarydev.crystal.paper.item.ItemTemplate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Serializer for {@link ItemTemplate}, using the same keys as {@link ItemStackSerializer}.
 */
public final class ItemTemplateSerializer implements TypeSerializer<ItemTemplate> {

    @Override
    public ItemTemplate deserialize(@NonNull Type type, ConfigurationNode node) throws SerializationException {
        final Material material = node.node("material").get(Material.class);
        if (material == null) throw new SerializationException("Invalid ItemStack material");

        final int amount = node.node("amount").getInt(1);
        if (amount < 1 || amount > 64) throw new SerializationException("Invalid ItemStack amount");

        final ItemTemplate.Builder builder = ItemTemplate.builder(material).amount(amount);

        if (node.hasChild("texture")) {
            builder.texture(node.node("texture").getString());
        }

        if (node.hasChild("name")) {
            builder.name(node.node("name").getString());
        }

        if (node.hasChild("lore")) {
            final List<String> lore = node.node("lore").getList(String.class);
            if (lore != null) {
                builder.lore(lore);
            }
        }

        if (node.hasChild("unbreakable")) {
            builder.unbreakable(node.node("unbreakable").getBoolean(false));
        }

        if (node.hasChild("flags")) {
            final Set<ItemFlag> flags = EnumSet.noneOf(ItemFlag.class);
            for (String flag : node.node("flags").getList(String.class, Collections.emptyList())) {
                try {
                    flags.add(ItemFlag.valueOf(flag));
                } catch (IllegalArgumentException ignored) {
                    // Unknown flags are skipped, as in ItemStackSerializer
                }
            }
            builder.flags(flags);
        }

        if (node.hasChild("damage")) {
            builder.damage(node.node("damage").getInt(0));
        }

        if (node.hasChild("enchants")) {
            final Registry<@NonNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            final Map<Enchantment, Integer> enchants = new LinkedHashMap<>();

            node.node("enchants").childrenMap().forEach((key, value) -> {
                final Enchantment ench = registry.get(NamespacedKey.minecraft(key.toString().toLowerCase()));
                if (ench == null) return;

                enchants.put(ench, value.getInt());
            });

            builder.enchants(enchants);
        }

        if (node.hasChild("glow")) {
            builder.glow(node.node("glow").getBoolean(false));
        }

        if (node.hasChild("item-model")) {
            final String raw = node.node("item-model").getString();
            final NamespacedKey key = raw == null ? null : NamespacedKey.fromString(raw);
            if (key == null) throw new SerializationException(node.node("item-model"), NamespacedKey.class, "Invalid item model: " + raw);

            builder.itemModel(key);
        }

        if (node.hasChild("custom-model-data")) {
            final CustomModelData data = CustomModelDataSerializer.read(node.node("custom-model-data"));
            builder.customModelData(data.isEmpty() ? null : data);
        }

        return builder.build();
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable ItemTemplate template, @NonNull ConfigurationNode node) throws SerializationException {
        if (template == null) {
            node.set(null);
            return;
        }

        node.node("material").set(template.material());
        if (template.amount() > 1) node.node("amount").set(template.amount());

        if (template.texture() != null) {
            node.node("texture").set(template.texture());
        }

        if (template.name() != null) {
            node.node("name").set(String.class, template.name());
        }

        if (!template.lore().isEmpty()) {
            node.node("lore").setList(String.class, template.lore());
        }

        if (template.unbreakable()) {
            node.node("unbreakable").set(true);
        }

        if (!template.flags().isEmpty()) {
            node.node("flags").setList(ItemFlag.class, new ArrayList<>(template.flags()));
        }

        for (Map.Entry<Enchantment, Integer> entry : template.enchants().entrySet()) {
            node.node("enchants", entry.getKey().getKey().getKey().toLowerCase()).set(entry.getValue());
        }

        if (template.damage() > 0) {
            node.node("damage").set(template.damage());
        }

        if (template.glow() != null) {
            node.node("glow").set(template.glow());
        }

        if (template.itemModel() != null) {
            node.node("item-model").set(template.itemModel().asString());
        }

        if (template.customModelData() != null) {
            CustomModelDataSerializer.write(node.node("custom-model-data"), template.customModelData());
        }
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import io.sapphiremc.lib.configurate.ConfigurationNode;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import io.sapphiremc.lib.configurate.serialize.TypeSerializer;
import me.denarydev.crystal.paper.item.ItemBuilder;
import me.denarydev.crystal.paper.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Serializer for {@link ItemStack}.
 * <p>
 * The format is that of {@link ItemTemplateSerializer}; the item is built from the template
 * through {@link ItemBuilder} on read and turned back into one with
 * {@link ItemTemplate#of(ItemStack)} on write.
 */
@NullMarked
public final class ItemStackSerializer implements TypeSerializer<ItemStack> {

    private final ItemTemplateSerializer templates = new ItemTemplateSerializer();

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return templates.deserialize(ItemTemplate.class, node).build();
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack item, ConfigurationNode node) throws SerializationException {
        templates.serialize(ItemTemplate.class, item == null ? null : ItemTemplate.of(item), node);
    }
}

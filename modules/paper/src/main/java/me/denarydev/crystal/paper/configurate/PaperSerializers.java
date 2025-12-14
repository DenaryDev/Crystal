/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate;

import me.denarydev.crystal.paper.configurate.serializers.ComponentSerializer;
import me.denarydev.crystal.paper.configurate.serializers.ItemStackSerializer;
import me.denarydev.crystal.paper.configurate.serializers.LocationSerializer;
import me.denarydev.crystal.paper.configurate.serializers.MaterialSerializer;
import me.denarydev.crystal.paper.configurate.serializers.NamespacedKeySerializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import io.sapphiremc.lib.configurate.serialize.TypeSerializerCollection;

/**
 * Набор кастомных сериализаторов для некоторых объектов из ядра Paper.
 * <p>
 * <b>Обязательно используйте Configurate из пути <code>io.sapphiremc.lib.configurate</code>,
 * не <code>org.spongepowered.configurate</code></b>
 */
public final class PaperSerializers {

    /**
     * Возвращает коллекцию сериализаторов для Configurate.
     *
     * @return {@link TypeSerializerCollection}
     */
    public static TypeSerializerCollection get() {
        return TypeSerializerCollection.builder()
            .registerAll(TypeSerializerCollection.defaults())
            .register(ItemStack.class, new ItemStackSerializer())
            .register(Location.class, new LocationSerializer())
            .register(new ComponentSerializer())
            .register(new NamespacedKeySerializer())
            .register(new MaterialSerializer())
            .build();
    }

    /**
     * Применяет все имеющиеся сериализаторы к указанному билдеру коллекции.
     *
     * @param builder {@link TypeSerializerCollection.Builder}
     */
    public static void apply(TypeSerializerCollection.Builder builder) {
        builder
            .register(ItemStack.class, new ItemStackSerializer())
            .register(Location.class, new LocationSerializer())
            .register(new ComponentSerializer())
            .register(new NamespacedKeySerializer())
            .register(new MaterialSerializer());
    }
}

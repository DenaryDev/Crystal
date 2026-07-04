/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate;

import io.sapphiremc.lib.configurate.serialize.TypeSerializerCollection;
import me.denarydev.crystal.paper.configurate.serializers.ComponentSerializer;
import me.denarydev.crystal.paper.configurate.serializers.ItemStackSerializer;
import me.denarydev.crystal.paper.configurate.serializers.LocationSerializer;
import me.denarydev.crystal.paper.configurate.serializers.MaterialSerializer;
import me.denarydev.crystal.paper.configurate.serializers.NamespacedKeySerializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * A collection of custom serializers for common Paper objects.
 * <p>
 * <b>You must use Configurate from <code>io.sapphiremc.lib.configurate</code>,
 * not <code>org.spongepowered.configurate</code>.</b>
 */
public final class PaperSerializers {

    /**
     * Returns the serializer collection for Configurate.
     *
     * @return a {@link TypeSerializerCollection} containing all registered serializers.
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
     * Registers all available serializers into the given collection builder.
     *
     * @param builder the {@link TypeSerializerCollection.Builder} to populate.
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

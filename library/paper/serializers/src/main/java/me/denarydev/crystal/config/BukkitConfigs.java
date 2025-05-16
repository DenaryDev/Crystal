/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import me.denarydev.crystal.config.serializers.ComponentSerializer;
import me.denarydev.crystal.config.serializers.ItemStackSerializer;
import me.denarydev.crystal.config.serializers.LocationSerializer;
import me.denarydev.crystal.config.serializers.MaterialSerializer;
import me.denarydev.crystal.config.serializers.NamespacedKeySerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public final class BukkitConfigs {

    /**
     * Return serializer collection for configurate
     *
     * @return {@link TypeSerializerCollection}
     */
    public static TypeSerializerCollection serializers() {
        return TypeSerializerCollection.builder()
            .registerAll(TypeSerializerCollection.defaults())
            .register(ItemStackSerializer.TYPE, new ItemStackSerializer())
            .register(LocationSerializer.TYPE, new LocationSerializer())
            .register(new ComponentSerializer())
            .register(new NamespacedKeySerializer())
            .register(new MaterialSerializer())
            .build();
    }
}

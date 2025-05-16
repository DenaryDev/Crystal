/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.serializers;

import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * @author DenaryDev
 * @since 14:31 28.04.2024
 */
@ApiStatus.AvailableSince("2.2.0")
public final class MaterialSerializer extends ScalarSerializer<Material> {
    public MaterialSerializer() {
        super(Material.class);
    }

    @Override
    public @NotNull Material deserialize(final Type type, @Nullable final Object obj) throws SerializationException {
        if (obj instanceof String s) {
            final var material = Material.matchMaterial(s);
            if (material == null) throw new SerializationException("Cannot deserialize " + obj + " as a Material");
            return material;
        }

        return Material.AIR;
    }

    @Override
    @Nullable
    protected Object serialize(@Nullable final Material item, final Predicate<Class<?>> typeSupported) {
        if (item == null) return null;

        return item.name();
    }
}

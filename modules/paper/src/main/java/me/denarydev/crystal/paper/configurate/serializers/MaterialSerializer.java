/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.sapphiremc.lib.configurate.serialize.ScalarSerializer;
import io.sapphiremc.lib.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * @author DenaryDev
 * @since 14:31 28.04.2024
 */
public final class MaterialSerializer extends ScalarSerializer<Material> {

    public MaterialSerializer() {
        super(Material.class);
    }

    @Override
    public @NotNull Material deserialize(@NotNull final Type type, @Nullable final Object obj) throws SerializationException {
        if (obj instanceof String s) {
            final Material material = Material.matchMaterial(s);
            if (material == null) {
                throw new SerializationException("Cannot deserialize " + obj + " as a Material");
            }

            return material;
        }

        return Material.AIR;
    }

    @Override
    @NotNull
    protected Object serialize(@NotNull final Material item, @NotNull final Predicate<Class<?>> typeSupported) {
        return item.name();
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import io.sapphiremc.lib.configurate.serialize.ScalarSerializer;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import org.bukkit.Material;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public final class MaterialSerializer extends ScalarSerializer<Material> {

    public MaterialSerializer() {
        super(Material.class);
    }

    @Override
    public @NonNull Material deserialize(@NonNull final Type type, @Nullable final Object obj) throws SerializationException {
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
    @NonNull
    protected Object serialize(@NonNull final Material item, @NonNull final Predicate<Class<?>> typeSupported) {
        return item.name();
    }
}

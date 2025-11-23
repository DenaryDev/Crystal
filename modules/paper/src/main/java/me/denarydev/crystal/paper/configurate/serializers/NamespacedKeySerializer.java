/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * @author DenaryDev
 * @since 0:00 18.02.2024
 */
public final class NamespacedKeySerializer extends ScalarSerializer<NamespacedKey> {
    public NamespacedKeySerializer() {
        super(NamespacedKey.class);
    }

    @Override
    public NamespacedKey deserialize(@NotNull final Type type, @Nullable final Object obj) throws SerializationException {
        if (obj instanceof String s) {
            final var key = NamespacedKey.fromString(s);
            if (key == null) {
                throw new SerializationException("Cannot deserialize " + obj + " as a NamespacedKey");
            }

            return key;
        }

        return null;
    }

    @Override
    @NotNull
    protected Object serialize(@NotNull final NamespacedKey item, @NotNull final Predicate<Class<?>> typeSupported) {
        return item.toString();
    }
}

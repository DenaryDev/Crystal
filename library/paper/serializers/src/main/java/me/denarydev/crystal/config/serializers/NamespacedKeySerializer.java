/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.serializers;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * @author DenaryDev
 * @since 0:00 18.02.2024
 */
@ApiStatus.AvailableSince("2.1.1")
public final class NamespacedKeySerializer extends ScalarSerializer<NamespacedKey> {
    public NamespacedKeySerializer() {
        super(NamespacedKey.class);
    }

    @Override
    public NamespacedKey deserialize(final Type type, @Nullable final Object obj) throws SerializationException {
        if (obj instanceof String s) {
            final var key = NamespacedKey.fromString(s);
            if (key == null) throw new SerializationException("Cannot deserialize " + obj + " as a NamespacedKey");
            return key;
        }

        return null;
    }

    @Override
    protected Object serialize(@Nullable final NamespacedKey item, final Predicate<Class<?>> typeSupported) {
        if (item == null) return null;

        return item.toString();
    }
}

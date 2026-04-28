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
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public final class NamespacedKeySerializer extends ScalarSerializer<NamespacedKey> {

    public NamespacedKeySerializer() {
        super(NamespacedKey.class);
    }

    @Override
    public NamespacedKey deserialize(@NonNull final Type type, @Nullable final Object obj) throws SerializationException {
        if (obj instanceof String s) {
            final NamespacedKey key = NamespacedKey.fromString(s);
            if (key == null) {
                throw new SerializationException("Cannot deserialize " + obj + " as a NamespacedKey");
            }

            return key;
        }

        return null;
    }

    @Override
    @NonNull
    protected Object serialize(@NonNull final NamespacedKey item, @NonNull final Predicate<Class<?>> typeSupported) {
        return item.toString();
    }
}

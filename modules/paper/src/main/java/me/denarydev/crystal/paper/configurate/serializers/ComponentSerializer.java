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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * Сериализатор для {@link net.kyori.adventure.text.Component} в Configurate.
 * Сериализует компоненты в строку формата MiniMessage и обратно.
 */
public class ComponentSerializer extends ScalarSerializer<Component> {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public ComponentSerializer() {
        super(Component.class);
    }

    @Override
    @Nullable
    public Component deserialize(@NonNull final Type type, @Nullable final Object obj) throws SerializationException {
        if (obj instanceof String s) {
            return MINI_MESSAGE.deserialize(s);
        } else {
            throw new SerializationException("Component serializer only supports String types");
        }
    }

    @Override
    @NonNull
    protected Object serialize(@NonNull final Component item, @NonNull final Predicate<Class<?>> typeSupported) {
        return MINI_MESSAGE.serialize(item);
    }
}

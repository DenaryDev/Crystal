/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.serializers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class ComponentSerializer extends ScalarSerializer<Component> {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public ComponentSerializer() {
        super(Component.class);
    }

    @Override
    @Nullable
    public Component deserialize(final Type type, @Nullable final Object obj) {
        if (obj instanceof String s) {
            return MINI_MESSAGE.deserialize(s);
        }

        return null;
    }

    @Override
    @Nullable
    protected Object serialize(@Nullable final Component item, final Predicate<Class<?>> typeSupported) {
        if (item == null) return null;

        return MINI_MESSAGE.serialize(item);
    }
}

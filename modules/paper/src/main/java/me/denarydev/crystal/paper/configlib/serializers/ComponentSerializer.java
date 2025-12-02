/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib.serializers;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * @author DenaryDev
 * @since 22:21 12.08.2025
 * @deprecated Используйте Configurate вместо ConfigLib.
 * <p>
 * Оставлено для обратной совместимости с уже написанными плагинами.
 * Будет удалено в одном из будущих промежуточных релизов.
 */
@Deprecated(forRemoval = true)
public final class ComponentSerializer implements Serializer<Component, String> {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public ComponentSerializer() {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
    }

    @Override
    public String serialize(Component element) {
        return MINI_MESSAGE.serialize(element);
    }

    @Override
    public Component deserialize(String element) {
        return MINI_MESSAGE.deserialize(element);
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib.serializers;

import de.exlll.configlib.Serializer;
import org.bukkit.NamespacedKey;

/**
 * @author DenaryDev
 * @since 22:24 12.08.2025
 * @deprecated Используйте Configurate вместо ConfigLib.
 * <p>
 * Оставлено для обратной совместимости с уже написанными плагинами.
 * Будет удалено в одном из будущих промежуточных релизов.
 */
@Deprecated(forRemoval = true)
public final class NamespacedKeySerializer implements Serializer<NamespacedKey, String> {

    public NamespacedKeySerializer() {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
    }

    @Override
    public String serialize(NamespacedKey element) {
        return element.toString();
    }

    @Override
    public NamespacedKey deserialize(String element) {
        final NamespacedKey key = NamespacedKey.fromString(element);
        if (key == null) {
            throw new RuntimeException("Cannot deserialize NamespacedKey from \"" + element + "\"");
        }

        return key;
    }
}

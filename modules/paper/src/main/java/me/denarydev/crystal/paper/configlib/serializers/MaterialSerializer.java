/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib.serializers;

import de.exlll.configlib.Serializer;
import org.bukkit.Material;

/**
 * @author DenaryDev
 * @since 22:37 12.08.2025
 * @deprecated Используйте Configurate вместо ConfigLib.
 * <p>
 * Оставлено для обратной совместимости с уже написанными плагинами.
 * Будет удалено в одном из будущих промежуточных релизов.
 */
@Deprecated(forRemoval = true)
public final class MaterialSerializer implements Serializer<Material, String> {

    public MaterialSerializer() {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
    }

    @Override
    public String serialize(Material element) {
        return element.name();
    }

    @Override
    public Material deserialize(String element) {
        final Material material = Material.matchMaterial(element);
        if (material == null) {
            throw new RuntimeException("Material \"" + element + "\" not found!");
        }

        return material;
    }
}

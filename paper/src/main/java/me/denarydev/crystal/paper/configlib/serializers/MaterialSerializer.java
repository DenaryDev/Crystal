package me.denarydev.crystal.paper.configlib.serializers;

import de.exlll.configlib.Serializer;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author DenaryDev
 * @since 22:37 12.08.2025
 */
@ApiStatus.AvailableSince("3.0.0")
public final class MaterialSerializer implements Serializer<Material, String> {

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

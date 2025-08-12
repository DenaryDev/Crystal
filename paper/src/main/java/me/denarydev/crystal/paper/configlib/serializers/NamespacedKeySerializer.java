package me.denarydev.crystal.paper.configlib.serializers;

import de.exlll.configlib.Serializer;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author DenaryDev
 * @since 22:24 12.08.2025
 */
@ApiStatus.AvailableSince("3.0.0")
public final class NamespacedKeySerializer implements Serializer<NamespacedKey, String> {

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

package me.denarydev.crystal.paper.configlib.serializers;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author DenaryDev
 * @since 22:21 12.08.2025
 */
@ApiStatus.AvailableSince("3.0.0")
public final class ComponentSerializer implements Serializer<Component, String> {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    public String serialize(Component element) {
        return MINI_MESSAGE.serialize(element);
    }

    @Override
    public Component deserialize(String element) {
        return MINI_MESSAGE.deserialize(element);
    }
}

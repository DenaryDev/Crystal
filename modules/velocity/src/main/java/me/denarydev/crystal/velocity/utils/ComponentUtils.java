/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.velocity.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Utilities for working with Adventure components via the MiniMessage format.
 */
@NullMarked
public final class ComponentUtils {

    /**
     * Deserializes a MiniMessage-formatted string into a {@link Component}.
     *
     * @param message the MiniMessage string.
     * @param tags    additional tag resolvers.
     * @return the parsed component.
     */
    public static Component deserialize(String message, TagResolver... tags) {
        return MiniMessage.miniMessage().deserialize(message, tags);
    }

    /**
     * Deserializes a list of MiniMessage-formatted strings into a list of {@link Component}s.
     *
     * @param message the list of MiniMessage strings.
     * @param tags    additional tag resolvers.
     * @return a list of parsed components.
     */
    public static List<Component> deserialize(List<String> message, TagResolver... tags) {
        return message.stream()
            .map(s -> MiniMessage.miniMessage().deserialize(s, tags))
            .toList();
    }

    /**
     * Serializes the given {@link Component} to a MiniMessage-formatted string.
     *
     * @param component the component to serialize.
     * @return the MiniMessage string.
     */
    public static String serialize(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    /**
     * Serializes a list of {@link Component}s to a list of MiniMessage-formatted strings.
     *
     * @param components the components to serialize.
     * @return a list of MiniMessage strings.
     */
    public static List<String> serialize(List<Component> components) {
        return components.stream()
            .map(c -> MiniMessage.miniMessage().serialize(c))
            .toList();
    }
}

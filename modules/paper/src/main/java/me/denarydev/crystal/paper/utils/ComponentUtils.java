package me.denarydev.crystal.paper.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Утилиты для работы с компонентами Adventure через формат MiniMessage.
 */
@NullMarked
public final class ComponentUtils {

    /**
     * Десериализует строку в формате MiniMessage в {@link Component}.
     *
     * @param message строка в формате MiniMessage
     * @param tags    дополнительные резолверы тегов
     * @return готовый компонент
     */
    public static Component deserialize(String message, TagResolver... tags) {
        return MiniMessage.miniMessage().deserialize(message, tags);
    }

    /**
     * Десериализует список строк в формате MiniMessage в список {@link Component}.
     *
     * @param message список строк в формате MiniMessage
     * @param tags    дополнительные резолверы тегов
     * @return список готовых компонентов
     */
    public static List<Component> deserialize(List<String> message, TagResolver... tags) {
        return message.stream()
            .map(s -> MiniMessage.miniMessage().deserialize(s, tags))
            .toList();
    }

    /**
     * Сериализует {@link Component} в строку формата MiniMessage.
     *
     * @param component компонент для сериализации
     * @return строка в формате MiniMessage
     */
    public static String serialize(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    /**
     * Сериализует список {@link Component} в список строк формата MiniMessage.
     *
     * @param components список компонентов для сериализации
     * @return список строк в формате MiniMessage
     */
    public static List<String> serialize(List<Component> components) {
        return components.stream()
            .map(c -> MiniMessage.miniMessage().serialize(c))
            .toList();
    }
}

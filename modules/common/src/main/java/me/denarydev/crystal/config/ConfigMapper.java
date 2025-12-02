/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.CheckedFunction;

/**
 * Получает экземпляры указанных классов из файла.
 * <p>
 * Подробнее: <a href="https://github.com/SpongePowered/Configurate/wiki/Object-Mapper">документация Configurate`s ObjectMapper (англ)</a>
 */
public final class ConfigMapper {

    /**
     * Загружает конфигурацию из файла и возвращает её в виде экземпляра указанного класса.
     *
     * @param loader загрузчик конфигурации, может быть получен из {@link ConfigLoaders}
     * @param clazz  тип класса конфига
     * @return созданный экземпляр класса конфига
     */
    public static <T> T load(@NotNull ConfigurationLoader<?> loader, @NotNull Class<T> clazz) throws ConfigurateException {
        return load(loader, clazz, false);
    }

    /**
     * Загружает конфигурацию из файла и возвращает её в виде экземпляра указанного класса.
     *
     * @param loader      загрузчик конфигурации, может быть получен из {@link ConfigLoaders}
     * @param clazz       тип класса конфига
     * @param refreshNode обновлять ли порядок параметров в файле
     * @return созданный экземпляр класса конфига
     */
    public static <T> T load(@NotNull ConfigurationLoader<?> loader, @NotNull Class<T> clazz, boolean refreshNode) throws ConfigurateException {
        final var creator = creator(clazz, refreshNode);

        final ConfigurationNode node;

        if (loader.canLoad()) {
            node = loader.load();
        } else {
            node = CommentedConfigurationNode.root(loader.defaultOptions());
        }

        final T instance = creator.apply(node);

        loader.save(node);

        return instance;
    }

    @NotNull
    private static <T> CheckedFunction<ConfigurationNode, T, @NotNull SerializationException> creator(@NotNull Class<T> type, boolean forceRefresh) {
        return node -> {
            T instance = node.require(type);
            if (forceRefresh) {
                node.set(type, instance);
            }

            return instance;
        };
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import io.sapphiremc.lib.configurate.CommentedConfigurationNode;
import io.sapphiremc.lib.configurate.ConfigurateException;
import io.sapphiremc.lib.configurate.ConfigurationNode;
import io.sapphiremc.lib.configurate.loader.ConfigurationLoader;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import io.sapphiremc.lib.configurate.util.CheckedFunction;
import org.jspecify.annotations.NonNull;

/**
 * Deserializes configuration files into instances of the specified class.
 * <p>
 * See: <a href="https://github.com/SpongePowered/Configurate/wiki/Object-Mapper">Configurate ObjectMapper documentation</a>
 * <p>
 * <b>You MUST use Configurate from the path <code>io.sapphiremc.lib.configurate</code>,
 * not <code>org.spongepowered.configurate</code></b>
 */
public final class ConfigMapper {

    /**
     * Loads the configuration from a file and returns it as an instance of the given class.
     *
     * @param loader the configuration loader, obtainable from {@link ConfigLoaders}
     * @param clazz  the class to deserialize into
     * @return the deserialized config instance
     */
    public static <T> T load(@NonNull ConfigurationLoader<?> loader, @NonNull Class<T> clazz) throws ConfigurateException {
        return load(loader, clazz, false);
    }

    /**
     * Loads the configuration from a file and returns it as an instance of the given class.
     *
     * @param loader      the configuration loader, obtainable from {@link ConfigLoaders}
     * @param clazz       the class to deserialize into
     * @param refreshNode whether to refresh the order of configuration keys in the file
     * @return the deserialized config instance
     */
    public static <T> T load(@NonNull ConfigurationLoader<?> loader, @NonNull Class<T> clazz, boolean refreshNode) throws ConfigurateException {
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

    @NonNull
    private static <T> CheckedFunction<ConfigurationNode, T, @NonNull SerializationException> creator(@NonNull Class<T> type, boolean forceRefresh) {
        return node -> {
            T instance = node.require(type);
            if (forceRefresh) {
                node.set(type, instance);
            }

            return instance;
        };
    }
}

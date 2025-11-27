/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.util.CheckedFunction;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public final class ConfigurateLoaders {

    /**
     * Returns hocon configuration loader.
     *
     * @param path Path to configuration file
     * @return {@link HoconConfigurationLoader}
     */
    public static HoconConfigurationLoader hocon(@NotNull final Path path) {
        return hocon(path, TypeSerializerCollection.defaults());
    }

    /**
     * Returns json configuration loader with custom serializers.
     *
     * @param path        Path to configuration file
     * @param serializers custom serializers
     * @return {@link HoconConfigurationLoader}
     */
    public static HoconConfigurationLoader hocon(@NotNull final Path path, @Nullable TypeSerializerCollection serializers) {
        final HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder()
            .path(path)
            .emitJsonCompatible(false);

        if (serializers != null) {
            return builder.defaultOptions(options -> options.serializers(b -> b.registerAll(serializers))).build();
        } else {
            return builder.build();
        }
    }

    /**
     * Returns json configuration loader with custom serializers.
     *
     * @param path    Path to configuration file
     * @param options Configuration options
     * @return {@link HoconConfigurationLoader}
     */
    public static HoconConfigurationLoader hocon(@NotNull final Path path, @NotNull ConfigurationOptions options) {
        return HoconConfigurationLoader.builder()
            .defaultOptions(options)
            .path(path)
            .emitJsonCompatible(false)
            .build();
    }

    /**
     * Returns yaml configuration loader.
     *
     * @param path Path to configuration file
     * @return {@link YamlConfigurationLoader}
     */
    public static YamlConfigurationLoader yaml(@NotNull final Path path) {
        return yaml(path, TypeSerializerCollection.defaults());
    }

    /**
     * Returns yaml configuration loader with custom serializers.
     *
     * @param path        Path to configuration file
     * @param serializers custom serializers
     * @return {@link YamlConfigurationLoader}
     */
    public static YamlConfigurationLoader yaml(@NotNull final Path path, @Nullable TypeSerializerCollection serializers) {
        final YamlConfigurationLoader.Builder builder = YamlConfigurationLoader.builder()
            .path(path)
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2);

        if (serializers != null) {
            return builder.defaultOptions(options -> options.serializers(b -> b.registerAll(serializers))).build();
        } else {
            return builder.build();
        }
    }

    /**
     * Returns yaml configuration loader with custom serializers.
     *
     * @param path    Path to configuration file
     * @param options Configuration options
     * @return {@link YamlConfigurationLoader}
     */
    public static YamlConfigurationLoader yaml(@NotNull final Path path, @NotNull ConfigurationOptions options) {
        return YamlConfigurationLoader.builder()
            .defaultOptions(options)
            .path(path)
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .build();
    }

    /**
     * Load configuration from file using hocon loader.
     *
     * @param path        Path to configuration file
     * @param clazz       Configuration class type
     * @param refreshNode force apply class to node
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final Path path,
                                   @NotNull final Class<T> clazz, final boolean refreshNode) throws ConfigurateException {
        return loadConfig(hocon(path), clazz, refreshNode, null);
    }

    /**
     * Load configuration from file using hocon loader.
     *
     * @param path           Path to configuration file
     * @param clazz          Configuration class type
     * @param refreshNode    force apply class to node
     * @param transformation configuration transformer (updater)
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final Path path,
                                   @NotNull final Class<T> clazz, final boolean refreshNode,
                                   @Nullable final ConfigurationTransformation.Versioned transformation) throws ConfigurateException {
        return loadConfig(hocon(path), clazz, refreshNode, transformation);
    }

    /**
     * Load configuration from file using hocon loader with custom serializers.
     *
     * @param path        Path to configuration file
     * @param serializers Custom serializers
     * @param clazz       Configuration class type
     * @param refreshNode force apply class to node
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final Path path, @Nullable TypeSerializerCollection serializers,
                                   @NotNull final Class<T> clazz, final boolean refreshNode) throws ConfigurateException {
        return loadConfig(hocon(path, serializers), clazz, refreshNode, null);
    }

    /**
     * Load configuration from file using hocon loader with custom serializers.
     *
     * @param path           Path to configuration file
     * @param serializers    Custom serializers
     * @param clazz          Configuration class type
     * @param refreshNode    force apply class to node
     * @param transformation configuration transformer (updater)
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final Path path, @Nullable TypeSerializerCollection serializers,
                                   @NotNull final Class<T> clazz, final boolean refreshNode,
                                   @Nullable final ConfigurationTransformation.Versioned transformation) throws ConfigurateException {
        return loadConfig(hocon(path, serializers), clazz, refreshNode, transformation);
    }

    /**
     * Load configuration from file using hocon loader with custom serializers.
     *
     * @param path        Path to configuration file
     * @param options     Configuration options
     * @param clazz       Configuration class type
     * @param refreshNode force apply class to node
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final Path path, @NotNull ConfigurationOptions options,
                                   @NotNull final Class<T> clazz, final boolean refreshNode) throws ConfigurateException {
        return loadConfig(hocon(path, options), clazz, refreshNode, null);
    }

    /**
     * Load configuration from file using hocon loader with custom serializers.
     *
     * @param path           Path to configuration file
     * @param options        Configuration options
     * @param clazz          Configuration class type
     * @param refreshNode    force apply class to node
     * @param transformation configuration transformer (updater)
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final Path path, @NotNull ConfigurationOptions options,
                                   @NotNull final Class<T> clazz, final boolean refreshNode,
                                   @Nullable final ConfigurationTransformation.Versioned transformation) throws ConfigurateException {
        return loadConfig(hocon(path, options), clazz, refreshNode, transformation);
    }

    /**
     * Load configuration from file using specified loader.
     *
     * @param loader      Configuration loader
     * @param clazz       Configuration class type
     * @param refreshNode force apply class to node
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final ConfigurationLoader<? extends @NotNull ConfigurationNode> loader,
                                   @NotNull final Class<T> clazz, final boolean refreshNode) throws ConfigurateException {
        return loadConfig(loader, clazz, refreshNode, null);
    }

    /**
     * Load configuration from file using specified loader.
     *
     * @param loader         Configuration loader
     * @param clazz          Configuration class type
     * @param refreshNode    force apply class to node
     * @param transformation configuration transformer (updater)
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final ConfigurationLoader<? extends @NotNull ConfigurationNode> loader,
                                   @NotNull final Class<T> clazz, final boolean refreshNode,
                                   @Nullable final ConfigurationTransformation.Versioned transformation) throws ConfigurateException {
        final var creator = creator(clazz, refreshNode);

        final ConfigurationNode node;

        if (loader.canLoad()) {
            node = loader.load();
        } else {
            node = CommentedConfigurationNode.root(loader.defaultOptions());
        }

        final T instance = creator.apply(node);

        if (transformation != null) {
            transformation.apply(node);
        }

        loader.save(node);

        return instance;
    }

    @NotNull
    private static <T> CheckedFunction<ConfigurationNode, T, @NotNull SerializationException> creator(@NotNull Class<T> type, boolean refreshNode) {
        return node -> {
            T instance = node.require(type);
            if (refreshNode) {
                node.set(type, instance);
            }
            return instance;
        };
    }
}

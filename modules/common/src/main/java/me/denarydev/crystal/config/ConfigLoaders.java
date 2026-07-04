/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import io.sapphiremc.lib.configurate.ConfigurationOptions;
import io.sapphiremc.lib.configurate.hocon.HoconConfigurationLoader;
import io.sapphiremc.lib.configurate.serialize.TypeSerializerCollection;
import io.sapphiremc.lib.configurate.yaml.NodeStyle;
import io.sapphiremc.lib.configurate.yaml.YamlConfigurationLoader;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

/**
 * Factory for creating Configurate configuration loaders.
 * <p>
 * Crystal uses a fork of Configurate that supports comments in YAML files.
 * <p>
 * <b>You MUST use Configurate from the path <code>io.sapphiremc.lib.configurate</code>,
 * not <code>org.spongepowered.configurate</code></b>
 */
public final class ConfigLoaders {

    /**
     * Returns a builder for a YAML configuration loader.
     * <p>
     * The following settings are pre-configured:
     * <ul>
     * <li><code>nodeStyle({@link NodeStyle#BLOCK})</code> — block style for a human-readable layout</li>
     * <li><code>indent(2)</code> — 2-space indentation (<i>the Configurate 4 default</i>)</li>
     * <li><code>splitLines(false)</code> — prevents long lines from being split</li>
     * </ul>
     *
     * @return a YAML configuration loader builder
     */
    public static YamlConfigurationLoader.Builder yamlBuilder() {
        return YamlConfigurationLoader.builder()
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .splitLines(false);
    }

    /**
     * Returns a YAML configuration loader with all default settings.
     *
     * @param path path to the configuration file
     * @return a new YAML configuration loader instance
     */
    public static YamlConfigurationLoader yaml(@NonNull Path path) {
        return yamlBuilder()
            .path(path)
            .build();
    }

    /**
     * Creates a YAML configuration loader with the given header.
     *
     * @param path   path to the configuration file
     * @param header the configuration file header
     * @return a new YAML configuration loader instance
     */
    public static YamlConfigurationLoader yaml(@NonNull Path path, @NonNull String header) {
        return yaml(path, options -> options.header(header));
    }

    /**
     * Creates a YAML configuration loader and registers the given type serializers.
     *
     * @param path        path to the configuration file
     * @param serializers type serializers to register
     * @return a new YAML configuration loader instance
     */
    public static YamlConfigurationLoader yaml(@NonNull Path path, @NonNull TypeSerializerCollection serializers) {
        return yaml(path, options -> options.serializers(b -> b.registerAll(serializers)));
    }

    /**
     * Creates a YAML configuration loader and applies the given options editor to it.
     *
     * @param path   path to the configuration file
     * @param editor editor for the loader's configuration options
     * @return a new YAML configuration loader instance
     */
    public static YamlConfigurationLoader yaml(@NonNull Path path, @NonNull UnaryOperator<ConfigurationOptions> editor) {
        return yamlBuilder().path(path)
            .defaultOptions(editor)
            .build();
    }

    /**
     * Returns a builder for a HOCON configuration loader with some pre-configured settings.
     *
     * @return a HOCON configuration loader builder
     */
    public static HoconConfigurationLoader.Builder hoconBuilder() {
        return HoconConfigurationLoader.builder()
            .emitJsonCompatible(false);
    }

    /**
     * Returns a HOCON configuration loader with all default settings.
     *
     * @param path path to the configuration file
     * @return a new HOCON configuration loader instance
     */
    public static HoconConfigurationLoader hocon(@NonNull Path path) {
        return hocon(path, options -> options);
    }

    /**
     * Creates a HOCON configuration loader with the given header.
     *
     * @param path   path to the configuration file
     * @param header the configuration file header
     * @return a new HOCON configuration loader instance
     */
    public static HoconConfigurationLoader hocon(@NonNull Path path, @NonNull String header) {
        return hocon(path, options -> options.header(header));
    }

    /**
     * Creates a HOCON configuration loader and registers the given type serializers.
     *
     * @param path        path to the configuration file
     * @param serializers type serializers to register
     * @return a new HOCON configuration loader instance
     */
    public static HoconConfigurationLoader hocon(@NonNull Path path, @NonNull TypeSerializerCollection serializers) {
        return hocon(path, options -> options.serializers(b -> b.registerAll(serializers)));
    }

    /**
     * Creates a HOCON configuration loader and applies the given options editor to it.
     *
     * @param path   path to the configuration file
     * @param editor editor for the loader's configuration options
     * @return a new HOCON configuration loader instance
     */
    public static HoconConfigurationLoader hocon(@NonNull Path path, @NonNull UnaryOperator<ConfigurationOptions> editor) {
        return HoconConfigurationLoader.builder()
            .path(path)
            .defaultOptions(editor)
            .emitJsonCompatible(false)
            .build();
    }
}

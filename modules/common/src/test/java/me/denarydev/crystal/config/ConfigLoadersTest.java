/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import io.sapphiremc.lib.configurate.hocon.HoconConfigurationLoader;
import io.sapphiremc.lib.configurate.serialize.TypeSerializerCollection;
import io.sapphiremc.lib.configurate.yaml.YamlConfigurationLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigLoadersTest {

    @TempDir
    Path tempDir;

    // --- YAML ---

    @Test
    void yamlBuilderIsNotNull() {
        assertNotNull(ConfigLoaders.yamlBuilder());
    }

    @Test
    void yamlCreatesLoader() {
        YamlConfigurationLoader loader = ConfigLoaders.yaml(tempDir.resolve("config.yml"));
        assertNotNull(loader);
        assertInstanceOf(YamlConfigurationLoader.class, loader);
    }

    @Test
    void yamlWithHeaderSetsHeader() {
        YamlConfigurationLoader loader = ConfigLoaders.yaml(tempDir.resolve("config.yml"), "My Header");
        assertEquals("My Header", loader.defaultOptions().header());
    }

    @Test
    void yamlWithSerializersCreatesLoader() {
        YamlConfigurationLoader loader = ConfigLoaders.yaml(tempDir.resolve("config.yml"), TypeSerializerCollection.defaults());
        assertNotNull(loader);
    }

    @Test
    void yamlWithOptionsEditorSetsHeader() {
        YamlConfigurationLoader loader = ConfigLoaders.yaml(tempDir.resolve("config.yml"), options -> options.header("Edited"));
        assertEquals("Edited", loader.defaultOptions().header());
    }

    // --- HOCON ---

    @Test
    void hoconBuilderIsNotNull() {
        assertNotNull(ConfigLoaders.hoconBuilder());
    }

    @Test
    void hoconCreatesLoader() {
        HoconConfigurationLoader loader = ConfigLoaders.hocon(tempDir.resolve("config.conf"));
        assertNotNull(loader);
        assertInstanceOf(HoconConfigurationLoader.class, loader);
    }

    @Test
    void hoconWithHeaderSetsHeader() {
        HoconConfigurationLoader loader = ConfigLoaders.hocon(tempDir.resolve("config.conf"), "HOCON Header");
        assertEquals("HOCON Header", loader.defaultOptions().header());
    }

    @Test
    void hoconWithSerializersCreatesLoader() {
        HoconConfigurationLoader loader = ConfigLoaders.hocon(tempDir.resolve("config.conf"), TypeSerializerCollection.defaults());
        assertNotNull(loader);
    }

    @Test
    void hoconWithOptionsEditorSetsHeader() {
        HoconConfigurationLoader loader = ConfigLoaders.hocon(tempDir.resolve("config.conf"), options -> options.header("Edited"));
        assertEquals("Edited", loader.defaultOptions().header());
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import io.sapphiremc.lib.configurate.ConfigurateException;
import io.sapphiremc.lib.configurate.objectmapping.ConfigSerializable;
import io.sapphiremc.lib.configurate.objectmapping.meta.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMapperTest {

    @TempDir
    Path tempDir;

    @Test
    void createsFileWithDefaultsWhenMissing() throws ConfigurateException {
        Path path = tempDir.resolve("config.yml");
        assertFalse(Files.exists(path));

        TestConfig config = ConfigMapper.load(ConfigLoaders.yaml(path), TestConfig.class);

        assertEquals(42, config.value());
        assertEquals("hello", config.name());
        assertTrue(Files.exists(path));
    }

    @Test
    void loadsExistingValues() throws ConfigurateException, IOException {
        Path path = tempDir.resolve("config.yml");
        Files.writeString(path, "value: 99\nname: world\n");

        TestConfig config = ConfigMapper.load(ConfigLoaders.yaml(path), TestConfig.class);

        assertEquals(99, config.value());
        assertEquals("world", config.name());
    }

    @Test
    void missingFieldsFallBackToDefaults() throws ConfigurateException, IOException {
        Path path = tempDir.resolve("config.yml");
        Files.writeString(path, "value: 7\n");

        TestConfig config = ConfigMapper.load(ConfigLoaders.yaml(path), TestConfig.class);

        assertEquals(7, config.value());
        assertEquals("hello", config.name());
    }

    @Test
    void refreshNodeWritesMissingFieldsToFile() throws ConfigurateException, IOException {
        Path path = tempDir.resolve("config.yml");
        Files.writeString(path, "value: 5\n");

        ConfigMapper.load(ConfigLoaders.yaml(path), TestConfig.class, true);

        String content = Files.readString(path);
        assertTrue(content.contains("name"));
    }

    @Test
    void nestedSectionIsDeserialized() throws ConfigurateException, IOException {
        Path path = tempDir.resolve("config.yml");
        Files.writeString(path, "nested:\n  score: 100\n");

        TestConfig config = ConfigMapper.load(ConfigLoaders.yaml(path), TestConfig.class);

        assertEquals(100, config.nested().score());
    }

    // --- fixtures ---

    @ConfigSerializable
    static final class TestConfig {

        @Comment("A test integer value.")
        private int value = 42;

        private String name = "hello";

        private NestedSection nested = new NestedSection();

        int value() {
            return value;
        }

        String name() {
            return name;
        }

        NestedSection nested() {
            return nested;
        }

        @ConfigSerializable
        static final class NestedSection {
            private int score = 0;

            int score() {
                return score;
            }
        }
    }
}

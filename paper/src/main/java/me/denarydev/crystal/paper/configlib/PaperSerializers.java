/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib;

import de.exlll.configlib.YamlConfigurationProperties;
import me.denarydev.crystal.config.ConfigLibLoader;
import me.denarydev.crystal.paper.configlib.serializers.ComponentSerializer;
import me.denarydev.crystal.paper.configlib.serializers.LocationSerializer;
import me.denarydev.crystal.paper.configlib.serializers.MaterialSerializer;
import me.denarydev.crystal.paper.configlib.serializers.NamespacedKeySerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

/**
 * @author DenaryDev
 * @since 22:52 12.08.2025
 */
public final class PaperSerializers {

    /**
     * Применяет кастомные Serializers к указанному билдеру свойств.
     *
     * @param builder билдер
     */
    public static void applyTo(YamlConfigurationProperties.Builder<?> builder) {
        builder
            .addSerializer(Component.class, new ComponentSerializer())
            .addSerializer(Location.class, new LocationSerializer())
            .addSerializer(Material.class, new MaterialSerializer())
            .addSerializer(NamespacedKey.class, new NamespacedKeySerializer());
    }

    /**
     * Возвращает готовые к использованию свойства для загрузки конфига через {@link de.exlll.configlib.YamlConfigurations}
     *
     * @return свойства в виде {@link YamlConfigurationProperties}
     */
    public static YamlConfigurationProperties getProperties() {
        return getProperties(null);
    }

    /**
     * Возвращает готовые к использованию свойства для загрузки конфига через {@link de.exlll.configlib.YamlConfigurations}
     *
     * @param header заголовок конфига
     * @return свойства в виде {@link YamlConfigurationProperties}
     */
    public static YamlConfigurationProperties getProperties(@Nullable String header) {
        final YamlConfigurationProperties.Builder<?> builder = ConfigLibLoader.properties();

        applyTo(builder);

        return builder.header(header).build();
    }
}

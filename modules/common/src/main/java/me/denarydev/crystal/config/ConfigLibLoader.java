/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @author DenaryDev
 * @since 1:46 28.10.2025
 * @deprecated ConfigLib не имеет некоторых важных и удобных, вместо неё
 * лучше использовать Configurate от SpongePowered ({@link ConfigLoaders}).
 * В Crystal используется мой форк, поддерживающий комментарии через аннотации {@link Comment}.
 * <p>
 * Оставлено для обратной совместимости с уже написанными плагинами.
 * Будет удалено в одном из будущих промежуточных релизов.
 */
@Deprecated(forRemoval = true)
public class ConfigLibLoader {
    private static final YamlConfigurationProperties.Builder<?> properties = YamlConfigurationProperties.newBuilder()
        .charset(StandardCharsets.UTF_8)
        .outputNulls(false)
        .inputNulls(false)
        .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE);

    /**
     * Загружает конфиг из файла и возвращает его при успешной загрузке,
     * но никак не изменяет файл.
     *
     * @param file  путь к файлу конфига.
     * @param clazz класс конфига.
     * @return загруженный конфиг или null, если не удалось загрузить.
     */
    public static <T> T loadConfig(Path file, Class<T> clazz) {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        return YamlConfigurations.load(file, clazz, properties.build());
    }

    /**
     * Загружает конфиг из файла и возвращает его при успешной загрузке,
     * но никак не изменяет файл.
     *
     * @param file       путь к файлу конфига.
     * @param clazz      класс конфига.
     * @param properties свойства загрузчика конфига.
     * @return загруженный конфиг или null, если не удалось загрузить.
     */
    public static <T> T loadConfig(Path file, Class<T> clazz, YamlConfigurationProperties properties) {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        return YamlConfigurations.load(file, clazz, properties);
    }

    /**
     * Загружает конфиг из файла и возвращает его при успешной загрузке,
     * а так же обновляет файл в соответствии с предоставленным классом.
     *
     * @param file   путь к файлу конфига.
     * @param clazz  класс конфига.
     * @param header заголовок конфига.
     * @return загруженный конфиг или null, если не удалось загрузить.
     */
    public static <T> T updateConfig(Path file, Class<T> clazz, String header) {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        return YamlConfigurations.update(file, clazz, properties.header(header).build());
    }

    /**
     * Загружает конфиг из файла и возвращает его при успешной загрузке,
     * а так же обновляет файл в соответствии с предоставленным классом.
     *
     * @param file       путь к файлу конфига.
     * @param clazz      класс конфига.
     * @param properties свойства загрузчика конфига.
     * @return загруженный конфиг или null, если не удалось загрузить.
     */
    public static <T> T updateConfig(Path file, Class<T> clazz, YamlConfigurationProperties properties) {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        return YamlConfigurations.update(file, clazz, properties);
    }

    /**
     * Сохраняет указанный объект конфига в файл.
     *
     * @param file     путь к файлу конфига.
     * @param clazz    класс конфига.
     * @param instance объект конфига.
     */
    public static <T> void saveConfig(Path file, Class<T> clazz, T instance) {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        YamlConfigurations.save(file, clazz, instance);
    }

    /**
     * Сохраняет указанный объект конфига в файл.
     *
     * @param file       путь к файлу конфига.
     * @param clazz      класс конфига.
     * @param instance   объект конфига.
     * @param properties свойства загрузчика конфига.
     */
    public static <T> void saveConfig(Path file, Class<T> clazz, T instance, YamlConfigurationProperties properties) {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        YamlConfigurations.save(file, clazz, instance, properties);
    }

    /**
     * Возвращает билдер свойств загрузчика конфига.
     */
    public static YamlConfigurationProperties.Builder<?> properties() {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        return properties;
    }
}

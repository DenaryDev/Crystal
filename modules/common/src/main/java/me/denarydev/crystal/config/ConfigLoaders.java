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
 * Класс для создания загрузчиков конфигураций из библиотеки Configurate.
 * <p>
 * Crystal использует свой форк Configurate с поддержкой комментариев в yaml файлах.
 * <p>
 * <b>Обязательно используйте Configurate из пути <code>io.sapphiremc.lib.configurate</code>,
 * не <code>org.spongepowered.configurate</code></b>
 */
public final class ConfigLoaders {

    /**
     * Возвращает создатель загрузчика конфигурации yaml.
     * <p>
     * Уже указанные нами параметры:
     * <ul>
     * <li><code>nodeStyle({@link NodeStyle#BLOCK})</code> - читаемый вид файла конфигурации</li>
     * <li><code>indent(2)</code> - отступы по 2 пробела (<i>по умолчанию у Configurate 4</i>)</li>
     * <li><code>splitLines(false)</code> - не разделять на части слишком длинные строки</li>
     * </ul>
     *
     * @return создатель загрузчика конфигурации yaml
     */
    public static YamlConfigurationLoader.Builder yamlBuilder() {
        return YamlConfigurationLoader.builder()
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .splitLines(false);
    }

    /**
     * Возвращает загрузчик конфигурации yaml со всеми значениями по умолчанию.
     *
     * @param path Путь к файлу конфигурации
     * @return новый экземпляр загрузчика конфигурации yaml
     */
    public static YamlConfigurationLoader yaml(@NonNull Path path) {
        return yamlBuilder()
            .path(path)
            .build();
    }

    /**
     * Создает загрузчик конфигурации yaml с указанным заголовком.
     *
     * @param path   путь к файлу конфигурации
     * @param header заголовок конфигурации
     * @return новый экземпляр загрузчика конфигурации yaml
     */
    public static YamlConfigurationLoader yaml(@NonNull Path path, @NonNull String header) {
        return yaml(path, options -> options.header(header));
    }

    /**
     * Создает загрузчик конфигурации yaml и добавляет к нему указанные сериализаторы.
     *
     * @param path        путь к файлу конфигурации
     * @param serializers сериализаторы
     * @return новый экземпляр загрузчика конфигурации yaml
     */
    public static YamlConfigurationLoader yaml(@NonNull Path path, @NonNull TypeSerializerCollection serializers) {
        return yaml(path, options -> options.serializers(b -> b.registerAll(serializers)));
    }

    /**
     * Создает загрузчик конфигурации yaml и применяет указанный редактор настроек к нему.
     *
     * @param path   путь к файлу конфигурации
     * @param editor редактор настроек загрузчика конфигурации
     * @return новый экземпляр загрузчика конфигурации yaml
     */
    public static YamlConfigurationLoader yaml(@NonNull Path path, @NonNull UnaryOperator<ConfigurationOptions> editor) {
        return yamlBuilder().path(path)
            .defaultOptions(editor)
            .build();
    }

    /**
     * Возвращает создатель загрузчика конфигурации hocon с некоторыми заданными параметрами.
     *
     * @return создатель загрузчика конфигурации yaml
     */
    public static HoconConfigurationLoader.Builder hoconBuilder() {
        return HoconConfigurationLoader.builder()
            .emitJsonCompatible(false);
    }

    /**
     * Возвращает загрузчик конфигурации hocon со всеми значениями по умолчанию.
     *
     * @param path Путь к файлу конфигурации
     * @return новый экземпляр загрузчика конфигурации hocon
     */
    public static HoconConfigurationLoader hocon(@NonNull Path path) {
        return hocon(path, options -> options);
    }

    /**
     * Создает загрузчик конфигурации hocon с указанным заголовком.
     *
     * @param path   путь к файлу конфигурации
     * @param header заголовок конфигурации
     * @return новый экземпляр загрузчика конфигурации hocon
     */
    public static HoconConfigurationLoader hocon(@NonNull Path path, @NonNull String header) {
        return hocon(path, options -> options.header(header));
    }

    /**
     * Создает загрузчик конфигурации hocon и добавляет к нему указанные сериализаторы.
     *
     * @param path        путь к файлу конфигурации
     * @param serializers сериализаторы
     * @return новый экземпляр загрузчика конфигурации hocon
     */
    public static HoconConfigurationLoader hocon(@NonNull Path path, @NonNull TypeSerializerCollection serializers) {
        return hocon(path, options -> options.serializers(b -> b.registerAll(serializers)));
    }

    /**
     * Создает загрузчик конфигурации hocon и применяет указанный редактор настроек к нему.
     *
     * @param path   путь к файлу конфигурации
     * @param editor редактор настроек загрузчика конфигурации
     * @return новый экземпляр загрузчика конфигурации yaml
     */
    public static HoconConfigurationLoader hocon(@NonNull Path path, @NonNull UnaryOperator<ConfigurationOptions> editor) {
        return HoconConfigurationLoader.builder()
            .path(path)
            .defaultOptions(editor)
            .emitJsonCompatible(false)
            .build();
    }
}

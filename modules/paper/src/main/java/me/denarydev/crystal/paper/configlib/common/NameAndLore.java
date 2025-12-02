/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib.common;

import de.exlll.configlib.Configuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Название и описание предмета для записи в конфиг через ConfigLib
 *
 * @author DenaryDev
 * @since 15:01 25.11.2025
 * @deprecated Используйте Configurate вместо ConfigLib.
 * <p>
 * Оставлено для обратной совместимости с уже написанными плагинами.
 * Будет удалено в одном из будущих промежуточных релизов.
 */
@Deprecated(forRemoval = true)
@Configuration
public final class NameAndLore {

    /**
     * Создаёт связку названия и описания предмета без указания описания.
     *
     * @param name название предмета
     * @return новый экземпляр этого класса, но без описания
     */
    @Contract(
        value = "_ -> new",
        pure = true
    )
    public static @NotNull NameAndLore of(@NotNull String name) {
        return new NameAndLore(name, null);
    }

    /**
     * Создаёт связку названия и описания предмета.
     *
     * @param name название предмета
     * @param lore описание предмета
     * @return новый экземпляр этого класса
     */
    @Contract(
        value = "_, _ -> new",
        pure = true
    )
    public static @NotNull NameAndLore of(@NotNull String name, @NotNull List<String> lore) {
        return new NameAndLore(name, lore);
    }

    private String name;
    private List<String> lore;

    private NameAndLore() {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
    }

    private NameAndLore(String name, List<String> lore) {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        this.name = name;
        this.lore = lore;
    }

    /**
     * Возвращает название предмета в виде строки.
     *
     * @return название предмета
     * @see NameAndLore#name(TagResolver...)
     */
    @NotNull
    public String rawName() {
        return name;
    }

    /**
     * Возвращает название предмета в виде компонента
     * с применением к нему форматирования MiniMessage.
     *
     * @param placeholders заполнители
     * @return форматированное название
     */
    @NotNull
    public Component name(TagResolver... placeholders) {
        return MiniMessage.miniMessage().deserialize(name, placeholders);
    }

    /**
     * Возвращает описание предмета в виде списка строк.
     *
     * @return описание предмета
     * @see NameAndLore#lore(TagResolver...)
     */
    public List<String> rawLore() {
        return lore;
    }

    /**
     * Возвращает описание предмета в виде списка компонентов
     * с применением к нему форматирования MiniMessage.
     *
     * @param tags заполнители
     * @return форматированное описание
     */
    @Nullable
    public List<Component> lore(TagResolver... tags) {
        if (lore == null) return null;

        return lore.stream()
            .map(line -> MiniMessage.miniMessage().deserialize(line, tags))
            .toList();
    }

    /**
     * Применяет название и описание к указанному предмету.
     *
     * @param item         предмет
     * @param placeholders заполнители, применяемые к названию и описанию
     * @return Этот же предмет, но с изменённым именем и описанием
     */
    @NotNull
    public ItemStack apply(@NotNull ItemStack item, TagResolver... placeholders) {
        item.editMeta(meta -> {
            meta.displayName(MiniMessage.miniMessage().deserialize(name, placeholders));
            if (lore != null) {
                meta.lore(lore.stream()
                    .map(line -> MiniMessage.miniMessage().deserialize(line, placeholders))
                    .toList());
            }
        });

        return item;
    }
}

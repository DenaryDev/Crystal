/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.item;

import com.google.common.base.Preconditions;
import me.denarydev.crystal.paper.utils.HeadUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Класс для создания ItemStack'ов.
 */
public final class ItemBuilder {
    @NotNull
    private final ItemStack itemStack;

    //region Методы создания билдера

    /**
     * Создает {@link ItemBuilder} для создания предмета.
     * <p>
     * Тип по умолчанию - воздух
     */
    public static ItemBuilder empty() {
        return new ItemBuilder(new ItemStack(Material.AIR));
    }

    /**
     * Создает {@link ItemBuilder} для создания предмета указанного типа.
     *
     * @param material тип предмета
     */
    public static ItemBuilder fromMaterial(@NotNull final Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    /**
     * Создает {@link ItemBuilder} для создания предмета на основе другого предмета.
     * <p>
     * Оригинальный предмет никак не изменяется.
     *
     * @param stack другой предмет для основы.
     */
    public static ItemBuilder fromItem(@NotNull final ItemStack stack) {
        return new ItemBuilder(stack.clone());
    }

    /**
     * Создает {@link ItemBuilder} для создания головы с текстурой.
     * <p>
     * Оригинальный предмет никак не изменяется.
     *
     * @param texture текстура скина
     */
    public static ItemBuilder playerHead(@NotNull final String texture) {
        return new ItemBuilder(new ItemStack(Material.PLAYER_HEAD))
            .texture(texture);
    }
    //endregion

    private ItemBuilder(@NotNull final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    //region Общие методы для всех типов предметов

    /**
     * Устанавливает тип предмета.
     *
     * @param type тип предмета.
     */
    public ItemBuilder type(@NotNull final Material type) {
        this.itemStack.setType(type);

        return this;
    }

    /**
     * Устанавливает количество предметов.
     *
     * @param amount количество предметов.
     */
    public ItemBuilder amount(final int amount) {
        Preconditions.checkArgument(amount > 0, "amount less than 1");
        Preconditions.checkArgument(amount <= 64, "amount greater than 64");

        this.itemStack.setAmount(amount);

        return this;
    }

    /**
     * Устанавливает имя предмета в виде {@link Component}.
     * <p>
     * Если будет передано null, название предмета не изменится.
     *
     * @param displayName имя предмета.
     */
    public ItemBuilder displayName(@Nullable final Component displayName) {
        return editMeta(meta -> meta.displayName(displayName));
    }

    /**
     * Устанавливает имя предмета, используя формат MiniMessage
     * <p>
     * Так же поддерживает методы для замены тегов.
     * <p>
     * Если будет передано null, название предмета не изменится.
     *
     * @param displayName имя предмета.
     * @param tags        методы для замены тегов.
     */
    public ItemBuilder displayNameRich(@Nullable final String displayName, @NotNull final TagResolver... tags) {
        if (displayName != null) {
            return displayName(MiniMessage.miniMessage().deserialize(displayName, tags));
        }

        return this;
    }

    /**
     * Устанавливает имя предмета, не применяя никакие форматы.
     * <p>
     * Если будет передано null, название предмета не изменится.
     *
     * @param displayName имя предмета.
     */
    public ItemBuilder displayNamePlain(@Nullable final String displayName) {
        if (displayName != null) {
            return displayName(Component.text(displayName));
        }

        return this;
    }

    /**
     * Устанавливает описание предмета из списка {@link Component}.
     * <p>
     * Если будет передано null, описание предмета не изменится.
     *
     * @param lore описание предмета.
     */
    public ItemBuilder lore(@Nullable final List<Component> lore) {
        return editMeta(meta -> meta.lore(lore));
    }

    /**
     * Устанавливает описание предмета, используя формат MiniMessage
     * <p>
     * Так же поддерживает методы для замены тегов.
     * <p>
     * Если будет передано null, описание предмета не изменится.
     *
     * @param lore описание предмета.
     * @param tags методы для замены тегов.
     */
    public ItemBuilder loreRich(@Nullable final List<String> lore, @NotNull final TagResolver... tags) {
        if (lore != null) {
            return this.lore(lore.stream()
                .map(line -> MiniMessage.miniMessage().deserialize(line, tags))
                .toList());
        }

        return this;
    }

    /**
     * Устанавливает описание предмета, не применяя никакие форматы.
     * <p>
     * Если будет передано null, описание предмета не изменится.
     *
     * @param lore описание предмета.
     */
    public ItemBuilder lorePlain(@Nullable final List<String> lore) {
        if (lore != null) {
            return this.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
        }

        return this;
    }

    /**
     * Добавляет к предмету указанные флаги.
     *
     * @param flags флаги для добавления.
     */
    public ItemBuilder itemFlags(@NotNull final ItemFlag... flags) {
        return this.editMeta(meta -> meta.addItemFlags(flags));
    }

    /**
     * Удаляет указанные флаги у предмета.
     *
     * @param flags флаги для удаления.
     */
    public ItemBuilder removeFlags(@NotNull final ItemFlag... flags) {
        return this.editMeta(meta -> meta.removeItemFlags(flags));
    }

    /**
     * Делает предмет нерушимым.
     * <p>
     * Работает только на предметах, которые имеют прочность.
     */
    public ItemBuilder unbreakable() {
        return this.unbreakable(true);
    }

    /**
     * Устанавливает, может ли предмет разрушаться.
     * <p>
     * Работает только на предметах, которые имеют прочность.
     *
     * @param unbreakable может ли предмет разрушаться.
     */
    public ItemBuilder unbreakable(final boolean unbreakable) {
        return this.editMeta(meta -> meta.setUnbreakable(unbreakable));
    }

    /**
     * Устанавливает степень повреждений предмета.
     * <p>
     * Работает только на предметах, которые имеют прочность.
     *
     * @param damage степень повреждений предмета.
     */
    public ItemBuilder damage(final int damage) {
        return this.editMeta(meta -> {
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damage);
            }
        });
    }

    /**
     * Устанавливает кастомдату, по которой определяют модельку предмета.
     * <p>
     * Обычно используется в связке с текстурпаком.
     *
     * @param data кастомдата модельки предмета.
     */
    public ItemBuilder customModelData(@Nullable final Integer data) {
        return this.editMeta(meta -> meta.setCustomModelData(data));
    }

    /**
     * Добавляет значение по указанному ключу в контейнер кастомных данных предмета.
     * <p>
     * Поддерживаемые типы ключей: {@link PersistentDataType}
     *
     * @param key   ключ, по которому сохранится значение.
     * @param value само значение.
     */
    public ItemBuilder persistentData(@NotNull final NamespacedKey key, @NotNull final Object value) {
        return this.editMeta(meta -> {
            final var container = meta.getPersistentDataContainer();

            if (value instanceof String s) container.set(key, PersistentDataType.STRING, s);
            else if (value instanceof Byte b) container.set(key, PersistentDataType.BYTE, b);
            else if (value instanceof Short s) container.set(key, PersistentDataType.SHORT, s);
            else if (value instanceof Integer i) container.set(key, PersistentDataType.INTEGER, i);
            else if (value instanceof Long l) container.set(key, PersistentDataType.LONG, l);
            else if (value instanceof Float f) container.set(key, PersistentDataType.FLOAT, f);
            else if (value instanceof Double d) container.set(key, PersistentDataType.DOUBLE, d);
            else if (value instanceof Enum<?> e) container.set(key, PersistentDataType.STRING, e.name());
            else if (value instanceof byte[] ba) container.set(key, PersistentDataType.BYTE_ARRAY, ba);
            else if (value instanceof int[] ia) container.set(key, PersistentDataType.INTEGER_ARRAY, ia);
            else if (value instanceof long[] la) container.set(key, PersistentDataType.LONG_ARRAY, la);
            else if (value instanceof PersistentDataContainer pdc) container.set(key, PersistentDataType.TAG_CONTAINER, pdc);
            else if (value instanceof PersistentDataContainer[] pdca) container.set(key, PersistentDataType.TAG_CONTAINER_ARRAY, pdca);
            else throw new IllegalArgumentException("Unknown persistent data type: " + value.getClass().getName());
        });
    }

    /**
     * Накладывает чару на предмет с указанным уровнем.
     *
     * @param enchantment тип чар для накладывания.
     * @param level       уровень чара.
     */
    public ItemBuilder enchantment(@NotNull final Enchantment enchantment, final int level) {
        return this.enchantments(Map.of(enchantment, level));
    }

    /**
     * Накладывает на предмет указанные чары.
     * <p>
     * Уровень каждой чары ставится на 1.
     *
     * @param enchantments чары для накладывания.
     */
    public ItemBuilder enchantments(@NotNull final Enchantment... enchantments) {
        final var map = new HashMap<Enchantment, Integer>();

        for (final var enchantment : enchantments) {
            map.putIfAbsent(enchantment, 1);
        }

        return this.enchantments(map);
    }

    /**
     * Накладывает на предмет указанные чары с указанными уровнями.
     *
     * @param enchantments чары для накладывания.
     */
    public ItemBuilder enchantments(@NotNull final Map<Enchantment, Integer> enchantments) {
        return this.editMeta(meta -> {
            for (final var enchantment : enchantments.entrySet()) {
                meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
            }
        });
    }
    //endregion

    //region Методы добавления эффектов к зельям

    /**
     * Устанавливает тип зелья.
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     *
     * @param type тип зелья.
     */
    public ItemBuilder potionType(@NotNull final PotionType type) {
        return this.editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                potion.setBasePotionData(new PotionData(type));
            }
        });
    }

    /**
     * Добавляет эффекты к предмету зелья.
     * <p>
     * Если уже есть эффект такого же типа, он перезапишется.
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     *
     * @param effects эффекты
     */
    public ItemBuilder potionEffects(@NotNull final PotionEffect... effects) {
        return this.potionEffects(true, effects);
    }

    /**
     * Добавляет эффекты к предмету зелья.
     * <p>
     * Если overwrite установить на true, то эффект перезапишет другой
     * эффект такого же типа, если он был.
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     *
     * @param overwrite перезаписывать ли эффекты
     * @param effects   эффекты
     */
    public ItemBuilder potionEffects(final boolean overwrite, @NotNull final PotionEffect... effects) {
        final var map = new HashMap<PotionEffect, Boolean>();

        for (final var effect : effects) {
            map.put(effect, overwrite);
        }

        return this.potionEffects(map);
    }

    /**
     * Добавляет эффекты к предмету зелья.
     * <p>
     * Для каждого эффекта указывается значение overwrite.
     * <p>
     * Если overwrite = true, то эффект перезапишет предыдущий с таким же типом
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     *
     * @param effects эффекты
     */
    public ItemBuilder potionEffects(@NotNull final Map<PotionEffect, Boolean> effects) {
        return this.editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                effects.forEach(potion::addCustomEffect);
            }
        });
    }

    /**
     * Удаляет эффекты из предмета зелья.
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     *
     * @param type тип эффекта для удаления.
     */
    public ItemBuilder removePotionEffect(@NotNull final PotionEffectType type) {
        return this.editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                potion.removeCustomEffect(type);
            }
        });
    }

    /**
     * Удаляет все эффекты из предмета зелья.
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     */
    public ItemBuilder clearPotionEffects() {
        return this.editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                potion.clearCustomEffects();
            }
        });
    }
    //endregion

    //region Методы сохранения чар в книгу зачарований.

    /**
     * Сохраняет в предмет указанную чару с указанным уровнем.
     * <p>
     * Применяется только к предмету с типом {@link Material#ENCHANTED_BOOK}
     *
     * @param enchantment тип чара для сохранения.
     * @param level       уровень чара.
     */
    public ItemBuilder storedEnchantment(@NotNull final Enchantment enchantment, final int level) {
        return this.storedEnchantments(Map.of(enchantment, level));
    }

    /**
     * Сохраняет в предмет указанные чары с первым уровнем для каждой.
     * <p>
     * Применяется только к предмету с типом {@link Material#ENCHANTED_BOOK}
     *
     * @param enchantments типs чар для сохранения.
     */
    public ItemBuilder storedEnchantments(@NotNull final Enchantment... enchantments) {
        final var map = new HashMap<Enchantment, Integer>();

        for (final var enchantment : enchantments) {
            map.putIfAbsent(enchantment, 1);
        }

        return this.storedEnchantments(map);
    }

    /**
     * Сохраняет в предмет указанные чары с указанными уровнями.
     * <p>
     * Применяется только к предмету с типом {@link Material#ENCHANTED_BOOK}
     *
     * @param enchants чары для сохранения.
     */
    public ItemBuilder storedEnchantments(@NotNull final Map<Enchantment, Integer> enchants) {
        return this.editMeta(meta -> {
            if (meta instanceof EnchantmentStorageMeta storage) {
                for (final var enchant : enchants.entrySet()) {
                    storage.addStoredEnchant(enchant.getKey(), enchant.getValue(), true);
                }
            }
        });
    }
    //endregion

    /**
     * Устанавливает текстуру для предмета с типом {@link Material#PLAYER_HEAD}
     *
     * @param texture текстура для предмета.
     */
    public ItemBuilder texture(@NotNull final String texture) {
        return editMeta(meta -> {
            if (meta instanceof SkullMeta skullMeta) {
                HeadUtils.setTexture(skullMeta, texture);
            }
        });
    }

    /**
     * Редактирует метаданные предметы.
     * <p>
     * Используйте, если вам не хватает методов этого билдера.
     *
     * @param editor метод редактирования меты предмета.
     */
    public ItemBuilder editMeta(@NotNull final Consumer<? super ItemMeta> editor) {
        this.itemStack.editMeta(editor);

        return this;
    }

    /**
     * Возвращает созданный и настроенный предмет.
     */
    @NotNull
    public ItemStack build() {
        return this.itemStack.clone();
    }

    /**
     * Клонирует этот билдер.
     */
    @NotNull
    public ItemBuilder duplicate() {
        return new ItemBuilder(this.itemStack.clone());
    }
}

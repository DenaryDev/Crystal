/*
 * Copyright (c) 2026 DenaryDev
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
import org.bukkit.Color;
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
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Класс для создания ItemStack'ов.
 */
public final class ItemBuilder {

    @NonNull
    private ItemStack itemStack;

    //region Методы создания билдера

    /**
     * Создает {@link ItemBuilder} для создания предмета.
     * <p>
     * Тип по умолчанию - воздух
     */
    public static ItemBuilder empty() {
        return new ItemBuilder(ItemStack.of(Material.AIR));
    }

    /**
     * Создает {@link ItemBuilder} для создания предмета указанного типа.
     *
     * @param material тип предмета
     */
    public static ItemBuilder fromMaterial(@NonNull Material material) {
        return new ItemBuilder(ItemStack.of(material));
    }

    /**
     * Создает {@link ItemBuilder} для создания предмета на основе другого предмета.
     * <p>
     * Оригинальный предмет никак не изменяется.
     *
     * @param stack другой предмет для основы.
     */
    public static ItemBuilder fromItem(@NonNull ItemStack stack) {
        return new ItemBuilder(stack.clone());
    }

    /**
     * Создает {@link ItemBuilder} для создания головы с текстурой.
     * <p>
     * Оригинальный предмет никак не изменяется.
     *
     * @param texture текстура скина
     */
    public static ItemBuilder playerHead(@NonNull String texture) {
        return new ItemBuilder(ItemStack.of(Material.PLAYER_HEAD))
            .texture(texture);
    }

    //endregion

    private ItemBuilder(@NonNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    //region Тип, название, описание

    /**
     * Устанавливает тип предмета.
     *
     * @param type тип предмета.
     */
    public ItemBuilder type(@NonNull Material type) {
        this.itemStack = this.itemStack.withType(type);

        return this;
    }

    /**
     * Устанавливает текстуру для предмета с типом {@link Material#PLAYER_HEAD}
     * <p>
     * Перед этим обязательно задайте тип через {@link #type(Material)}
     *
     * @param texture текстура для предмета.
     */
    public ItemBuilder texture(@NonNull String texture) {
        return editMeta(meta -> {
            if (meta instanceof SkullMeta skull) {
                HeadUtils.setTexture(skull, texture);
            }
        });
    }

    /**
     * Устанавливает количество предметов.
     *
     * @param amount количество предметов.
     */
    public ItemBuilder amount(int amount) {
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
    public ItemBuilder displayName(@Nullable Component displayName) {
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
    public ItemBuilder displayNameRich(@Nullable String displayName, @NonNull TagResolver... tags) {
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
    public ItemBuilder displayNamePlain(@Nullable String displayName) {
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
    public ItemBuilder lore(@Nullable List<Component> lore) {
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
    public ItemBuilder loreRich(@Nullable List<String> lore, @NonNull TagResolver... tags) {
        if (lore != null) {
            return lore(lore.stream()
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
    public ItemBuilder lorePlain(@Nullable List<String> lore) {
        if (lore != null) {
            return lore(lore.stream().map(Component::text).collect(Collectors.toList()));
        }

        return this;
    }

    //endregion

    //region Флаги, нерушимость, степень разрушения

    /**
     * Добавляет к предмету указанные флаги.
     *
     * @param flags флаги для добавления.
     */
    public ItemBuilder itemFlags(@NonNull ItemFlag... flags) {
        return editMeta(meta -> meta.addItemFlags(flags));
    }

    /**
     * Удаляет указанные флаги у предмета.
     *
     * @param flags флаги для удаления.
     */
    public ItemBuilder removeFlags(@NonNull ItemFlag... flags) {
        return editMeta(meta -> meta.removeItemFlags(flags));
    }

    /**
     * Делает предмет нерушимым.
     * <p>
     * Работает только на предметах, которые имеют прочность.
     */
    public ItemBuilder unbreakable() {
        return unbreakable(true);
    }

    /**
     * Устанавливает, может ли предмет разрушаться.
     * <p>
     * Работает только на предметах, которые имеют прочность.
     *
     * @param unbreakable может ли предмет разрушаться.
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        return editMeta(meta -> meta.setUnbreakable(unbreakable));
    }

    /**
     * Устанавливает степень повреждений предмета.
     * <p>
     * Работает только на предметах, которые имеют прочность.
     *
     * @param damage степень повреждений предмета.
     */
    public ItemBuilder damage(int damage) {
        return editMeta(meta -> {
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damage);
            }
        });
    }

    //endregion

    //region Управление моделькой и кастомдатой предмета

    /**
     * Устанавливает предмету указанную модель.
     *
     * @param model ключ модели предмета
     */
    public ItemBuilder itemModel(@NonNull NamespacedKey model) {
        return editMeta(meta -> meta.setItemModel(model));
    }

    /**
     * Добавляет список указанных чисел с плавающей запятой в компонент кастомдаты предмета.
     * <p>
     * Обычно используется в связке с текстурпаком.
     *
     * @param floats список чисел с плавающей запятой
     * @see CustomModelDataComponent#setFloats(List)
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataFloats(@NonNull List<Float> floats) {
        return editMeta(meta -> {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFloats(floats);
            meta.setCustomModelDataComponent(component);
        });
    }

    /**
     * Добавляет список указанных флагов в компонент кастомдаты предмета.
     * <p>
     * Обычно используется в связке с текстурпаком.
     *
     * @param flags список флагов
     * @see CustomModelDataComponent#setFlags(List)
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataFlags(@NonNull List<Boolean> flags) {
        return editMeta(meta -> {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFlags(flags);
            meta.setCustomModelDataComponent(component);
        });
    }

    /**
     * Добавляет список указанных строк в компонент кастомдаты предмета.
     * <p>
     * Обычно используется в связке с текстурпаком.
     *
     * @param strings список строк
     * @see CustomModelDataComponent#setStrings(List)
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataStrings(@NonNull List<String> strings) {
        return editMeta(meta -> {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setStrings(strings);
            meta.setCustomModelDataComponent(component);
        });
    }

    /**
     * Добавляет список указанных цветов в компонент кастомдаты предмета.
     * <p>
     * Обычно используется в связке с текстурпаком.
     *
     * @param colors список цветов
     * @see CustomModelDataComponent#setColors(List)
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataColors(@NonNull List<Color> colors) {
        return editMeta(meta -> {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setColors(colors);
            meta.setCustomModelDataComponent(component);
        });
    }

    /**
     * Устанавливает кастомдату, по которой определяют модельку предмета.
     * <p>
     * Обычно используется в связке с текстурпаком.
     *
     * @param data кастомдата модельки предмета.
     * @deprecated используйте {@link #customModelDataFloats(List)}
     */
    @Deprecated(since = "3.0.0")
    public ItemBuilder customModelData(@Nullable Integer data) {
        return editMeta(meta -> meta.setCustomModelData(data));
    }

    //endregion

    //region Наложение чаров на предмет

    /**
     * Накладывает чару на предмет с указанным уровнем.
     *
     * @param enchantment тип чар для накладывания.
     * @param level       уровень чара.
     */
    public ItemBuilder enchantment(@NonNull Enchantment enchantment, int level) {
        return enchantments(Map.of(enchantment, level));
    }

    /**
     * Накладывает на предмет указанные чары.
     * <p>
     * Уровень каждой чары ставится на 1.
     *
     * @param enchantments чары для накладывания.
     */
    public ItemBuilder enchantments(@NonNull Enchantment... enchantments) {
        final Map<Enchantment, Integer> map = new HashMap<>();

        for (Enchantment enchantment : enchantments) {
            map.putIfAbsent(enchantment, 1);
        }

        return enchantments(map);
    }

    /**
     * Накладывает на предмет указанные чары с указанными уровнями.
     *
     * @param enchantments чары для накладывания.
     */
    public ItemBuilder enchantments(@NonNull Map<Enchantment, Integer> enchantments) {
        return editMeta(meta -> {
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
            }
        });
    }

    //endregion

    //region Добавление эффектов к зельям

    /**
     * Устанавливает тип зелья.
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     *
     * @param type тип зелья.
     */
    public ItemBuilder potionType(@NonNull PotionType type) {
        return editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                potion.setBasePotionType(type);
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
    public ItemBuilder potionEffects(@NonNull PotionEffect... effects) {
        return potionEffects(true, effects);
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
    public ItemBuilder potionEffects(boolean overwrite, @NonNull PotionEffect... effects) {
        final Map<PotionEffect, Boolean> map = new HashMap<>();

        for (PotionEffect effect : effects) {
            map.put(effect, overwrite);
        }

        return potionEffects(map);
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
    public ItemBuilder potionEffects(@NonNull Map<PotionEffect, Boolean> effects) {
        return editMeta(meta -> {
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
    public ItemBuilder removePotionEffect(@NonNull PotionEffectType type) {
        return editMeta(meta -> {
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
        return editMeta(meta -> {
            if (meta instanceof PotionMeta potion) {
                potion.clearCustomEffects();
            }
        });
    }

    //endregion

    //region Сохранение чар в книгу зачарований

    /**
     * Сохраняет в предмет указанную чару с указанным уровнем.
     * <p>
     * Применяется только к предмету с типом {@link Material#ENCHANTED_BOOK}
     *
     * @param enchantment тип чара для сохранения.
     * @param level       уровень чара.
     */
    public ItemBuilder storedEnchantment(@NonNull Enchantment enchantment, int level) {
        return storedEnchantments(Map.of(enchantment, level));
    }

    /**
     * Сохраняет в предмет указанные чары с первым уровнем для каждой.
     * <p>
     * Применяется только к предмету с типом {@link Material#ENCHANTED_BOOK}
     *
     * @param enchantments типs чар для сохранения.
     */
    public ItemBuilder storedEnchantments(@NonNull Enchantment... enchantments) {
        final Map<Enchantment, Integer> map = new HashMap<>();

        for (Enchantment enchantment : enchantments) {
            map.putIfAbsent(enchantment, 1);
        }

        return storedEnchantments(map);
    }

    /**
     * Сохраняет в предмет указанные чары с указанными уровнями.
     * <p>
     * Применяется только к предмету с типом {@link Material#ENCHANTED_BOOK}
     *
     * @param enchantments чары для сохранения.
     */
    public ItemBuilder storedEnchantments(@NonNull Map<Enchantment, Integer> enchantments) {
        return editMeta(meta -> {
            if (meta instanceof EnchantmentStorageMeta storage) {
                for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                    storage.addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                }
            }
        });
    }

    //endregion

    //region Контейнер кастомных данных предмета

    /**
     * Добавляет значение по указанному ключу в контейнер кастомных данных предмета.
     * <p>
     * Поддерживаемые типы ключей: {@link PersistentDataType}
     *
     * @param key   ключ, по которому сохранится значение.
     * @param value само значение.
     */
    public ItemBuilder persistentData(@NonNull NamespacedKey key, @NonNull Object value) {
        return editMeta(meta -> {
            final PersistentDataContainer container = meta.getPersistentDataContainer();

            switch (value) {
                case Byte b -> container.set(key, PersistentDataType.BYTE, b);
                case Short s -> container.set(key, PersistentDataType.SHORT, s);
                case Integer i -> container.set(key, PersistentDataType.INTEGER, i);
                case Long l -> container.set(key, PersistentDataType.LONG, l);
                case Float f -> container.set(key, PersistentDataType.FLOAT, f);
                case Double d -> container.set(key, PersistentDataType.DOUBLE, d);
                case Boolean b -> container.set(key, PersistentDataType.BOOLEAN, b);
                case String s -> container.set(key, PersistentDataType.STRING, s);
                case Enum<?> e -> container.set(key, PersistentDataType.STRING, e.name());
                case byte[] ba -> container.set(key, PersistentDataType.BYTE_ARRAY, ba);
                case int[] ia -> container.set(key, PersistentDataType.INTEGER_ARRAY, ia);
                case long[] la -> container.set(key, PersistentDataType.LONG_ARRAY, la);
                case PersistentDataContainer pdc -> container.set(key, PersistentDataType.TAG_CONTAINER, pdc);
                default -> throw new IllegalArgumentException("Unknown persistent data type: " + value.getClass().getName());
            }
        });
    }

    //endregion

    /**
     * Редактирует метаданные предметы.
     * <p>
     * Используйте, если вам не хватает методов этого билдера.
     *
     * @param editor метод редактирования меты предмета.
     */
    public ItemBuilder editMeta(@NonNull Consumer<? super ItemMeta> editor) {
        this.itemStack.editMeta(editor);

        return this;
    }

    /**
     * Возвращает созданный и настроенный предмет.
     */
    @NonNull
    public ItemStack build() {
        return this.itemStack.clone();
    }

    /**
     * Клонирует этот билдер.
     */
    @NonNull
    public ItemBuilder duplicate() {
        return new ItemBuilder(this.itemStack.clone());
    }
}

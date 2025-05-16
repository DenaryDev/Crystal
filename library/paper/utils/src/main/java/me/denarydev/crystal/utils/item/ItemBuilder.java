/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils.item;

import me.denarydev.crystal.utils.ItemUtils;
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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Класс для создания ItemStack'ов.
 * <p>
 * Для создания стака с нуля, используйте {@link #empty()}.
 * <p>
 * Для создания стака на основе другого стака,
 * используйте {@link #fromStack(ItemStack)}.
 */
@ApiStatus.AvailableSince("2.2.0")
public class ItemBuilder {
    @Nullable
    private final ItemStack baseStack;

    private Consumer<? super ItemMeta> metaEditor;
    private Material type;
    private String texture;
    private int amount;
    private Component displayName;
    private List<Component> lore;
    private ItemFlag[] itemFlags;
    private boolean unbreakable;
    private Integer customModelData;
    private int damage;
    private PotionType potionType;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private final Map<PotionEffect, Boolean> potionEffects = new HashMap<>();
    private final Map<Enchantment, Integer> storedEnchantments = new HashMap<>();
    private final Map<NamespacedKey, Object> persistentData = new HashMap<>();

    private ItemBuilder(@Nullable final ItemStack stack) {
        this.baseStack = stack;
        if (stack != null) {
            this.customModelData = baseStack.getItemMeta().getCustomModelData();
        }
    }

    //region Методы создания билдера

    /**
     * Создает пустой {@link ItemBuilder} для создания {@link ItemStack}.
     *
     * @return пустой {@link ItemBuilder}.
     */
    public static ItemBuilder empty() {
        return new ItemBuilder(null);
    }

    /**
     * Создает {@link ItemBuilder} для создания {@link ItemStack} на основе другого ItemStack.
     * <p>
     * Оригинальный ItemStack никак не изменяется.
     *
     * @param stack другой ItemStack для основы.
     * @return {@link ItemBuilder} на основе другого ItemStack.
     */
    public static ItemBuilder fromStack(@NotNull final ItemStack stack) {
        return new ItemBuilder(stack);
    }
    //endregion

    //region Общие методы для всех типов предметов

    /**
     * Устанавливает тип предмета.
     *
     * @param type тип предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder type(@NotNull final Material type) {
        this.type = type;
        return this;
    }

    /**
     * Устанавливает текстуру для предмета с типом {@link Material#PLAYER_HEAD}
     *
     * @param texture текстура для предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder texture(@NotNull final String texture) {
        this.texture = texture;
        return this;
    }

    /**
     * Устанавливает количество предметов.
     *
     * @param amount количество предметов.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder amount(final int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Позволяет редактировать мету предмета при создании.
     * <p>
     * Применяется прежде, чем будут применены методы ниже.
     * <p>
     * Используйте, если вам не хватает методов этого билдера.
     *
     * @param editor метод редактирования меты предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder editMeta(@NotNull final Consumer<? super ItemMeta> editor) {
        this.metaEditor = editor;
        return this;
    }

    /**
     * Устанавливает имя предмета в виде {@link Component}.
     * <p>
     * Если будет передано null, название предмета не изменится.
     *
     * @param displayName имя предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder displayName(@Nullable final Component displayName) {
        this.displayName = displayName;
        return this;
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
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder displayNameRich(@Nullable final String displayName, @NotNull final TagResolver... tags) {
        if (displayName != null) {
            this.displayName = MiniMessage.miniMessage().deserialize(displayName, tags);
        }
        return this;
    }

    /**
     * Устанавливает имя предмета, не применяя никакие форматы.
     * <p>
     * Если будет передано null, название предмета не изменится.
     *
     * @param displayName имя предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder displayNamePlain(@Nullable final String displayName) {
        if (displayName != null) {
            this.displayName = Component.text(displayName);
        }
        return this;
    }

    /**
     * Устанавливает описание предмета из списка {@link Component}.
     * <p>
     * Если будет передано null, описание предмета не изменится.
     *
     * @param lore описание предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder lore(@Nullable final List<Component> lore) {
        this.lore = lore;
        return this;
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
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder loreRich(@Nullable final List<String> lore, @NotNull final TagResolver... tags) {
        if (lore != null) {
            this.lore = new ArrayList<>();
            for (final String line : lore) {
                this.lore.add(MiniMessage.miniMessage().deserialize(line, tags));
            }
        }
        return this;
    }

    /**
     * Устанавливает описание предмета, не применяя никакие форматы.
     * <p>
     * Если будет передано null, описание предмета не изменится.
     *
     * @param lore описание предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder lorePlain(@Nullable final List<String> lore) {
        if (lore != null) {
            this.lore = new ArrayList<>();
            for (final String line : lore) {
                this.lore.add(Component.text(line));
            }
        }
        return this;
    }

    /**
     * Добавляет к предмету указанные флаги.
     *
     * @param flags флаги для добавления.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder itemFlags(@NotNull final ItemFlag... flags) {
        this.itemFlags = flags;
        return this;
    }

    /**
     * Устанавливает, может ли предмет разрушаться.
     * <p>
     * Работает только на предметах, которые имеют прочность.
     *
     * @param unbreakable может ли предмет разрушаться.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder unbreakable(final boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    /**
     * Устанавливает степень повреждений предмета.
     * <p>
     * Работает только на предметах, которые имеют прочность.
     *
     * @param damage степень повреждений предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder damage(final int damage) {
        this.damage = damage;
        return this;
    }

    /**
     * Устанавливает кастомдату, по которой определяют модельку предмета.
     * <p>
     * Обычно используется в связке с текстурпаком.
     *
     * @param data кастомдата модельки предмета.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder customModelData(@Nullable final Integer data) {
        this.customModelData = data;
        return this;
    }

    /**
     * Добавляет значение по указанному ключу в контейнер предмета.
     * <p>
     * Поддерживаемые типы ключей: {@link PersistentDataType}
     *
     * @param key   ключ, по которому сохранится значение.
     * @param value само значение.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder persistentData(@NotNull final NamespacedKey key, @NotNull final Object value) {
        this.persistentData.put(key, value);
        return this;
    }

    //region Накладывание чар на предметы

    /**
     * Накладывает чару на предмет с указанным уровнем.
     *
     * @param enchantment тип чар для накладывания.
     * @param level       уровень чара.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder enchantment(@NotNull final Enchantment enchantment, final int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Накладывает на предмет чары первого уровня.
     *
     * @param enchantments чары для накладывания.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder enchantments(@NotNull final Enchantment... enchantments) {
        for (final var enchantment : enchantments) {
            this.enchantments.putIfAbsent(enchantment, 1);
        }
        return this;
    }

    /**
     * Накладывает на предмет указанные чары с указанными уровнями.
     *
     * @param enchantments чары для накладывания.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder enchantments(@NotNull final Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }
    //endregion
    //endregion

    //region Методы добавления эффектов к зельям

    /**
     * Устанавливает тип зелья.
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     *
     * @param type тип зелья.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder potionType(@NotNull final PotionType type) {
        this.potionType = type;
        return this;
    }

    /**
     * Добавляет эффекты зелий к предмету.
     * <p>
     * Эффект не применится, если уже есть эффект такого же типа.
     * <p>
     * Работает только если тип предмета {@link Material#POTION},
     * {@link Material#SPLASH_POTION} или {@link Material#LINGERING_POTION}.
     *
     * @param effects эффекты зелий.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder potionEffect(@NotNull final PotionEffect... effects) {
        for (final var effect : effects) {
            this.potionEffects.putIfAbsent(effect, false);
        }
        return this;
    }

    /**
     * Добавляет эффект зелья к предмету.
     * <p>
     * Если override установить на true, то эффект перезапишет другой
     * эффект такого же типа, если он был.
     *
     * @param effect   эффект зелья
     * @param override должен ли быть перезаписан уже существующие эффекты такого же типа
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder potionEffect(@NotNull final PotionEffect effect, final boolean override) {
        this.potionEffects.put(effect, override);
        return this;
    }

    /**
     * Добавляет эффекты зелий к предмету.
     * <p>
     * Для каждого эффекта устанавливается значение override.
     * <p>
     * Если override = true, то эффект перезапишет предыдущий с таким же типом
     *
     * @param effects эффекты зелий.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder potionEffects(@NotNull final Map<PotionEffect, Boolean> effects) {
        this.potionEffects.putAll(effects);
        return this;
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
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder storedEnchant(@NotNull final Enchantment enchantment, final int level) {
        this.storedEnchantments.put(enchantment, level);
        return this;
    }

    /**
     * Сохраняет в предмет указанные чары с первым уровнем для каждой.
     * <p>
     * Применяется только к предмету с типом {@link Material#ENCHANTED_BOOK}
     *
     * @param enchantments типs чар для сохранения.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder storedEnchants(@NotNull final Enchantment... enchantments) {
        for (final var enchantment : enchantments) {
            this.storedEnchantments.put(enchantment, 1);
        }
        return this;
    }

    /**
     * Сохраняет в предмет указанные чары с указанными уровнями.
     * <p>
     * Применяется только к предмету с типом {@link Material#ENCHANTED_BOOK}
     *
     * @param enchants чары для сохранения.
     * @return {@link ItemBuilder} для дальнейшего использования.
     */
    public ItemBuilder storedEnchants(@NotNull final Map<Enchantment, Integer> enchants) {
        this.storedEnchantments.putAll(enchants);
        return this;
    }
    //endregion

    /**
     * Завершает создание и возвращает получившийся {@link ItemStack}.
     *
     * @return созданный ItemStack.
     */
    @NotNull
    public ItemStack build() {
        final ItemStack item;
        if (baseStack != null) {
            item = baseStack.clone();
            if (texture != null) {
                final var headMeta = (SkullMeta) ItemUtils.createHead(texture).getItemMeta();
                final var itemMeta = (SkullMeta) item.getItemMeta();
                itemMeta.setPlayerProfile(headMeta.getPlayerProfile());
                item.setItemMeta(itemMeta);
            } else if (type != null) {
                item.setType(type);
            }
            if (amount > 0 && amount <= 64) {
                item.setAmount(amount);
            }
        } else {
            if (texture != null) {
                item = ItemUtils.createHead(texture, Math.max(Math.min(amount, 64), 1));
            } else if (type != null) {
                item = new ItemStack(type, Math.max(Math.min(amount, 64), 1));
            } else {
                throw new IllegalArgumentException("The ItemStack type or texture must be present!");
            }
        }

        if (metaEditor != null) {
            item.editMeta(metaEditor);
        }

        final var meta = item.getItemMeta();

        if (displayName != null) {
            meta.displayName(displayName);
        }
        if (lore != null) {
            meta.lore(lore);
        }

        if (!enchantments.isEmpty()) {
            enchantments.forEach((enchantment, level) -> {
                if (!meta.hasEnchant(enchantment) && level > 0) {
                    meta.addEnchant(enchantment, level, true);
                }
            });
        }

        if (itemFlags != null) {
            meta.addItemFlags(itemFlags);
        }

        meta.setCustomModelData(customModelData);

        if (damage > 0 && meta instanceof Damageable damageable) {
            damageable.setDamage(damage);
        }

        meta.setUnbreakable(unbreakable);

        if (!persistentData.isEmpty()) {
            final var container = meta.getPersistentDataContainer();
            persistentData.forEach((key, value) -> {
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
            });
        }

        if (meta instanceof PotionMeta potion) {
            if (potionType != null) potion.setBasePotionData(new PotionData(potionType));
            if (!potionEffects.isEmpty()) potionEffects.forEach(potion::addCustomEffect);
        }

        if (!storedEnchantments.isEmpty() && meta instanceof EnchantmentStorageMeta storage) {
            storedEnchantments.forEach((enchantment, lvl) -> {
                if (!storage.hasStoredEnchant(enchantment)) {
                    storage.addStoredEnchant(enchantment, lvl, true);
                }
            });
        }

        item.setItemMeta(meta);
        return item;
    }
}

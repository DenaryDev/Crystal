/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib.common;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.PostProcess;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.paper.utils.HeadUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Этот класс можно использовать для сохранения {@link ItemStack} в конфиг
 * через ConfigLib.
 *
 * @author DenaryDev
 * @since 23:09 12.08.2025
 * @deprecated Используйте Configurate вместо ConfigLib.
 * Configurate может записывать ItemStack напрямую через {@link me.denarydev.crystal.paper.configurate.serializers.ItemStackSerializer}
 * <p>
 * Оставлено для обратной совместимости с уже написанными плагинами.
 * Будет удалено в одном из будущих промежуточных релизов.
 */
@Deprecated(forRemoval = true)
@SuppressWarnings("FieldMayBeFinal")
@Configuration
public final class ItemSettings {

    private Material material = null;
    private Integer amount = null;
    private String texture = null;
    private String name = null;
    private List<String> lore = null;
    private Boolean unbreakable = null;
    private List<ItemFlag> flags = null;
    private Integer damage = null;
    private Map<String, Integer> enchants = null;

    /**
     * Создаёт экземпляр ItemSettings с указанным материалом.
     *
     * @param material материал предмета
     * @return новый экземпляр ItemSettings
     */
    public static ItemSettings of(@NotNull Material material) {
        return of(new ItemStack(material));
    }

    /**
     * Создаёт экземпляр ItemSettings с головой с указанной текстурой.
     *
     * @param texture текстура головы
     * @return новый экземпляр ItemSettings
     */
    public static ItemSettings of(@NotNull String texture) {
        return of(HeadUtils.createHead(texture));
    }

    /**
     * Создаёт объект ItemSettings, беря параметры из указанного предмета.
     *
     * @param stack предмет
     * @return новый экземпляр ItemSettings
     */
    @Contract(
        value = "_ -> new",
        pure = true
    )
    public static ItemSettings of(@NotNull ItemStack stack) {
        return new ItemSettings(stack);
    }

    private ItemSettings() {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
    }

    private ItemSettings(@NotNull ItemStack stack) {
        System.err.println("Detected ConfigLib usage. It is deprecated and will be removed in near future");
        final ItemMeta meta = stack.getItemMeta();
        if (meta instanceof SkullMeta skull) {
            final PlayerProfile profile = skull.getPlayerProfile();
            if (profile != null) {
                profile.getProperties().stream()
                    .filter(p -> p.getName().equals("textures"))
                    .findFirst()
                    .ifPresent(property -> this.texture = property.getValue());
            }
        }

        if (this.texture == null) {
            this.material = stack.getType();
        }

        this.amount = stack.getAmount();

        final Component displayName = meta.displayName();
        if (displayName != null) {
            this.name = MiniMessage.miniMessage().serialize(displayName);
        }

        final List<Component> lore = meta.lore();
        if (lore != null) {
            this.lore = lore.stream()
                .map(MiniMessage.miniMessage()::serialize)
                .toList();
        }

        if (meta.isUnbreakable()) {
            this.unbreakable = true;
        }

        final Set<ItemFlag> itemFlags = meta.getItemFlags();
        if (!itemFlags.isEmpty()) {
            this.flags = new ArrayList<>(itemFlags);
        }

        if (meta instanceof Damageable damageable && damageable.hasDamage()) {
            this.damage = damageable.getDamage();
        }

        if (meta.hasEnchants()) {
            meta.getEnchants().forEach((enchantment, level) ->
                this.enchants.put(enchantment.getKey().toString(), level));
        }
    }

    @PostProcess
    private void verify() {
        if (material == null && texture == null) {
            throw new IllegalStateException("You must specify item material or head texture");
        }
    }

    /**
     * Возвращает материал предмета.
     * <p>
     * Если указана текстура, вернёт {@link Material#PLAYER_HEAD} независимо от указанного материала.
     *
     * @return материал предмета
     */
    @NotNull
    public Material material() {
        return texture != null ? Material.PLAYER_HEAD : material;
    }

    /**
     * Возвращает текстуру головы, если указана.
     *
     * @return текстура головы
     */
    @Nullable
    public String texture() {
        return texture;
    }

    /**
     * Возвращает кол-во предметов в стаке, если указано.
     *
     * @return кол-во предметов
     */
    public Integer amount() {
        return amount;
    }

    /**
     * Устанавливает кол-во предметов в стаке.
     *
     * @param amount кол-во предметов в стаке
     */
    public ItemSettings amount(int amount) {
        this.amount = amount;

        return this;
    }

    /**
     * Возвращает название предмета без применения какого-либо форматирования, если указано.
     *
     * @return название предмета
     */
    @Nullable
    private String displayName() {
        return name;
    }

    /**
     * Устанавливает название предмета.
     *
     * @param name название предмета
     */
    public ItemSettings displayName(@NotNull String name) {
        this.name = name;

        return this;
    }

    /**
     * Возвращает описание предмета без применения какого-либо форматирования, если указано.
     *
     * @return описание предмета
     */
    @Nullable
    public List<String> lore() {
        return lore;
    }

    /**
     * Устанавливает описание предмета
     *
     * @param lore описание предмета
     */
    public ItemSettings lore(@NotNull List<String> lore) {
        this.lore = lore;

        return this;
    }

    /**
     * Возвращает флаг неразрушаемости предмета.
     * <p>
     * Только для предметов, имеющих прочность.
     *
     * @return флаг неразрушаемости предмета
     */
    @Nullable
    public Boolean unbreakable() {
        return unbreakable;
    }

    /**
     * Устанавливает флаг неразрушаемости предмета.
     * <p>
     * Только для предметов, имеющих прочность.
     *
     * @param unbreakable флаг неразрушаемости предмета
     */
    public ItemSettings unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;

        return this;
    }

    /**
     * Возвращает флаги скрытия атрибутов предмета, если указаны.
     *
     * @return флаги скрытия атрибутов предмета
     * @see ItemFlag
     */
    @Nullable
    public List<ItemFlag> flags() {
        return flags;
    }

    /**
     * Устанавливает флаги скрытия атрибутов предмета.
     *
     * @param flags флаги скрытия атрибутов предмета
     */
    public ItemSettings flags(@NotNull ItemFlag... flags) {
        this.flags = List.of(flags);

        return this;
    }

    /**
     * Возвращает степень повреждения предмета, если указано.
     * <p>
     * Только для предметов, имеющих прочность.
     *
     * @return степень повреждения предмета
     */
    @Nullable
    public Integer damage() {
        return damage;
    }

    /**
     * Устанавливает степень повреждения предмета.
     * <p>
     * Только для предметов, имеющих прочность.
     *
     * @param damage степень повреждения предмета
     */
    public ItemSettings damage(int damage) {
        this.damage = damage;

        return this;
    }

    /**
     * Возвращает чары, наложенные на предмет, если указаны.
     *
     * @return чары, наложенные на предмет
     */
    @Nullable
    public Map<String, Integer> enchantments() {
        return enchants;
    }

    /**
     * Добавляет все указанные чары к предмету.
     *
     * @param enchantments чары
     */
    public ItemSettings enchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        final Map<String, Integer> enchants = new HashMap<>();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            enchants.put(entry.getKey().getKey().getKey(), entry.getValue());
        }

        this.enchants = enchants;

        return this;
    }

    /**
     * Создаёт и возвращает ItemStack со всеми параметрами из этого класса.
     *
     * @return новый экземпляр ItemStack
     */
    @NotNull
    public ItemStack itemStack() {
        final ItemStack item = texture != null ?
            HeadUtils.createHead(texture) :
            ItemStack.of(material);

        if (amount != null) {
            item.setAmount(Math.max(1, Math.min(64, amount)));
        }

        final ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.displayName(MiniMessage.miniMessage().deserialize(name));
        }

        if (lore != null) {
            meta.lore(lore.stream()
                .map(MiniMessage.miniMessage()::deserialize)
                .toList());
        }

        if (unbreakable != null) {
            meta.setUnbreakable(unbreakable);
        }

        if (flags != null) {
            meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        }

        if (damage != null && meta instanceof Damageable damageable) {
            damageable.setDamage(damage);
        }

        if (enchants != null) {
            for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
                final NamespacedKey keyInRegistry = NamespacedKey.fromString(entry.getKey());
                if (keyInRegistry == null) {
                    Crystal.instance().logger().warn("Invalid enchantment key: {}", entry.getKey());
                    continue;
                }

                final Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(keyInRegistry);
                if (enchantment == null) {
                    Crystal.instance().logger().warn("Enchantment with key {} not found", keyInRegistry);
                    continue;
                }

                meta.addEnchant(enchantment, entry.getValue(), true);
            }
        }

        item.setItemMeta(meta);

        return item;
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Preconditions;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Этот класс можно использовать для сохранения {@link ItemStack} в конфиг
 * через ConfigLib.
 * <p>
 * По сути эт костыль, т.к., в этой либе нет адекватной возможности
 * сделать Serializer таких сложных вещей,
 *
 * @author DenaryDev
 * @since 23:09 12.08.2025
 */
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

    @Ignore
    private ItemStack item;

    public static ItemSettings fromStack(ItemStack stack) {
        final ItemSettings settings = new ItemSettings();
        settings.material = stack.getType();
        settings.amount = stack.getAmount();

        final ItemMeta meta = stack.getItemMeta();
        if (meta instanceof SkullMeta skull) {
            final PlayerProfile profile = skull.getPlayerProfile();
            if (profile != null) {
                profile.getProperties().stream()
                    .filter(p -> p.getName().equals("textures"))
                    .findFirst()
                    .ifPresent(property -> settings.texture = property.getValue());
            }
        }

        final Component displayName = meta.displayName();
        if (displayName != null) {
            settings.name = MiniMessage.miniMessage().serialize(displayName);
        }

        final List<Component> lore = meta.lore();
        if (lore != null) {
            settings.lore = lore.stream()
                .map(MiniMessage.miniMessage()::serialize)
                .toList();
        }

        if (meta.isUnbreakable()) {
            settings.unbreakable = true;
        }

        final Set<ItemFlag> itemFlags = meta.getItemFlags();
        if (!itemFlags.isEmpty()) {
            settings.flags = new ArrayList<>(itemFlags);
        }

        if (meta instanceof Damageable damageable) {
            settings.damage = damageable.getDamage();
        }

        if (meta.hasEnchants()) {
            meta.getEnchants().forEach((enchantment, level) ->
                settings.enchants.put(enchantment.getKey().toString(), level));
        }

        return settings;
    }

    @PostProcess
    private void buildStack() {
        if (texture != null) {
            item = HeadUtils.createHead(texture);
        } else if (material != null) {
            item = new ItemStack(material);
        } else {
            throw new IllegalStateException("You must specify item material OR head texture");
        }

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
            for (final var entry : enchants.entrySet()) {
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
    }

    @NotNull
    public ItemStack getItem() {
        Preconditions.checkNotNull(item, "ItemSettings not initialized properly");

        return item;
    }
}

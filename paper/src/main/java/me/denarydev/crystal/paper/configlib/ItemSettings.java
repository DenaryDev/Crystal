/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib;

import com.google.common.base.Preconditions;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;
import de.exlll.configlib.PostProcess;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.denarydev.crystal.paper.PaperPlugin;
import me.denarydev.crystal.paper.utils.HeadUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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
    private Component name = null;
    private List<Component> lore = null;
    private Boolean unbreakable = null;
    private List<ItemFlag> flags = null;
    private Integer damage = null;
    private Map<String, Integer> enchants = null;

    @Ignore
    private ItemStack item;

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
            meta.displayName(name);
        }

        if (lore != null) {
            meta.lore(lore);
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
                    PaperPlugin.getInstance().getLogger().warning("Invalid enchantment key: " + entry.getKey());
                    continue;
                }

                final Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(keyInRegistry);
                if (enchantment == null) {
                    PaperPlugin.getInstance().getLogger().warning("Enchantment with key " + keyInRegistry + " not found");
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

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.utils;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.UUID;

/**
 * Утилиты для работы с предметами.
 */
public final class ItemUtils {

    /**
     * Создаёт голову с указанной текстурой.
     *
     * @param texture текстура головы.
     * @return {@link ItemStack} с текстурой.
     */
    @NotNull
    public static ItemStack createHead(final String texture) {
        return createHead(null, texture, 1);
    }

    /**
     * Создаёт голову с указанной текстурой и количеством.
     *
     * @param texture текстура головы.
     * @param amount  количество предметов.
     * @return {@link ItemStack} с текстурой.
     */
    @NotNull
    public static ItemStack createHead(final String texture, final int amount) {
        return createHead(null, texture, amount);
    }

    /**
     * Создаёт голову с указанной текстурой и подписью.
     *
     * @param signature подпись. (Не обязательно)
     * @param texture   текстура головы.
     * @return {@link ItemStack} с текстурой.
     */
    @NotNull
    public static ItemStack createHead(final String signature, final String texture) {
        return createHead(signature, texture, 1);
    }

    /**
     * Создаёт указанное кол-во голов с указанной текстурой и подписью.
     *
     * @param signature подпись. (Не обязательно)
     * @param texture   текстура головы.
     * @param amount    количество предметов.
     * @return {@link ItemStack} с текстурой.
     */
    @NotNull
    public static ItemStack createHead(final String signature, final String texture, final int amount) {
        final var head = new ItemStack(Material.PLAYER_HEAD, Math.max(Math.min(amount, 64), 1));
        if (texture == null)
            return head;

        final var skullMeta = (SkullMeta) head.getItemMeta();
        final var profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CustomHead");

        if (texture.endsWith("=")) {
            if (signature == null) {
                profile.setProperty(new ProfileProperty("textures", texture.replaceAll("=", "")));
            } else {
                profile.setProperty(new ProfileProperty("textures", texture, signature));
            }
        } else {
            final byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
            profile.setProperty(new ProfileProperty("textures", new String(encodedData)));
        }

        skullMeta.setPlayerProfile(profile);
        head.setItemMeta(skullMeta);
        return head;
    }
}

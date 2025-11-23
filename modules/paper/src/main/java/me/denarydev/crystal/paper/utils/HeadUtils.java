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
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.UUID;

/**
 * Утилиты для работы с предметами головы.
 */
public final class HeadUtils {

    /**
     * Создаёт предмет головы с указанной текстурой.
     *
     * @param texture текстура головы
     * @return {@link ItemStack} с текстурой
     */
    @NotNull
    public static ItemStack createHead(final String texture) {
        return createHead(texture, null, 1);
    }

    /**
     * Создаёт предмет головы с указанной текстурой и количеством.
     *
     * @param texture текстура головы
     * @param amount  количество предметов
     * @return {@link ItemStack} с текстурой
     */
    @NotNull
    public static ItemStack createHead(final String texture, final int amount) {
        return createHead(texture, null, amount);
    }

    /**
     * Создаёт предмет головы с указанной текстурой и подписью.
     *
     * @param texture   текстура головы
     * @param signature подпись (Не обязательно)
     * @return {@link ItemStack} с текстурой
     */
    @NotNull
    public static ItemStack createHead(final String texture, final String signature) {
        return createHead(texture, signature, 1);
    }

    /**
     * Создаёт предмет головы с указанной текстурой и подписью.
     *
     * @param texture   текстура головы
     * @param signature подпись (Не обязательно)
     * @param amount    количество предметов
     * @return {@link ItemStack} с текстурой
     */
    @NotNull
    public static ItemStack createHead(@NotNull final String texture, @Nullable final String signature, final int amount) {
        final var head = new ItemStack(Material.PLAYER_HEAD, Math.max(Math.min(amount, 64), 1));

        final var meta = (SkullMeta) head.getItemMeta();
        setTexture(meta, texture, signature);

        head.setItemMeta(meta);

        return head;
    }

    /**
     * Устанавливает текстуру головы.
     *
     * @param meta    метаданные предмета головы
     * @param texture текстура головы
     */
    public static void setTexture(@NotNull final SkullMeta meta, @NotNull final String texture) {
        setTexture(meta, texture, null);
    }

    /**
     * Устанавливает текстуру головы.
     *
     * @param meta      метаданные предмета головы
     * @param texture   текстура головы
     * @param signature подпись (Не обязательно)
     */
    public static void setTexture(@NotNull final SkullMeta meta, @NotNull final String texture, @Nullable String signature) {
        final var profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CrystalHead");

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

        meta.setPlayerProfile(profile);
    }
}

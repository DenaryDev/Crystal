/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Base64;
import java.util.UUID;

/**
 * Utilities for creating and configuring player head items.
 */
public final class HeadUtils {

    /**
     * Creates a player head item with the given texture.
     *
     * @param texture the Base64-encoded texture value or texture hash.
     * @return an {@link ItemStack} with the texture applied.
     */
    @NonNull
    public static ItemStack createHead(final String texture) {
        return createHead(texture, null, 1);
    }

    /**
     * Creates a player head item stack with the given texture and quantity.
     *
     * @param texture the Base64-encoded texture value or texture hash.
     * @param amount  the stack size (clamped to 1–64).
     * @return an {@link ItemStack} with the texture applied.
     */
    @NonNull
    public static ItemStack createHead(final String texture, final int amount) {
        return createHead(texture, null, amount);
    }

    /**
     * Creates a player head item with the given texture and signature.
     *
     * @param texture   the Base64-encoded texture value.
     * @param signature the Mojang signature for the texture, or {@code null} if unavailable.
     * @return an {@link ItemStack} with the texture applied.
     */
    @NonNull
    public static ItemStack createHead(final String texture, final String signature) {
        return createHead(texture, signature, 1);
    }

    /**
     * Creates a player head item stack with the given texture, signature, and quantity.
     *
     * @param texture   the Base64-encoded texture value.
     * @param signature the Mojang signature for the texture, or {@code null} if unavailable.
     * @param amount    the stack size (clamped to 1–64).
     * @return an {@link ItemStack} with the texture applied.
     */
    @NonNull
    public static ItemStack createHead(@NonNull final String texture, @Nullable final String signature, final int amount) {
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD, Math.max(Math.min(amount, 64), 1));

        final SkullMeta meta = (SkullMeta) head.getItemMeta();
        setTexture(meta, texture, signature);

        head.setItemMeta(meta);

        return head;
    }

    /**
     * Applies the given texture to a skull's metadata.
     *
     * @param meta    the skull metadata to modify.
     * @param texture the Base64-encoded texture value or texture hash.
     */
    public static void setTexture(@NonNull final SkullMeta meta, @NonNull final String texture) {
        setTexture(meta, texture, null);
    }

    /**
     * Applies the given texture and optional signature to a skull's metadata.
     *
     * @param meta      the skull metadata to modify.
     * @param texture   the Base64-encoded texture value or texture hash.
     * @param signature the Mojang signature for the texture, or {@code null} if unavailable.
     */
    public static void setTexture(@NonNull final SkullMeta meta, @NonNull final String texture, @Nullable String signature) {
        final PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CrystalHead");

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

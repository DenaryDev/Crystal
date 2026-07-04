/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.utils;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Utilities for working with permissions.
 */
public final class PermissionUtils {

    /**
     * Extracts the highest integer suffix from all permissions the player holds that start with the given prefix.
     * <p>
     * For example, if the prefix is {@code "example.limit."} and the player has
     * {@code "example.limit.5"} and {@code "example.limit.10"}, this returns {@code 10}.
     *
     * @param player     the player whose permissions are checked.
     * @param permission the permission prefix to match against.
     * @return the highest numeric value found among matching permissions.
     */
    public static int numberFromPermission(final Player player, final String permission) {
        final List<Integer> values = player.getEffectivePermissions().stream()
            .filter(info -> info.getPermission().startsWith(permission))
            .map(info -> info.getPermission().substring(permission.length()))
            .map(Integer::parseInt)
            .sorted()
            .toList();

        return values.getLast();
    }
}

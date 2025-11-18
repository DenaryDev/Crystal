/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.utils;

import org.bukkit.entity.Player;

/**
 * Утилиты для работы с пермишенами.
 */
public final class PermissionUtils {

    /**
     * Получает число из пермишена игрока.
     *
     * @param player     игрок
     * @param permission пермишен
     * @return число из пермишена
     */
    public static int numberFromPermission(final Player player, final String permission) {
        final var values = player.getEffectivePermissions().stream()
            .filter(info -> info.getPermission().startsWith(permission))
            .map(info -> info.getPermission().substring(permission.length()))
            .map(Integer::parseInt)
            .sorted()
            .toList();

        return values.get(values.size() - 1);
    }
}

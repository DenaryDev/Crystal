/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Утилиты для работы с местоположением игрока.
 */
public final class LocationUtils {

    /**
     * Проверяет, находится ли точка в указанной зоне.
     *
     * @param loc  точка для проверки
     * @param pos1 первая точка зоны
     * @param pos2 вторая точка зоны
     * @return true, если указанная точка в зоне, иначе false
     */
    public static boolean inArea(@NonNull final Location loc, @NonNull final Location pos1, @NonNull final Location pos2) {
        final double x1 = Math.min(pos1.getX(), pos2.getX());
        final double y1 = Math.min(pos1.getY(), pos2.getY());
        final double z1 = Math.min(pos1.getZ(), pos2.getZ());

        final double x2 = Math.max(pos1.getX(), pos2.getX());
        final double y2 = Math.max(pos1.getY(), pos2.getY());
        final double z2 = Math.max(pos1.getZ(), pos2.getZ());

        return loc.getX() >= x1 && loc.getX() <= x2 &&
            loc.getY() >= y1 && loc.getY() <= y2 &&
            loc.getZ() >= z1 && loc.getZ() <= z2;
    }

    /**
     * Возвращает центральную позицию, но не трогает высоту.
     *
     * @param location точка
     * @return центральная позиция от этой точки
     */
    @NonNull
    public static Location centerLocation(@NonNull final Location location) {
        final Location centerLoc = location.clone();
        centerLoc.setX(location.getBlockX() + 0.5);
        centerLoc.setZ(location.getBlockZ() + 0.5);

        return centerLoc;
    }

    /**
     * Находит ближайшей к точке блок, и возвращает его {@link Location}
     * <p>
     * По высоте ищет в диапазоне от -1 до +1 относительно высоты точки.
     *
     * @param loc    точка, вокруг которой ищем блок
     * @param type   тип блока, который ищем
     * @param radius радиус, не рекомендуются ставить большие значения
     * @return Позиция ближайшего к точке блока, или null, если таковой не найден
     */
    @Nullable
    public static Location findClosestBlock(@NonNull final Location loc, @NonNull final Material type, final int radius) {
        if (loc.getBlock().getType().equals(type)) return loc;

        Location closest = null;
        for (int y = -1; y <= 1; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    final Location current = loc.clone().add(x, y, z);
                    if (current.getBlock().getType().equals(type)) {
                        if (closest == null || loc.distance(current) < loc.distance(closest)) {
                            closest = current;
                        }
                    }
                }
            }
        }
        return closest;
    }

    /**
     * Преобразует {@link Location} в строку вида {@code мир;x;y;z} или {@code мир;x;y;z;yaw;pitch}.
     * <p>
     * Если мир не загружен, он не включается в строку.
     *
     * @param location локация для сериализации
     * @return строковое представление локации
     */
    public static String locationToString(Location location) {
        Preconditions.checkNotNull(location, "location cannot be null");

        final StringBuilder builder = new StringBuilder();

        if (location.isWorldLoaded()) builder.append(location.getWorld().getName()).append(";"); // World

        builder.append(location.getX()).append(";"); // X
        builder.append(location.getY()).append(";"); // Y
        builder.append(location.getZ()); // Z

        if (location.getYaw() > 0 || location.getPitch() > 0) {
            builder.append(";").append(location.getYaw()); // Yaw
            builder.append(";").append(location.getPitch()); // Pitch
        }

        return builder.toString();
    }
}

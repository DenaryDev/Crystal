/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Utilities for working with locations.
 */
public final class LocationUtils {

    /**
     * Returns whether the given location is within the axis-aligned bounding box
     * defined by two corner positions.
     *
     * @param loc  the location to test.
     * @param pos1 one corner of the area.
     * @param pos2 the opposite corner of the area.
     * @return {@code true} if the location is inside the area; {@code false} otherwise.
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
     * Returns a copy of the given location snapped to the horizontal center of its block (X + 0.5, Z + 0.5).
     * The Y coordinate is left unchanged.
     *
     * @param location the location to center.
     * @return the horizontally centered location.
     */
    @NonNull
    public static Location centerLocation(@NonNull final Location location) {
        final Location centerLoc = location.clone();
        centerLoc.setX(location.getBlockX() + 0.5);
        centerLoc.setZ(location.getBlockZ() + 0.5);

        return centerLoc;
    }

    /**
     * Finds the nearest block of the given type within the search radius and returns its location.
     * <p>
     * The vertical search range is limited to Y-1 through Y+1 relative to the origin.
     * Large radius values are not recommended for performance reasons.
     *
     * @param loc    the origin location to search around.
     * @param type   the block type to search for.
     * @param radius the horizontal search radius in blocks.
     * @return the location of the nearest matching block, or {@code null} if none was found.
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
     * Serializes a {@link Location} to a semicolon-delimited string of the form
     * {@code world;x;y;z} or {@code world;x;y;z;yaw;pitch}.
     * <p>
     * If the world is not loaded, the world name is omitted from the string.
     * Yaw and pitch are appended only when either value is greater than zero.
     *
     * @param location the location to serialize.
     * @return the string representation of the location.
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

    /**
     * Parses a location string produced by {@link #locationToString(Location)} back into a {@link Location}.
     * <p>
     * Accepts the {@code world;x;y;z} and {@code world;x;y;z;yaw;pitch} forms, as well as their
     * world-less variants ({@code x;y;z} and {@code x;y;z;yaw;pitch}). If a world name is present
     * but no such world is currently loaded, the resulting location's world is {@code null}.
     *
     * @param string the string to parse.
     * @return the parsed location.
     * @throws IllegalArgumentException if the string is not in a recognized format.
     * @throws NumberFormatException    if a coordinate, yaw, or pitch component is not a valid number.
     */
    @NonNull
    public static Location locationFromString(@NonNull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null");

        final String[] parts = string.split(";");

        final int offset = switch (parts.length) {
            case 3, 5 -> 0;
            case 4, 6 -> 1;
            default -> throw new IllegalArgumentException("Invalid location string: " + string);
        };

        final World world = offset == 1 ? Bukkit.getWorld(parts[0]) : null;

        final double x = Double.parseDouble(parts[offset]);
        final double y = Double.parseDouble(parts[offset + 1]);
        final double z = Double.parseDouble(parts[offset + 2]);

        if (parts.length == offset + 5) {
            final float yaw = Float.parseFloat(parts[offset + 3]);
            final float pitch = Float.parseFloat(parts[offset + 4]);

            return new Location(world, x, y, z, yaw, pitch);
        }

        return new Location(world, x, y, z);
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import io.leangen.geantyref.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class LocationSerializer implements TypeSerializer<Location> {
    public static final TypeToken<Location> TYPE = TypeToken.get(Location.class);

    @Override
    public Location deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var s = node.getString();
        if (s != null) {
            final String[] loc = s.split(";");
            if (loc.length == 3) { // X;Y;Z
                final var x = parseDouble(loc[0]);
                final var y = parseDouble(loc[1]);
                final var z = parseDouble(loc[2]);

                return new Location(null, x, y, z);
            } else if (loc.length == 4) { // WORLD;X;Y;Z
                return locationWithWorld(loc);
            } else if (loc.length == 6) { // WORLD;X;Y;Z;YAW;PITCH
                final var location = locationWithWorld(loc);
                location.setYaw((float) parseDouble(loc[4]));
                location.setPitch((float) parseDouble(loc[5]));

                return location;
            } else {
                throw new SerializationException("Invalid location format!");
            }
        } else {
            throw new SerializationException("Location string is null!");
        }
    }

    private Location locationWithWorld(String[] loc) throws SerializationException {
        final var world = Bukkit.getWorld(loc[0]);
        if (world == null) {
            throw new SerializationException("Unknown world!");
        }

        final var x = parseDouble(loc[1]);
        final var y = parseDouble(loc[2]);
        final var z = parseDouble(loc[3]);

        return new Location(world, x, y, z);
    }

    private double parseDouble(final String s) throws SerializationException {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            throw new SerializationException(Double.TYPE, ex);
        }
    }

    @Override
    public void serialize(Type type, @Nullable Location loc, ConfigurationNode node) throws SerializationException {
        if (loc != null) {
            final var builder = new StringBuilder();

            if (loc.isWorldLoaded()) builder.append(loc.getWorld().getName()).append(";"); // World

            builder.append(loc.getX()).append(";"); // X
            builder.append(loc.getY()).append(";"); // Y
            builder.append(loc.getZ()); // Z

            if (loc.getYaw() > 0 || loc.getPitch() > 0) {
                builder.append(";").append(loc.getYaw()); // Yaw
                builder.append(";").append(loc.getPitch()); // Pitch
            }

            node.set(builder.toString());
        }
    }
}

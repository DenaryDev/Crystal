/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import io.sapphiremc.lib.configurate.ConfigurationNode;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import io.sapphiremc.lib.configurate.serialize.TypeSerializer;
import me.denarydev.crystal.paper.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

public final class LocationSerializer implements TypeSerializer<Location> {

    @Override
    public Location deserialize(@NonNull Type type, ConfigurationNode node) throws SerializationException {
        final String s = node.getString();
        if (s == null) {
            throw new SerializationException("Location string is null!");
        }

        final String[] parts = s.split(";");
        if ((parts.length == 4 || parts.length == 6) && Bukkit.getWorld(parts[0]) == null) {
            throw new SerializationException("Unknown world!");
        }

        try {
            return LocationUtils.locationFromString(s);
        } catch (NumberFormatException ex) {
            throw new SerializationException(Double.TYPE, ex);
        } catch (IllegalArgumentException ex) {
            throw new SerializationException(type, ex);
        }
    }

    @Override
    public void serialize(@NonNull final Type type, @Nullable final Location loc, @NonNull final ConfigurationNode node) throws SerializationException {
        if (loc != null) {
            node.set(LocationUtils.locationToString(loc));
        }
    }
}

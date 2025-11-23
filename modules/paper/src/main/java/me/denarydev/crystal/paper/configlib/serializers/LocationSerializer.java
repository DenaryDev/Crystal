/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configlib.serializers;

import de.exlll.configlib.Serializer;
import me.denarydev.crystal.paper.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author DenaryDev
 * @since 22:24 12.08.2025
 */
public final class LocationSerializer implements Serializer<Location, String> {
    @Override
    public String serialize(Location element) {
        return LocationUtils.locationToString(element);
    }

    @Override
    public Location deserialize(String element) {
        final String[] loc = element.split(";");
        if (loc.length == 3) { // X;Y;Z
            final double x = parseDouble(loc[0]);
            final double y = parseDouble(loc[1]);
            final double z = parseDouble(loc[2]);

            return new Location(null, x, y, z);
        } else if (loc.length == 4) { // WORLD;X;Y;Z
            return locationWithWorld(loc);
        } else if (loc.length == 6) { // WORLD;X;Y;Z;YAW;PITCH
            final Location location = locationWithWorld(loc);
            location.setYaw((float) parseDouble(loc[4]));
            location.setPitch((float) parseDouble(loc[5]));

            return location;
        } else {
            throw new RuntimeException("Invalid location format!");
        }
    }

    private Location locationWithWorld(String[] loc) {
        final World world = Bukkit.getWorld(loc[0]);
        if (world == null) {
            throw new RuntimeException("Unknown world \"" + loc[0] + "\" !");
        }

        final double x = parseDouble(loc[1]);
        final double y = parseDouble(loc[2]);
        final double z = parseDouble(loc[3]);

        return new Location(world, x, y, z);
    }

    private double parseDouble(final String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Failed to parse double from \"" + s + "\"", ex);
        }
    }
}

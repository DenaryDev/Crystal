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
import me.denarydev.crystal.paper.item.CustomModelData;
import org.bukkit.Color;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Serializer for {@link CustomModelData}.
 * <p>
 * The node is a map with the optional lists {@code floats}, {@code flags}, {@code strings}
 * and {@code colors} (hex strings such as {@code "#FF8800"}, or RGB integers). A plain
 * number is accepted as the legacy integer custom model data and becomes a single float.
 */
public final class CustomModelDataSerializer implements TypeSerializer<CustomModelData> {

    @Override
    public CustomModelData deserialize(@NonNull Type type, ConfigurationNode node) throws SerializationException {
        return read(node);
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable CustomModelData data, @NonNull ConfigurationNode node) throws SerializationException {
        if (data == null || data.isEmpty()) {
            node.set(null);
            return;
        }

        write(node, data);
    }

    /**
     * Reads custom model data from the node, without going through a serializer collection.
     *
     * @param node the node to read.
     * @return the data, {@link CustomModelData#EMPTY} if the node is empty.
     */
    public static CustomModelData read(@NonNull ConfigurationNode node) throws SerializationException {
        if (node.virtual() || node.empty()) return CustomModelData.EMPTY;

        if (!node.isMap()) {
            // Legacy integer custom model data maps to a single float since 1.21.4
            return CustomModelData.ofFloats(List.of((float) node.getInt()));
        }

        final List<Float> floats = node.node("floats").getList(Float.class, Collections.emptyList());
        final List<Boolean> flags = node.node("flags").getList(Boolean.class, Collections.emptyList());
        final List<String> strings = node.node("strings").getList(String.class, Collections.emptyList());
        final List<Color> colors = new ArrayList<>();

        for (ConfigurationNode child : node.node("colors").childrenList()) {
            colors.add(readColor(child));
        }

        return new CustomModelData(floats, flags, strings, colors);
    }

    /**
     * Writes custom model data into the node, omitting empty lists.
     *
     * @param node the node to write to.
     * @param data the data to write.
     */
    public static void write(@NonNull ConfigurationNode node, @NonNull CustomModelData data) throws SerializationException {
        if (!data.floats().isEmpty()) node.node("floats").setList(Float.class, data.floats());
        if (!data.flags().isEmpty()) node.node("flags").setList(Boolean.class, data.flags());
        if (!data.strings().isEmpty()) node.node("strings").setList(String.class, data.strings());
        if (!data.colors().isEmpty()) {
            node.node("colors").setList(String.class, data.colors().stream().map(CustomModelDataSerializer::writeColor).toList());
        }
    }

    private static Color readColor(ConfigurationNode node) throws SerializationException {
        final String raw = node.getString();
        if (raw == null) throw new SerializationException(node, Color.class, "Missing color");

        final String hex = raw.startsWith("#") ? raw.substring(1) : raw;

        try {
            return Color.fromRGB(Integer.parseInt(hex, raw.startsWith("#") ? 16 : 10));
        } catch (IllegalArgumentException ex) {
            throw new SerializationException(node, Color.class, "Invalid color: " + raw, ex);
        }
    }

    private static String writeColor(Color color) {
        return String.format("#%06X", color.asRGB());
    }
}

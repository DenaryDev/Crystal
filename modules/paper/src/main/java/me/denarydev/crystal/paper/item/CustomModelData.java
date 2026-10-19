/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.item;

import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * An immutable copy of the {@code minecraft:custom_model_data} component: the float, flag,
 * string and color lists a resource pack can select models by.
 *
 * @param floats  the float list.
 * @param flags   the boolean flag list.
 * @param strings the string list.
 * @param colors  the color list.
 */
public record CustomModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<Color> colors) {

    /**
     * A component with every list empty.
     */
    public static final CustomModelData EMPTY = new CustomModelData(List.of(), List.of(), List.of(), List.of());

    public CustomModelData {
        floats = List.copyOf(floats);
        flags = List.copyOf(flags);
        strings = List.copyOf(strings);
        colors = List.copyOf(colors);
    }

    /**
     * Creates custom model data with only a float list, the modern equivalent of the
     * legacy integer custom model data.
     *
     * @param floats the float list.
     */
    public static CustomModelData ofFloats(@NonNull List<Float> floats) {
        return new CustomModelData(floats, List.of(), List.of(), List.of());
    }

    /**
     * Copies the custom model data component of the given item meta.
     *
     * @param meta the item meta.
     * @return the copied data, or {@code null} if the meta has no custom model data component.
     */
    @Nullable
    public static CustomModelData of(@NonNull ItemMeta meta) {
        if (!meta.hasCustomModelDataComponent()) return null;

        return of(meta.getCustomModelDataComponent());
    }

    /**
     * Copies the given component.
     *
     * @param component the component to copy.
     */
    public static CustomModelData of(@NonNull CustomModelDataComponent component) {
        return new CustomModelData(component.getFloats(), component.getFlags(), component.getStrings(), component.getColors());
    }

    /**
     * Returns whether every list is empty.
     */
    public boolean isEmpty() {
        return floats.isEmpty() && flags.isEmpty() && strings.isEmpty() && colors.isEmpty();
    }

    /**
     * Writes this data into the component of the given item meta.
     *
     * @param meta the item meta to modify.
     */
    public void applyTo(@NonNull ItemMeta meta) {
        final CustomModelDataComponent component = meta.getCustomModelDataComponent();
        component.setFloats(floats);
        component.setFlags(flags);
        component.setStrings(strings);
        component.setColors(colors);
        meta.setCustomModelDataComponent(component);
    }
}

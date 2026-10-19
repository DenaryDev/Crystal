/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import io.sapphiremc.lib.configurate.BasicConfigurationNode;
import io.sapphiremc.lib.configurate.ConfigurationNode;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import me.denarydev.crystal.paper.item.CustomModelData;
import me.denarydev.crystal.paper.item.ItemTemplate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomModelDataSerializerTest {

    private static final CustomModelData SAMPLE = new CustomModelData(
        List.of(1.5f, 2f), List.of(true, false), List.of("variant"), List.of(Color.fromRGB(0xFF8800)));

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void read_mapWithAllLists() throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();
        node.node("floats").setList(Float.class, List.of(1.5f, 2f));
        node.node("flags").setList(Boolean.class, List.of(true, false));
        node.node("strings").setList(String.class, List.of("variant"));
        node.node("colors").setList(String.class, List.of("#FF8800"));

        assertEquals(SAMPLE, CustomModelDataSerializer.read(node));
    }

    @Test
    public void read_legacyIntegerBecomesSingleFloat() throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();
        node.set(7);

        assertEquals(CustomModelData.ofFloats(List.of(7f)), CustomModelDataSerializer.read(node));
    }

    @Test
    public void read_colorsAcceptIntegers() throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();
        node.node("colors").setList(Integer.class, List.of(0xFF8800));

        assertEquals(List.of(Color.fromRGB(0xFF8800)), CustomModelDataSerializer.read(node).colors());
    }

    @Test
    public void read_invalidColorThrows() throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();
        node.node("colors").setList(String.class, List.of("#nope"));

        assertThrows(SerializationException.class, () -> CustomModelDataSerializer.read(node));
    }

    @Test
    public void read_emptyNodeIsEmpty() throws SerializationException {
        assertTrue(CustomModelDataSerializer.read(BasicConfigurationNode.root()).isEmpty());
    }

    @Test
    public void write_omitsEmptyListsAndRoundTrips() throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();

        CustomModelDataSerializer.write(node, CustomModelData.ofFloats(List.of(3f)));

        assertFalse(node.hasChild("flags"));
        assertFalse(node.hasChild("strings"));
        assertFalse(node.hasChild("colors"));
        assertEquals(CustomModelData.ofFloats(List.of(3f)), CustomModelDataSerializer.read(node));

        final ConfigurationNode full = BasicConfigurationNode.root();
        new CustomModelDataSerializer().serialize(CustomModelData.class, SAMPLE, full);
        assertEquals(List.of("#FF8800"), full.node("colors").getList(String.class));
        assertEquals(SAMPLE, new CustomModelDataSerializer().deserialize(CustomModelData.class, full));
    }

    @Test
    public void itemStack_roundTripsCustomModelData() throws SerializationException {
        final ItemStackSerializer serializer = new ItemStackSerializer();
        final ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("PAPER");
        node.node("item", "custom-model-data", "floats").setList(Float.class, List.of(1.5f));
        node.node("item", "custom-model-data", "strings").setList(String.class, List.of("variant"));

        final ItemStack item = serializer.deserialize(ItemStack.class, node.node("item"));
        final ItemMeta meta = item.getItemMeta();

        assertTrue(meta.hasCustomModelDataComponent());
        assertEquals(List.of(1.5f), meta.getCustomModelDataComponent().getFloats());
        assertEquals(List.of("variant"), meta.getCustomModelDataComponent().getStrings());

        final ConfigurationNode out = BasicConfigurationNode.root();
        serializer.serialize(ItemStack.class, item, out.node("item"));

        assertEquals(List.of(1.5f), out.node("item", "custom-model-data", "floats").getList(Float.class));
        assertEquals(List.of("variant"), out.node("item", "custom-model-data", "strings").getList(String.class));
        assertFalse(out.node("item", "custom-model-data").hasChild("flags"));
    }

    @Test
    public void itemStack_withoutCustomModelDataWritesNothing() throws SerializationException {
        final ConfigurationNode out = BasicConfigurationNode.root();

        new ItemStackSerializer().serialize(ItemStack.class, ItemStack.of(Material.STONE), out.node("item"));

        assertFalse(out.node("item").hasChild("custom-model-data"));
    }

    @Test
    public void itemTemplate_buildsAndRoundTripsCustomModelData() throws SerializationException {
        final ItemTemplate template = ItemTemplate.builder(Material.PAPER).customModelData(SAMPLE).build();

        final ItemMeta meta = template.build().getItemMeta();
        assertEquals(SAMPLE, CustomModelData.of(meta));
        assertEquals(SAMPLE, ItemTemplate.of(template.build()).customModelData());

        final ItemTemplateSerializer serializer = new ItemTemplateSerializer();
        final ConfigurationNode node = BasicConfigurationNode.root();
        serializer.serialize(ItemTemplate.class, template, node.node("item"));
        final ItemTemplate copy = serializer.deserialize(ItemTemplate.class, node.node("item"));

        assertNotNull(copy.customModelData());
        assertEquals(SAMPLE, copy.customModelData());
    }
}

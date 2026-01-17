/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.serializers;

import io.sapphiremc.lib.configurate.BasicConfigurationNode;
import io.sapphiremc.lib.configurate.ConfigurateException;
import io.sapphiremc.lib.configurate.ConfigurationNode;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SerializersTest {

    private ServerMock server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testLocationSerializer() throws SerializationException {
        final LocationSerializer serializer = new LocationSerializer();
        final World world = server.addSimpleWorld("world");

        final Location x_y_z_loc = new Location(null, 15.27, 65.12, 76.21);
        final Location world_x_y_z_loc = new Location(world, 15.27, 65.12, 76.21);
        final Location world_x_y_z_yaw_pitch_loc = new Location(world, 15.27, 65.12, 76.21, 45, 180);

        final ConfigurationNode node = BasicConfigurationNode.root();
        node.node("x_y_z_val").set("15.27;65.12;76.21");
        node.node("world_x_y_z_val").set("world;15.27;65.12;76.21");
        node.node("world_x_y_z_yaw_pitch_val").set("world;15.27;65.12;76.21;45;180.0");
        node.node("invalid_val").set("15.27;65.12;76.21;234.15");

        assertEquals(x_y_z_loc, serializer.deserialize(Location.class, node.node("x_y_z_val")));
        assertEquals(world_x_y_z_loc, serializer.deserialize(Location.class, node.node("world_x_y_z_val")));
        assertEquals(world_x_y_z_yaw_pitch_loc, serializer.deserialize(Location.class, node.node("world_x_y_z_yaw_pitch_val")));
        assertThrows(SerializationException.class, () -> serializer.deserialize(Location.class, node.node("invalid_val")));
    }

    @Test
    public void testNamespacedKeySerializer() throws ConfigurateException {
        final NamespacedKeySerializer serializer = new NamespacedKeySerializer();

        final NamespacedKey mcNamespace = NamespacedKey.fromString("test");
        final NamespacedKey customNamespace = NamespacedKey.fromString("customnamespace:test");

        final ConfigurationNode node = BasicConfigurationNode.root();
        node.node("val_with_mc_namespace").set("test");
        node.node("val_with_custom_namespace").set("customnamespace:test");
        node.node("invalid_val").set("InVaLiD:iNvAlId");

        assertEquals(mcNamespace, serializer.deserialize(NamespacedKey.class, node.node("val_with_mc_namespace")));
        assertEquals(customNamespace, serializer.deserialize(NamespacedKey.class, node.node("val_with_custom_namespace")));
        assertThrows(SerializationException.class, () -> serializer.deserialize(NamespacedKey.class, node.node("invalid_val")));
    }

//    @Test
//    public void testItemStackSerializer() throws SerializationException {
//        final var serializer = new ItemStackSerializer();
//
//        final var validItem = ItemUtils.itemBuilder()
//            .type(Material.NETHERITE_SWORD)
//            .amount(5)
//            .displayNameRich("<yellow>SuperMegaSword")
//            .loreRich(List.of("<gray>SuperLoreLine1", "SuperLoreLine2"))
//            .enchantment(Enchantment.DAMAGE_ALL, 10)
//            .itemFlags(ItemFlag.HIDE_ENCHANTS)
//            .unbreakable(true)
//            .customModelData(256)
//            .damage(512)
//            .build();
//
//        final var node = BasicConfigurationNode.root();
//        assertDoesNotThrow(() -> serializer.serialize(ItemStackSerializer.TYPE.getType(), validItem, node.node("validItemSerialized")));
//
//        node.node("validItem", "material").set(Material.NETHERITE_SWORD);
//        node.node("validItem", "amount").set(5);
//        node.node("validItem", "name").set("<yellow>SuperMegaSword");
//        node.node("validItem", "lore").set(List.of("<gray>SuperLoreLine1", "<gray>SuperLoreLine2"));
//        node.node("validItem", "unbreakable").set(true);
//        node.node("validItem", "flags").setList(ItemFlag.class, List.of(ItemFlag.HIDE_ENCHANTS));
//        node.node("validItem", "custom-model-data").set(256);
//        node.node("validItem", "damage").set(512);
//        node.node("validItem", "enchants", "sharpness").set(10);
//        node.node("invalidItem", "amount").set(512);
//        node.node("invalidItem", "unbreakable").set(true);
//
//        //assertEquals(validItem, serializer.deserialize(ItemStackSerializer.TYPE.getType(), node.node("validItem")));
//        assertThrows(SerializationException.class, () -> serializer.deserialize(ItemStack.class, node.node("invalidItem")));
//    }
}

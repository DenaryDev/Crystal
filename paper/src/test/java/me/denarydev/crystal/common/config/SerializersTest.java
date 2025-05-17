/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.common.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.denarydev.crystal.paper.configurate.serializers.LocationSerializer;
import me.denarydev.crystal.paper.configurate.serializers.NamespacedKeySerializer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author DenaryDev
 * @since 1:10 02.11.2023
 */
public class SerializersTest {

    private Server server;

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
        final var serializer = new LocationSerializer();
        final var world = server.createWorld(WorldCreator.name("world"));

        final var x_y_z_loc = new Location(null, 15.27, 65.12, 76.21);
        final var world_x_y_z_loc = new Location(world, 15.27, 65.12, 76.21);
        final var world_x_y_z_yaw_pitch_loc = new Location(world, 15.27, 65.12, 76.21, 45, 180);

        final var node = BasicConfigurationNode.root();
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
        final var serializer = new NamespacedKeySerializer();

        final var mcNamespace = NamespacedKey.fromString("test");
        final var customNamespace = NamespacedKey.fromString("customnamespace:test");

        final var node = BasicConfigurationNode.root();
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

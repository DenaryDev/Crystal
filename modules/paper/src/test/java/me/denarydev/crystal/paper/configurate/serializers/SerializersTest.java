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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // --- LocationSerializer ---

    @Test
    public void locationXYZ() throws SerializationException {
        LocationSerializer serializer = new LocationSerializer();

        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("loc").set("15.27;65.12;76.21");

        Location expected = new Location(null, 15.27, 65.12, 76.21);
        assertEquals(expected, serializer.deserialize(Location.class, node.node("loc")));
    }

    @Test
    public void locationWorldXYZ() throws SerializationException {
        LocationSerializer serializer = new LocationSerializer();
        World world = server.addSimpleWorld("world");

        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("loc").set("world;15.27;65.12;76.21");

        Location expected = new Location(world, 15.27, 65.12, 76.21);
        assertEquals(expected, serializer.deserialize(Location.class, node.node("loc")));
    }

    @Test
    public void locationWorldXYZYawPitch() throws SerializationException {
        LocationSerializer serializer = new LocationSerializer();
        World world = server.addSimpleWorld("world");

        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("loc").set("world;15.27;65.12;76.21;45;180.0");

        Location expected = new Location(world, 15.27, 65.12, 76.21, 45, 180);
        assertEquals(expected, serializer.deserialize(Location.class, node.node("loc")));
    }

    @Test
    public void locationInvalidFormatThrows() throws SerializationException {
        LocationSerializer serializer = new LocationSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("loc").set("15.27;65.12;76.21;234.15");

        assertThrows(SerializationException.class, () -> serializer.deserialize(Location.class, node.node("loc")));
    }

    @Test
    public void locationUnknownWorldThrows() throws SerializationException {
        LocationSerializer serializer = new LocationSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("loc").set("nonexistent_world;0;64;0");

        assertThrows(SerializationException.class, () -> serializer.deserialize(Location.class, node.node("loc")));
    }

    @Test
    public void locationSerialize() throws SerializationException {
        LocationSerializer serializer = new LocationSerializer();
        World world = server.addSimpleWorld("world");

        Location loc = new Location(world, 10, 64, -5);
        ConfigurationNode node = BasicConfigurationNode.root();
        serializer.serialize(Location.class, loc, node.node("loc"));

        assertFalse(node.node("loc").isNull());
    }

    // --- NamespacedKeySerializer ---

    @Test
    public void namespacedKeyMinecraftNamespace() throws SerializationException {
        NamespacedKeySerializer serializer = new NamespacedKeySerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("key").set("test");

        assertEquals(NamespacedKey.fromString("test"), serializer.deserialize(NamespacedKey.class, node.node("key")));
    }

    @Test
    public void namespacedKeyCustomNamespace() throws SerializationException {
        NamespacedKeySerializer serializer = new NamespacedKeySerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("key").set("customnamespace:test");

        assertEquals(NamespacedKey.fromString("customnamespace:test"), serializer.deserialize(NamespacedKey.class, node.node("key")));
    }

    @Test
    public void namespacedKeyInvalidThrows() throws SerializationException {
        NamespacedKeySerializer serializer = new NamespacedKeySerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("key").set("InVaLiD:iNvAlId");

        assertThrows(SerializationException.class, () -> serializer.deserialize(NamespacedKey.class, node.node("key")));
    }

    @Test
    public void namespacedKeySerialize() {
        NamespacedKeySerializer serializer = new NamespacedKeySerializer();
        NamespacedKey key = NamespacedKey.fromString("minecraft:speed");
        ConfigurationNode node = BasicConfigurationNode.root();
        serializer.serialize(NamespacedKey.class, key, node.node("key"));

        assertEquals("minecraft:speed", node.node("key").getString());
    }

    // --- ComponentSerializer ---

    @Test
    public void componentDeserializeSimpleString() throws SerializationException {
        ComponentSerializer serializer = new ComponentSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("text").set("<gold>Hello");

        Component result = serializer.deserialize(Component.class, node.node("text"));

        assertNotNull(result);
        assertEquals("Hello", PlainTextComponentSerializer.plainText().serialize(result));
    }

    @Test
    public void componentDeserializeRoundTrip() throws SerializationException {
        ComponentSerializer serializer = new ComponentSerializer();
        String input = "<red><bold>Important message";

        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("text").set(input);

        Component deserialized = serializer.deserialize(Component.class, node.node("text"));
        Component expected = MiniMessage.miniMessage().deserialize(input);

        assertEquals(expected, deserialized);
    }

    @Test
    public void componentSerialize() throws SerializationException {
        ComponentSerializer serializer = new ComponentSerializer();
        Component component = MiniMessage.miniMessage().deserialize("<green>Success");

        ConfigurationNode node = BasicConfigurationNode.root();
        serializer.serialize(Component.class, component, node.node("text"));

        assertEquals(component, node.node("text").get(Component.class));
    }

    @Test
    public void componentDeserializeNonStringThrows() throws SerializationException {
        ComponentSerializer serializer = new ComponentSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("text").set(42);

        assertThrows(SerializationException.class, () -> serializer.deserialize(Component.class, node.node("text")));
    }

    // --- MaterialSerializer ---

    @Test
    public void materialDeserializeValid() throws SerializationException {
        MaterialSerializer serializer = new MaterialSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("mat").set("DIAMOND_BLOCK");

        assertEquals(Material.DIAMOND_BLOCK, serializer.deserialize(Material.class, node.node("mat")));
    }

    @Test
    public void materialDeserializeLowercase() throws SerializationException {
        MaterialSerializer serializer = new MaterialSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("mat").set("iron_pickaxe");

        assertEquals(Material.IRON_PICKAXE, serializer.deserialize(Material.class, node.node("mat")));
    }

    @Test
    public void materialDeserializeInvalidThrows() throws SerializationException {
        MaterialSerializer serializer = new MaterialSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("mat").set("NOT_A_MATERIAL");

        assertThrows(SerializationException.class, () -> serializer.deserialize(Material.class, node.node("mat")));
    }

    @Test
    public void materialSerialize() {
        MaterialSerializer serializer = new MaterialSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        serializer.serialize(Material.class, Material.DIAMOND_SWORD, node.node("mat"));

        assertEquals("DIAMOND_SWORD", node.node("mat").getString());
    }

    // --- ItemStackSerializer ---

    @Test
    public void itemStackDeserializeBasic() throws SerializationException {
        ItemStackSerializer serializer = new ItemStackSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("IRON_SWORD");
        node.node("item", "amount").set(3);

        ItemStack item = serializer.deserialize(ItemStack.class, node.node("item"));

        assertNotNull(item);
        assertEquals(Material.IRON_SWORD, item.getType());
        assertEquals(3, item.getAmount());
    }

    @Test
    public void itemStackDeserializeDefaultAmount() throws SerializationException {
        ItemStackSerializer serializer = new ItemStackSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("STONE");

        ItemStack item = serializer.deserialize(ItemStack.class, node.node("item"));

        assertNotNull(item);
        assertEquals(Material.STONE, item.getType());
        assertEquals(1, item.getAmount());
    }

    @Test
    public void itemStackDeserializeUnbreakable() throws SerializationException {
        ItemStackSerializer serializer = new ItemStackSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("DIAMOND_SWORD");
        node.node("item", "unbreakable").set(true);

        ItemStack item = serializer.deserialize(ItemStack.class, node.node("item"));

        assertTrue(item.getItemMeta().isUnbreakable());
    }

    @Test
    public void itemStackDeserializeLore() throws SerializationException {
        ItemStackSerializer serializer = new ItemStackSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("PAPER");
        node.node("item", "lore").setList(String.class, List.of("<gray>Line one", "<gray>Line two"));

        ItemStack item = serializer.deserialize(ItemStack.class, node.node("item"));

        List<Component> lore = item.getItemMeta().lore();
        assertNotNull(lore);
        assertEquals(2, lore.size());
    }

    @Test
    public void itemStackDeserializeMissingMaterialThrows() throws SerializationException {
        ItemStackSerializer serializer = new ItemStackSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "amount").set(1);

        assertThrows(SerializationException.class, () -> serializer.deserialize(ItemStack.class, node.node("item")));
    }

    @Test
    public void itemStackDeserializeInvalidAmountThrows() throws SerializationException {
        ItemStackSerializer serializer = new ItemStackSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("STONE");
        node.node("item", "amount").set(0);

        assertThrows(SerializationException.class, () -> serializer.deserialize(ItemStack.class, node.node("item")));
    }

    @Test
    public void itemStackSerialize() {
        ItemStackSerializer serializer = new ItemStackSerializer();
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE, 2);
        ConfigurationNode node = BasicConfigurationNode.root();

        assertDoesNotThrow(() -> serializer.serialize(ItemStack.class, item, node.node("item")));
        assertEquals("GOLDEN_APPLE", node.node("item", "material").getString());
        assertEquals(2, node.node("item", "amount").getInt());
    }

    @Test
    public void itemStackDeserializeHeadTexture() throws SerializationException {
        ItemStackSerializer serializer = new ItemStackSerializer();
        String texture =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU5ODRmY2JjZTA3MDAwYmI4Y2ZjZGRkNzQ2NTMyN2I0YmMxMzQzY2E3NTVmZDg3ZjkxY2M4NWU0M2VmMjZjZiJ9fX0=";

        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("PLAYER_HEAD");
        node.node("item", "texture").set(texture);

        ItemStack item = serializer.deserialize(ItemStack.class, node.node("item"));

        assertNotNull(item);
        assertEquals(Material.PLAYER_HEAD, item.getType());
        assertInstanceOf(SkullMeta.class, item.getItemMeta());
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        assertNotNull(meta.getPlayerProfile());
    }

    @Test
    public void itemStackSerializeNull() throws SerializationException {
        ItemStackSerializer serializer = new ItemStackSerializer();
        ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("STONE");

        serializer.serialize(ItemStack.class, null, node.node("item"));

        assertTrue(node.node("item").isNull());
    }
}

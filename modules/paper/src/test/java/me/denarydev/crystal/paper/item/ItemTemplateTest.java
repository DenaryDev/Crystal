/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.item;

import io.sapphiremc.lib.configurate.BasicConfigurationNode;
import io.sapphiremc.lib.configurate.ConfigurationNode;
import io.sapphiremc.lib.configurate.serialize.SerializationException;
import me.denarydev.crystal.paper.configurate.serializers.ItemTemplateSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemTemplateTest {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void build_parsesNameAndLoreWithMiniMessage() {
        final ItemStack item = ItemTemplate.builder(Material.PAPER)
            .name("<red>Hello")
            .lore("<gray>Line one", "<gray>Line two")
            .build()
            .build();

        final ItemMeta meta = item.getItemMeta();
        final Component name = meta.displayName();
        final List<Component> lore = meta.lore();

        assertNotNull(name);
        assertNotNull(lore);
        assertEquals("Hello", PLAIN.serialize(name));
        assertEquals(NamedTextColor.RED, name.color());
        assertEquals(List.of("Line one", "Line two"), lore.stream().map(PLAIN::serialize).toList());
    }

    @Test
    public void build_appliesPreprocessorBeforeParsing() {
        final ItemStack item = ItemTemplate.builder(Material.PAPER)
            .name("%player_name%")
            .lore("Balance: %balance%")
            .build()
            .build(text -> text.replace("%player_name%", "<gold>Steve").replace("%balance%", "42"));

        final ItemMeta meta = item.getItemMeta();
        final Component name = meta.displayName();
        final List<Component> lore = meta.lore();

        assertNotNull(name);
        assertNotNull(lore);
        assertEquals("Steve", PLAIN.serialize(name));
        assertEquals(NamedTextColor.GOLD, name.color());
        assertEquals("Balance: 42", PLAIN.serialize(lore.getFirst()));
    }

    @Test
    public void build_usesTagResolvers() {
        final ItemStack item = ItemTemplate.builder(Material.PAPER)
            .name("Hi, <player>")
            .build()
            .build(Placeholder.unparsed("player", "Alex"));

        final Component name = item.getItemMeta().displayName();

        assertNotNull(name);
        assertEquals("Hi, Alex", PLAIN.serialize(name));
    }

    @Test
    public void build_appliesAmountFlagsUnbreakableAndGlow() {
        final ItemStack item = ItemTemplate.builder(Material.DIAMOND_SWORD)
            .amount(3)
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .unbreakable(true)
            .glow(true)
            .build()
            .build();

        final ItemMeta meta = item.getItemMeta();

        assertEquals(3, item.getAmount());
        assertTrue(meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
        assertTrue(meta.isUnbreakable());
        assertEquals(Boolean.TRUE, meta.getEnchantmentGlintOverride());
    }

    @Test
    public void build_producesIndependentItems() {
        final ItemTemplate template = ItemTemplate.builder(Material.STONE).name("x").build();

        final ItemStack first = template.build();
        final ItemStack second = template.build();

        first.setAmount(10);
        assertEquals(1, second.getAmount());
    }

    @Test
    public void hasPlaceholders_detectsPercentInNameOrLore() {
        assertTrue(ItemTemplate.builder(Material.STONE).name("%x%").build().hasPlaceholders());
        assertTrue(ItemTemplate.builder(Material.STONE).lore("a", "%x%").build().hasPlaceholders());
        assertFalse(ItemTemplate.builder(Material.STONE).name("<player>").lore("plain").build().hasPlaceholders());
        assertFalse(ItemTemplate.builder(Material.STONE).build().hasPlaceholders());
    }

    @Test
    public void of_roundTripsThroughItemStack() {
        final ItemTemplate original = ItemTemplate.builder(Material.GOLDEN_APPLE)
            .amount(2)
            .name("<gold>Apple")
            .lore("<gray>Tasty")
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .unbreakable(true)
            .glow(false)
            .build();

        final ItemTemplate copy = ItemTemplate.of(original.build());

        assertEquals(Material.GOLDEN_APPLE, copy.material());
        assertEquals(2, copy.amount());
        assertEquals("<gold>Apple", copy.name());
        assertEquals(List.of("<gray>Tasty"), copy.lore());
        assertTrue(copy.flags().contains(ItemFlag.HIDE_ATTRIBUTES));
        assertTrue(copy.unbreakable());
        assertEquals(Boolean.FALSE, copy.glow());
    }

    @Test
    public void toBuilder_copiesEverything() {
        final ItemTemplate original = ItemTemplate.builder(Material.STONE)
            .amount(5).name("n").lore("l").glow(true).damage(2).build();

        final ItemTemplate copy = original.toBuilder().name("changed").build();

        assertEquals("changed", copy.name());
        assertEquals(5, copy.amount());
        assertEquals(List.of("l"), copy.lore());
        assertEquals(Boolean.TRUE, copy.glow());
        assertEquals(2, copy.damage());
        assertEquals("n", original.name());
    }

    @Test
    public void itemModel_isAppliedCopiedAndSerialized() throws SerializationException {
        final NamespacedKey model = NamespacedKey.minecraft("writable_book");
        final ItemTemplate template = ItemTemplate.builder(Material.PAPER).itemModel(model).build();

        final ItemStack item = template.build();
        assertEquals(Material.PAPER, item.getType());
        assertEquals(model, item.getItemMeta().getItemModel());
        assertEquals(model, ItemTemplate.of(item).itemModel());
        assertEquals(model, template.toBuilder().build().itemModel());

        final ItemTemplateSerializer serializer = new ItemTemplateSerializer();
        final ConfigurationNode node = BasicConfigurationNode.root();
        serializer.serialize(ItemTemplate.class, template, node.node("item"));
        assertEquals("minecraft:writable_book", node.node("item", "item-model").getString());
        assertEquals(model, serializer.deserialize(ItemTemplate.class, node.node("item")).itemModel());

        node.node("item", "item-model").set("not a key!");
        assertThrows(SerializationException.class, () -> serializer.deserialize(ItemTemplate.class, node.node("item")));
    }

    @Test
    public void builder_rejectsBadAmount() {
        assertThrows(IllegalArgumentException.class, () -> ItemTemplate.builder(Material.STONE).amount(0));
        assertThrows(IllegalArgumentException.class, () -> ItemTemplate.builder(Material.STONE).amount(65));
    }

    // --- ItemTemplateSerializer ---

    @Test
    public void serializer_deserializesKeepingStrings() throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "material").set("PAPER");
        node.node("item", "amount").set(4);
        node.node("item", "name").set("<red>%player_name%");
        node.node("item", "lore").setList(String.class, List.of("<gray>a", "<gray>b"));
        node.node("item", "flags").setList(String.class, List.of("HIDE_ATTRIBUTES", "NOT_A_FLAG"));
        node.node("item", "unbreakable").set(true);
        node.node("item", "damage").set(7);
        node.node("item", "glow").set(true);

        final ItemTemplate template = new ItemTemplateSerializer().deserialize(ItemTemplate.class, node.node("item"));

        assertEquals(Material.PAPER, template.material());
        assertEquals(4, template.amount());
        assertEquals("<red>%player_name%", template.name());
        assertEquals(List.of("<gray>a", "<gray>b"), template.lore());
        assertEquals(1, template.flags().size());
        assertTrue(template.unbreakable());
        assertEquals(7, template.damage());
        assertEquals(Boolean.TRUE, template.glow());
        assertTrue(template.hasPlaceholders());
    }

    @Test
    public void serializer_missingMaterialThrows() throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();
        node.node("item", "amount").set(1);

        assertThrows(SerializationException.class,
            () -> new ItemTemplateSerializer().deserialize(ItemTemplate.class, node.node("item")));
    }

    @Test
    public void serializer_roundTrips() throws SerializationException {
        final ItemTemplate original = ItemTemplate.builder(Material.PAPER)
            .amount(2)
            .name("<red>Name")
            .lore("<gray>a")
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .unbreakable(true)
            .damage(3)
            .glow(false)
            .build();
        final ItemTemplateSerializer serializer = new ItemTemplateSerializer();
        final ConfigurationNode node = BasicConfigurationNode.root();

        serializer.serialize(ItemTemplate.class, original, node.node("item"));
        final ItemTemplate copy = serializer.deserialize(ItemTemplate.class, node.node("item"));

        assertEquals(original.material(), copy.material());
        assertEquals(original.amount(), copy.amount());
        assertEquals(original.name(), copy.name());
        assertEquals(original.lore(), copy.lore());
        assertEquals(original.flags(), copy.flags());
        assertEquals(original.unbreakable(), copy.unbreakable());
        assertEquals(original.damage(), copy.damage());
        assertEquals(original.glow(), copy.glow());
        assertNull(copy.texture());
    }

    @Test
    public void serializer_omitsDefaults() throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();

        new ItemTemplateSerializer().serialize(ItemTemplate.class, ItemTemplate.builder(Material.STONE).build(), node.node("item"));

        assertEquals(List.of("material"), node.node("item").childrenMap().keySet().stream().map(Object::toString).toList());
    }
}

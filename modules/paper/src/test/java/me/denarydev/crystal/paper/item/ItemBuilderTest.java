/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemBuilderTest {

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    private static List<String> plainLore(ItemStack item) {
        final List<Component> lore = item.lore();
        assertNotNull(lore);

        return lore.stream().map(PlainTextComponentSerializer.plainText()::serialize).toList();
    }

    @Test
    public void glow_setsAndResetsGlintOverride() {
        final ItemStack glowing = ItemBuilder.fromMaterial(Material.STONE).glow(true).build();
        final ItemStack reset = ItemBuilder.fromItem(glowing).resetGlow().build();

        assertEquals(Boolean.TRUE, glowing.getItemMeta().getEnchantmentGlintOverride());
        assertFalse(reset.getItemMeta().hasEnchantmentGlintOverride());
    }

    @Test
    public void appendLore_toEmptyItemBecomesLore() {
        final ItemStack item = ItemBuilder.fromMaterial(Material.STONE)
            .appendLore(Component.text("a"))
            .build();

        assertEquals(List.of("a"), plainLore(item));
    }

    @Test
    public void appendLore_keepsExistingLines() {
        final ItemStack item = ItemBuilder.fromMaterial(Material.STONE)
            .lorePlain(List.of("a"))
            .appendLore(Component.text("b"), Component.text("c"))
            .appendLorePlain(List.of("d"))
            .appendLoreRich(List.of("<name>"), Placeholder.unparsed("name", "e"))
            .build();

        assertEquals(List.of("a", "b", "c", "d", "e"), plainLore(item));
    }

    @Test
    public void appendLore_disablesDefaultItalic() {
        final ItemStack item = ItemBuilder.fromMaterial(Material.STONE)
            .appendLore(Component.text("plain"), Component.text("italic").decorate(TextDecoration.ITALIC))
            .build();

        final List<Component> lore = item.lore();
        assertNotNull(lore);
        assertEquals(TextDecoration.State.FALSE, lore.get(0).decoration(TextDecoration.ITALIC));
        assertEquals(TextDecoration.State.TRUE, lore.get(1).decoration(TextDecoration.ITALIC));
    }

    @Test
    public void appendLore_doesNotModifyExistingLines() {
        final ItemStack source = ItemStack.of(Material.STONE);
        source.lore(List.of(Component.text("vanilla")));

        final ItemStack item = ItemBuilder.fromItem(source)
            .appendLore(Component.text("added"))
            .build();

        final List<Component> lore = item.lore();
        assertNotNull(lore);
        assertEquals(TextDecoration.State.NOT_SET, lore.get(0).decoration(TextDecoration.ITALIC));
        assertEquals(List.of("vanilla", "added"), plainLore(item));
        assertEquals(1, source.lore().size());
    }

    @Test
    public void type_onEmptyBuilderProducesVisibleItem() {
        final ItemStack item = ItemBuilder.empty()
            .type(Material.PAPER)
            .build();

        assertEquals(Material.PAPER, item.getType());
        assertEquals(1, item.getAmount());
        assertFalse(item.isEmpty());
    }

    @Test
    public void type_keepsAmountOfExistingItem() {
        final ItemStack item = ItemBuilder.fromItem(ItemStack.of(Material.STONE, 5))
            .type(Material.PAPER)
            .build();

        assertEquals(Material.PAPER, item.getType());
        assertEquals(5, item.getAmount());
    }

    @Test
    public void lore_nullRemoves() {
        final ItemStack item = ItemBuilder.fromMaterial(Material.STONE)
            .lorePlain(List.of("a"))
            .lore(null)
            .build();

        assertNull(item.lore());
    }
}

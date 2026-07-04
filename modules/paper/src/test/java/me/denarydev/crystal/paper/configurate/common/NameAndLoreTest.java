/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.configurate.common;

import net.kyori.adventure.text.Component;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NameAndLoreTest {

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void ofNameOnly_rawLoreIsNull() {
        NameAndLore nal = NameAndLore.of("<gold>Sword");
        assertEquals("<gold>Sword", nal.rawName());
        assertNull(nal.rawLore());
    }

    @Test
    public void ofNameAndVarargs_rawLoreContainsLines() {
        NameAndLore nal = NameAndLore.of("<gold>Sword", "<gray>Line 1", "<gray>Line 2");
        assertEquals(List.of("<gray>Line 1", "<gray>Line 2"), nal.rawLore());
    }

    @Test
    public void ofNameAndList_rawLoreContainsLines() {
        List<String> lines = List.of("<gray>A", "<gray>B");
        NameAndLore nal = NameAndLore.of("<gold>Sword", lines);
        assertEquals(lines, nal.rawLore());
    }

    @Test
    public void name_parsesMiniMessage() {
        NameAndLore nal = NameAndLore.of("<gold>Dragon Sword");
        String plain = PlainTextComponentSerializer.plainText().serialize(nal.name());
        assertEquals("Dragon Sword", plain);
    }

    @Test
    public void name_withPlaceholder() {
        NameAndLore nal = NameAndLore.of("<gold><player>'s Sword");
        Component component = nal.name(Placeholder.unparsed("player", "Alice"));
        String plain = PlainTextComponentSerializer.plainText().serialize(component);
        assertEquals("Alice's Sword", plain);
    }

    @Test
    public void lore_parsesMiniMessage() {
        NameAndLore nal = NameAndLore.of("<gold>Sword", "<gray>Legendary", "<gray>Blade");
        List<Component> lore = nal.lore();
        assertNotNull(lore);
        assertEquals(2, lore.size());
        assertEquals("Legendary", PlainTextComponentSerializer.plainText().serialize(lore.get(0)));
        assertEquals("Blade", PlainTextComponentSerializer.plainText().serialize(lore.get(1)));
    }

    @Test
    public void lore_withPlaceholder() {
        NameAndLore nal = NameAndLore.of("<gold>Sword", "<gray>Damage: <dmg>");
        List<Component> lore = nal.lore(Placeholder.unparsed("dmg", "100"));
        assertNotNull(lore);
        assertEquals("Damage: 100", PlainTextComponentSerializer.plainText().serialize(lore.get(0)));
    }

    @Test
    public void lore_returnsNullWhenNoLoreSet() {
        NameAndLore nal = NameAndLore.of("<gold>Sword");
        assertNull(nal.lore());
    }

    @Test
    public void apply_setsDisplayNameOnItem() {
        NameAndLore nal = NameAndLore.of("<gold>Magic Wand");
        ItemStack item = new ItemStack(Material.STICK);
        nal.apply(item);

        Component name = item.getItemMeta().displayName();
        assertNotNull(name);
        assertEquals("Magic Wand", PlainTextComponentSerializer.plainText().serialize(name));
    }

    @Test
    public void apply_setsLoreOnItem() {
        NameAndLore nal = NameAndLore.of("<gold>Staff", "<gray>Ancient", "<gray>Powerful");
        ItemStack item = new ItemStack(Material.STICK);
        nal.apply(item);

        List<Component> lore = item.getItemMeta().lore();
        assertNotNull(lore);
        assertEquals(2, lore.size());
        assertEquals("Ancient", PlainTextComponentSerializer.plainText().serialize(lore.get(0)));
        assertEquals("Powerful", PlainTextComponentSerializer.plainText().serialize(lore.get(1)));
    }

    @Test
    public void apply_withPlaceholder() {
        NameAndLore nal = NameAndLore.of("<gold><player>'s Wand");
        ItemStack item = new ItemStack(Material.STICK);
        nal.apply(item, Placeholder.unparsed("player", "Bob"));

        Component name = item.getItemMeta().displayName();
        assertNotNull(name);
        assertEquals("Bob's Wand", PlainTextComponentSerializer.plainText().serialize(name));
    }

    @Test
    public void apply_noLoreDoesNotSetLore() {
        NameAndLore nal = NameAndLore.of("<gold>Plain Item");
        ItemStack item = new ItemStack(Material.STONE);
        nal.apply(item);

        assertNull(item.getItemMeta().lore());
    }
}

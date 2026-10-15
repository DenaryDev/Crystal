/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColorConverterTest {

    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.builder()
        .character(LegacyComponentSerializer.SECTION_CHAR)
        .hexColors()
        .build();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.builder()
        .character(LegacyComponentSerializer.AMPERSAND_CHAR)
        .hexColors()
        .build();

    // --- toMiniMessage(String) ---

    @Test
    void namedColors() {
        assertEquals("<red>Hello", ColorConverter.toMiniMessage("&cHello"));
        assertEquals("<yellow>Hello", ColorConverter.toMiniMessage("§eHello"));
        assertEquals("<black>a<dark_blue>b<white>c", ColorConverter.toMiniMessage("&0a&1b&fc"));
    }

    @Test
    void upperCaseCodes() {
        assertEquals("<red>Hello", ColorConverter.toMiniMessage("&CHello"));
        assertEquals("<bold>Hello", ColorConverter.toMiniMessage("&LHello"));
    }

    @Test
    void decorations() {
        assertEquals("<obfuscated>a<bold>b<strikethrough>c<underlined>d<italic>e<reset>f",
            ColorConverter.toMiniMessage("&ka&lb&mc&nd&oe&rf"));
    }

    @Test
    void colorAfterDecorationResets() {
        assertEquals("<bold>Hello <reset><green>world", ColorConverter.toMiniMessage("&lHello &aworld"));
        assertEquals("<green><bold>Hello", ColorConverter.toMiniMessage("&a&lHello"));
        assertEquals("<bold>Hello<reset><red>a<green>b", ColorConverter.toMiniMessage("&lHello&ca&ab"));
    }

    @Test
    void hexColors() {
        assertEquals("<#12ab34>Hello", ColorConverter.toMiniMessage("&#12ab34Hello"));
        assertEquals("<#12ab34>Hello", ColorConverter.toMiniMessage("§#12AB34Hello"));
        assertEquals("<#12ab34>Hello", ColorConverter.toMiniMessage("&x&1&2&a&b&3&4Hello"));
        assertEquals("<#12ab34>Hello", ColorConverter.toMiniMessage("§X§1§2§A§B§3§4Hello"));
    }

    @Test
    void incompleteHexIsLeftAlone() {
        assertEquals("&#12abHello", ColorConverter.toMiniMessage("&#12abHello"));
        assertEquals("&x<dark_blue><dark_green>Hello", ColorConverter.toMiniMessage("&x&1&2Hello"));
    }

    @Test
    void sectionCodesWinOverAmpersand() {
        assertEquals("<red>Hello &aworld", ColorConverter.toMiniMessage("§cHello &aworld"));
    }

    @Test
    void plainTextIsReturnedAsIs() {
        final String text = "Tom & Jerry";
        assertSame(text, ColorConverter.toMiniMessage(text));
        assertEquals("a && b", ColorConverter.toMiniMessage("a && b"));
        assertEquals("", ColorConverter.toMiniMessage(""));
    }

    @Test
    void sectionWithoutCodeIsNotConverted() {
        assertEquals("Price: 5§", ColorConverter.toMiniMessage("Price: 5§"));
        assertEquals("<blue>Price: 5§", ColorConverter.toMiniMessage("<blue>Price: 5§"));
    }

    @Test
    void ampersandInMiniMessageIsKept() {
        assertEquals("<red>Tom & Jerry", ColorConverter.toMiniMessage("<red>Tom & Jerry"));
        assertEquals("<green>R&B party", ColorConverter.toMiniMessage("<green>R&B party"));
        assertEquals("<gradient:red:blue>R&B</gradient>", ColorConverter.toMiniMessage("<gradient:red:blue>R&B</gradient>"));
    }

    @Test
    void sectionInMiniMessageIsConverted() {
        assertEquals("<red>Hi <yellow>there", ColorConverter.toMiniMessage("<red>Hi §ethere"));
    }

    @Test
    void angleBracketsWithoutTagsAreNotMiniMessage() {
        assertEquals("<green>I <3 you", ColorConverter.toMiniMessage("&aI <3 you"));
        assertEquals("<yellow>Hello <world>", ColorConverter.toMiniMessage("§eHello <world>"));
    }

    @Test
    void legacyInBrackets() {
        assertEquals("[<gold>VIP<reset>]", ColorConverter.toMiniMessage("[&6VIP&r]"));
    }

    @Test
    void lines() {
        assertEquals(List.of("<red>a", "b", "<bold>c"), ColorConverter.toMiniMessage(List.of("&ca", "b", "§lc")));
    }

    // --- toMiniMessage(String, char) ---

    @Test
    void explicitPrefixIgnoresHeuristics() {
        assertEquals("<green>R<aqua> party", ColorConverter.toMiniMessage("<green>R&B party", '&'));
        assertEquals("&cHello <yellow>there", ColorConverter.toMiniMessage("&cHello §ethere", '§'));
        assertEquals("<red>Hello", ColorConverter.toMiniMessage("%cHello", '%'));
    }

    // --- hasLegacyCodes ---

    @Test
    void hasLegacyCodes() {
        assertTrue(ColorConverter.hasLegacyCodes("&cHello", '&'));
        assertTrue(ColorConverter.hasLegacyCodes("&#12ab34Hello", '&'));
        assertFalse(ColorConverter.hasLegacyCodes("Tom & Jerry", '&'));
        assertFalse(ColorConverter.hasLegacyCodes("&cHello", '§'));
    }

    // --- hasMiniMessageTags ---

    @ParameterizedTest
    @ValueSource(strings = {
        "<red>Hello", "Hello</red>", "<RED>Hello", "<#ff0000>Hello", "<!italic>Hello", "<b>Hi</b>",
        "<gradient:red:blue>Hi", "<click:run_command:'/help'>Hi", "<hover:show_text:\"a > b\">Hi",
        "<lang:item.diamond>", "<br>", "<color:red>Hi", "<shadow:#000000>Hi", "<#ff00>Hi", "<c>Hi", "<bold:x>Hi"
    })
    void detectsMiniMessageTags(final String text) {
        assertTrue(ColorConverter.hasMiniMessageTags(text));
        assertEquals(text, ColorConverter.toMiniMessage(text));
        // Must agree with MiniMessage itself
        assertNotEquals(text, MiniMessage.miniMessage().stripTags(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Hello", "I <3 you", "Hello <world>", "a < b > c", "<prefix>Hi", "<red", "red>", "<>", "< red>",
        "<#zz>Hi", "<#>Hi", "<#ff0000x>Hi", "<#ff0000ff>Hi", "</foo>Hi", "<!foo>Hi"
    })
    void ignoresNonTags(final String text) {
        assertFalse(ColorConverter.hasMiniMessageTags(text));
        // Must agree with MiniMessage itself
        assertEquals(text, MiniMessage.miniMessage().stripTags(text));
    }

    // --- Round trip through Adventure: converted text must render exactly like the legacy serializer renders the original ---

    @ParameterizedTest
    @ValueSource(strings = {
        "&cHello & bye",
        "&a&lBold green &cthen red",
        "&lBold &athen green without bold",
        "&k&l&m&n&oAll &rreset",
        "&#12ab34Hex &x&f&f&0&0&0&0bungee",
        "[&6VIP&r] &7Player &fjoined",
        "&aI <3 you",
        "&CUpper &LCase"
    })
    void ampersandRoundTrip(final String legacy) {
        final Component expected = LEGACY_AMPERSAND.deserialize(legacy);
        final Component actual = MiniMessage.miniMessage().deserialize(ColorConverter.toMiniMessage(legacy));
        assertEquals(LEGACY_SECTION.serialize(expected), LEGACY_SECTION.serialize(actual));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "§eHello <world>",
        "§l§eBold yellow §r§cred",
        "§x§1§2§a§b§3§4Hex §ngradient §cno"
    })
    void sectionRoundTrip(final String legacy) {
        final Component expected = LEGACY_SECTION.deserialize(legacy);
        final Component actual = MiniMessage.miniMessage().deserialize(ColorConverter.toMiniMessage(legacy));
        assertEquals(LEGACY_SECTION.serialize(expected), LEGACY_SECTION.serialize(actual));
    }
}

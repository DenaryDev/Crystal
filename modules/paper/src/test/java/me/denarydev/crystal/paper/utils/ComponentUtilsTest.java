/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComponentUtilsTest {

    /**
     * Records everything sent to it instead of delivering it anywhere.
     */
    private static final class RecordingAudience implements Audience {
        final List<Component> messages = new ArrayList<>();
        final List<Component> actionBars = new ArrayList<>();

        @Override
        public void sendMessage(final Component message) {
            messages.add(message);
        }

        @Override
        public void sendActionBar(final Component message) {
            actionBars.add(message);
        }
    }

    private static String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @Test
    public void send_parsesMiniMessageAndResolvesTags() {
        final RecordingAudience audience = new RecordingAudience();

        ComponentUtils.send(audience, "<red>Hello, <name>!", Placeholder.unparsed("name", "Steve"));

        assertEquals(1, audience.messages.size());
        assertEquals("Hello, Steve!", plain(audience.messages.get(0)));
        assertEquals(NamedTextColor.RED, audience.messages.get(0).color());
    }

    @Test
    public void send_listSendsOneMessagePerLine() {
        final RecordingAudience audience = new RecordingAudience();

        ComponentUtils.send(audience, List.of("<green>a", "b", "<name>"), Placeholder.unparsed("name", "c"));

        assertEquals(List.of("a", "b", "c"), audience.messages.stream().map(ComponentUtilsTest::plain).toList());
        assertTrue(audience.actionBars.isEmpty());
    }

    @Test
    public void sendActionBar_goesToActionBar() {
        final RecordingAudience audience = new RecordingAudience();

        ComponentUtils.sendActionBar(audience, "<gold>Ready");

        assertTrue(audience.messages.isEmpty());
        assertEquals(1, audience.actionBars.size());
        assertEquals("Ready", plain(audience.actionBars.get(0)));
        assertEquals(NamedTextColor.GOLD, audience.actionBars.get(0).color());
    }

    @Test
    public void send_skipsEmptyMessage() {
        final RecordingAudience audience = new RecordingAudience();

        ComponentUtils.send(audience, "");
        ComponentUtils.sendActionBar(audience, "");

        assertTrue(audience.messages.isEmpty());
        assertTrue(audience.actionBars.isEmpty());
    }

    @Test
    public void send_listKeepsEmptyLinesAsSpacing() {
        final RecordingAudience audience = new RecordingAudience();

        ComponentUtils.send(audience, List.of("a", "", "b"));

        assertEquals(List.of("a", "", "b"), audience.messages.stream().map(ComponentUtilsTest::plain).toList());
    }
}

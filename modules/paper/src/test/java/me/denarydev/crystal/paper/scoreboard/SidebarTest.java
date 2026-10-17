/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MockBukkit does not implement {@code Score.customName} or {@code Objective.numberFormat},
 * so the sidebar is tested against a recording {@link SidebarBoard} instead of the real one.
 */
public class SidebarTest {

    private ServerMock server;
    private PlayerMock player;
    private FakeBoard board;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        player = server.addPlayer();
        board = new FakeBoard();
    }

    @AfterEach
    public void tearDown() {
        Sidebar.removeInternal(player);
        MockBukkit.unmock();
    }

    private Sidebar create() {
        return Sidebar.create(player, board);
    }

    @Test
    public void create_showsBoardAndRegistersSidebar() {
        final Sidebar sidebar = create();

        assertSame(player, board.shownTo);
        assertSame(board.scoreboard, player.getScoreboard());
        assertSame(sidebar, Sidebar.get(player).orElseThrow());
        assertEquals(Component.empty(), sidebar.title());
        assertEquals(NumberFormat.blank(), sidebar.numberFormat());
        assertTrue(sidebar.lines().isEmpty());
    }

    @Test
    public void create_twiceReplacesPreviousSidebar() {
        final Sidebar first = create();
        final FakeBoard secondBoard = new FakeBoard();
        final Sidebar second = Sidebar.create(player, secondBoard);

        assertTrue(first.removed());
        assertSame(player, board.hiddenFrom);
        assertFalse(second.removed());
        assertSame(second, Sidebar.get(player).orElseThrow());
    }

    @Test
    public void title_isAppliedToBoard() {
        final Sidebar sidebar = create().title(Component.text("Title"));

        assertEquals(Component.text("Title"), sidebar.title());
        assertEquals(Component.text("Title"), board.title);
    }

    @Test
    public void numberFormat_isAppliedToBoard() {
        final Sidebar sidebar = create().numberFormat(NumberFormat.noStyle());

        assertEquals(NumberFormat.noStyle(), sidebar.numberFormat());
        assertEquals(NumberFormat.noStyle(), board.numberFormat);
    }

    @Test
    public void lines_areWrittenTopToBottom() {
        final Sidebar sidebar = create()
            .lines(Component.text("one"), Component.text("two"), Component.text("three"));

        assertEquals(List.of(Component.text("one"), Component.text("two"), Component.text("three")), sidebar.lines());
        assertEquals(Component.text("one"), board.lines.get(0));
        assertEquals(Component.text("two"), board.lines.get(1));
        assertEquals(Component.text("three"), board.lines.get(2));
        assertEquals(3, board.writes);
    }

    @Test
    public void lines_onlyChangedLinesAreRewritten() {
        final Sidebar sidebar = create()
            .lines(Component.text("a"), Component.text("b"), Component.text("c"))
            .lines(Component.text("a"), Component.text("B"), Component.text("c"));

        assertEquals(4, board.writes);
        assertEquals(Component.text("B"), board.lines.get(1));
        assertEquals(List.of(Component.text("a"), Component.text("B"), Component.text("c")), sidebar.lines());
    }

    @Test
    public void lines_shrinkingClearsRemovedLines() {
        final Sidebar sidebar = create()
            .lines(Component.text("a"), Component.text("b"), Component.text("c"))
            .lines(Component.text("a"));

        assertEquals(1, sidebar.lines().size());
        assertEquals(Map.of(0, Component.text("a")), board.lines);
        assertEquals(List.of(1, 2), board.cleared);
    }

    @Test
    public void lines_moreThanMaxIsRejected() {
        final Sidebar sidebar = create();
        final Component[] tooMany = new Component[Sidebar.MAX_LINES + 1];
        Arrays.fill(tooMany, Component.empty());

        assertThrows(IllegalArgumentException.class, () -> sidebar.lines(tooMany));
    }

    @Test
    public void line_pastEndPadsWithEmptyLines() {
        final Sidebar sidebar = create().line(2, Component.text("third"));

        assertEquals(List.of(Component.empty(), Component.empty(), Component.text("third")), sidebar.lines());
        assertEquals(Component.empty(), board.lines.get(0));
        assertEquals(Component.empty(), board.lines.get(1));
        assertEquals(Component.text("third"), board.lines.get(2));
    }

    @Test
    public void line_unchangedTextIsNotRewritten() {
        create()
            .lines(Component.text("same"))
            .line(0, Component.text("same"));

        assertEquals(1, board.writes);
    }

    @Test
    public void line_outOfRangeIsRejected() {
        final Sidebar sidebar = create();

        assertThrows(IndexOutOfBoundsException.class, () -> sidebar.line(Sidebar.MAX_LINES, Component.empty()));
        assertThrows(IndexOutOfBoundsException.class, () -> sidebar.line(-1, Component.empty()));
    }

    @Test
    public void removeLine_shiftsFollowingLinesUp() {
        final Sidebar sidebar = create()
            .lines(Component.text("a"), Component.text("b"), Component.text("c"))
            .removeLine(1);

        assertEquals(List.of(Component.text("a"), Component.text("c")), sidebar.lines());
        assertEquals(Component.text("c"), board.lines.get(1));
        assertNull(board.lines.get(2));
    }

    @Test
    public void removeLine_outOfRangeIsIgnored() {
        final Sidebar sidebar = create().lines(Component.text("a")).removeLine(5);

        assertEquals(List.of(Component.text("a")), sidebar.lines());
    }

    @Test
    public void clearLines_leavesNoLines() {
        final Sidebar sidebar = create().lines(Component.text("a"), Component.text("b")).clearLines();

        assertTrue(sidebar.lines().isEmpty());
        assertTrue(board.lines.isEmpty());
    }

    @Test
    public void remove_hidesBoardAndForbidsFurtherChanges() {
        final Sidebar sidebar = create();

        sidebar.remove();

        assertTrue(sidebar.removed());
        assertSame(player, board.hiddenFrom);
        assertSame(server.getScoreboardManager().getMainScoreboard(), player.getScoreboard());
        assertTrue(Sidebar.get(player).isEmpty());
        assertThrows(IllegalStateException.class, () -> sidebar.title(Component.text("x")));
        assertThrows(IllegalStateException.class, () -> sidebar.lines(Component.text("x")));
    }

    @Test
    public void remove_twiceIsNoop() {
        final Sidebar sidebar = create();

        sidebar.remove();
        sidebar.remove();

        assertEquals(1, board.hides);
    }

    @Test
    public void removeInternal_dropsSidebarOnQuit() {
        final Sidebar sidebar = create();

        Sidebar.removeInternal(player);

        assertTrue(sidebar.removed());
        assertSame(player, board.hiddenFrom);
        assertTrue(Sidebar.get(player).isEmpty());
    }

    /**
     * Records every call; the scoreboard is a real {@code ScoreboardMock} so
     * {@code Player.setScoreboard} keeps working.
     */
    private static final class FakeBoard implements SidebarBoard {

        private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        private final Map<Integer, Component> lines = new TreeMap<>();
        private final List<Integer> cleared = new ArrayList<>();

        private Component title = Component.empty();
        private NumberFormat numberFormat = NumberFormat.blank();
        private Player shownTo;
        private Player hiddenFrom;
        private int writes;
        private int hides;

        @Override
        public Scoreboard scoreboard() {
            return scoreboard;
        }

        @Override
        public void title(Component title) {
            this.title = title;
        }

        @Override
        public void numberFormat(NumberFormat format) {
            this.numberFormat = format;
        }

        @Override
        public void line(int index, Component text) {
            lines.put(index, text);
            writes++;
        }

        @Override
        public void clearLine(int index) {
            lines.remove(index);
            cleared.add(index);
        }

        @Override
        public void show(Player player) {
            shownTo = player;
            player.setScoreboard(scoreboard);
        }

        @Override
        public void hide(Player player) {
            hiddenFrom = player;
            hides++;
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
}

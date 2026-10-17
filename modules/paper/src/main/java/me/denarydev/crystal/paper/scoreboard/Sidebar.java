/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.scoreboard;

import com.google.common.base.Preconditions;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import me.denarydev.crystal.paper.scheduler.PaperScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A per-player sidebar built on a Bukkit {@link Scoreboard}.
 * <p>
 * Creating a sidebar gives the player their own scoreboard with a single
 * sidebar objective. Each line is a score whose text is set
 * through {@code Score.customName(Component)}, so lines are not limited in length and no
 * teams or placeholder entries are needed. Score numbers are hidden with
 * {@link NumberFormat#blank()} by default.
 * <p>
 * All setters may be called from any thread and after creation: a call made off the
 * global tick thread is re-scheduled through {@link PaperScheduler#sync()}, so the
 * scoreboard itself is only ever touched on the global tick thread. Getters return the
 * last state that was applied.
 * <p>
 * The sidebar is removed automatically when the player quits.
 * <p>
 * <b>Paper only.</b> Folia does not support Bukkit scoreboards.
 */
@NullMarked
public final class Sidebar {

    /**
     * Maximum number of lines the client renders in the sidebar.
     */
    public static final int MAX_LINES = 15;

    private static final Map<UUID, Sidebar> SIDEBARS = new ConcurrentHashMap<>();

    private final Player player;
    private final SidebarBoard board;
    private final List<Component> lines = new ArrayList<>();

    private volatile Component title = Component.empty();
    private volatile NumberFormat numberFormat = NumberFormat.blank();
    private volatile boolean removed = false;

    /**
     * Creates a sidebar for the given player and shows it immediately.
     * <p>
     * If the player already has a sidebar, it is removed first.
     *
     * @param player the player to show the sidebar to.
     * @return the new sidebar.
     */
    public static Sidebar create(Player player) {
        return create(player, new BukkitSidebarBoard(Component.empty(), NumberFormat.blank()));
    }

    @ApiStatus.Internal
    static Sidebar create(Player player, SidebarBoard board) {
        final Sidebar previous = SIDEBARS.get(player.getUniqueId());
        if (previous != null) {
            previous.remove();
        }

        final Sidebar sidebar = new Sidebar(player, board);
        SIDEBARS.put(player.getUniqueId(), sidebar);

        return sidebar;
    }

    /**
     * Returns the sidebar currently shown to the given player, if any.
     *
     * @param player the player.
     * @return the player's sidebar, or empty if the player has none.
     */
    public static Optional<Sidebar> get(Player player) {
        return Optional.ofNullable(SIDEBARS.get(player.getUniqueId()));
    }

    private Sidebar(Player player, SidebarBoard board) {
        this.player = player;
        this.board = board;

        board.show(player);
    }

    //region Title and number format

    /**
     * Returns the sidebar title.
     */
    public Component title() {
        return title;
    }

    /**
     * Sets the sidebar title.
     *
     * @param title the title.
     */
    public Sidebar title(Component title) {
        modify(() -> {
            this.title = title;
            board.title(title);
        });

        return this;
    }

    /**
     * Returns the number format applied to all lines.
     */
    public NumberFormat numberFormat() {
        return numberFormat;
    }

    /**
     * Sets the number format applied to all lines.
     * <p>
     * Defaults to {@link NumberFormat#blank()}, which hides score numbers entirely.
     *
     * @param format the number format.
     */
    public Sidebar numberFormat(NumberFormat format) {
        modify(() -> {
            this.numberFormat = format;
            board.numberFormat(format);
        });

        return this;
    }

    //endregion

    //region Lines

    /**
     * Returns an unmodifiable snapshot of the lines, top to bottom.
     */
    public List<Component> lines() {
        synchronized (lines) {
            return Collections.unmodifiableList(new ArrayList<>(lines));
        }
    }

    /**
     * Replaces all lines, top to bottom.
     * <p>
     * Only lines whose text actually changed are re-sent to the client.
     *
     * @param lines the new lines, at most {@link #MAX_LINES}.
     */
    public Sidebar lines(List<Component> lines) {
        Preconditions.checkArgument(lines.size() <= MAX_LINES, "more than %s lines", MAX_LINES);

        final List<Component> copy = List.copyOf(lines);

        modify(() -> {
            synchronized (this.lines) {
                final int oldSize = this.lines.size();

                for (int index = 0; index < copy.size(); index++) {
                    final Component line = copy.get(index);
                    if (index >= oldSize || !line.equals(this.lines.get(index))) {
                        board.line(index, line);
                    }
                }

                for (int index = copy.size(); index < oldSize; index++) {
                    board.clearLine(index);
                }

                this.lines.clear();
                this.lines.addAll(copy);
            }
        });

        return this;
    }

    /**
     * Replaces all lines, top to bottom.
     *
     * @param lines the new lines, at most {@link #MAX_LINES}.
     * @see #lines(List)
     */
    public Sidebar lines(Component... lines) {
        return lines(Arrays.asList(lines));
    }

    /**
     * Sets a single line.
     * <p>
     * If the index is past the current end, the gap is filled with empty lines.
     *
     * @param index the zero-based line index from the top, below {@link #MAX_LINES}.
     * @param line  the line text.
     */
    public Sidebar line(int index, Component line) {
        Preconditions.checkElementIndex(index, MAX_LINES, "line index");

        modify(() -> {
            synchronized (lines) {
                while (lines.size() <= index) {
                    board.line(lines.size(), Component.empty());
                    lines.add(Component.empty());
                }

                if (!line.equals(lines.get(index))) {
                    board.line(index, line);
                    lines.set(index, line);
                }
            }
        });

        return this;
    }

    /**
     * Removes a single line, shifting the lines below it up.
     *
     * @param index the zero-based line index from the top.
     */
    public Sidebar removeLine(int index) {
        modify(() -> {
            synchronized (lines) {
                if (index < 0 || index >= lines.size()) return;

                final List<Component> copy = new ArrayList<>(lines);
                copy.remove(index);
                lines(copy);
            }
        });

        return this;
    }

    /**
     * Removes all lines, leaving only the title.
     */
    public Sidebar clearLines() {
        return lines(List.of());
    }

    //endregion

    /**
     * Returns the player this sidebar is shown to.
     */
    public Player player() {
        return player;
    }

    /**
     * Returns the underlying scoreboard.
     * <p>
     * Useful for attaching teams (for example name-tag prefixes) to the same board.
     */
    public Scoreboard scoreboard() {
        return board.scoreboard();
    }

    /**
     * Returns whether {@link #remove()} has been called.
     */
    public boolean removed() {
        return removed;
    }

    /**
     * Hides the sidebar and gives the player back the server's main scoreboard.
     * <p>
     * After this call the sidebar can no longer be modified.
     */
    public void remove() {
        if (removed) return;
        removed = true;

        SIDEBARS.remove(player.getUniqueId(), this);

        run(() -> board.hide(player));
    }

    @ApiStatus.Internal
    public static void removeInternal(Player player) {
        final Sidebar sidebar = SIDEBARS.remove(player.getUniqueId());
        if (sidebar != null) {
            sidebar.removed = true;
            sidebar.board.hide(player);
        }
    }

    /**
     * Runs a modification on the global tick thread, now if already there.
     * <p>
     * Throws if the sidebar was already removed; a modification that was re-scheduled and
     * only runs after {@link #remove()} is dropped silently.
     */
    private void modify(Runnable task) {
        Preconditions.checkState(!removed, "sidebar already removed");

        run(() -> {
            if (removed) return;
            task.run();
        });
    }

    /**
     * Runs the task on the global tick thread, now if already there.
     */
    private static void run(Runnable task) {
        if (Bukkit.isGlobalTickThread()) {
            task.run();
        } else {
            PaperScheduler.current().sync().execute(task);
        }
    }
}

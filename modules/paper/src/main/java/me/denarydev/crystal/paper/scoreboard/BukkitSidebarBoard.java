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
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * {@link SidebarBoard} backed by a fresh Bukkit {@link Scoreboard} with one
 * {@link DisplaySlot#SIDEBAR} objective.
 * <p>
 * Lines are scores on fixed entries {@code crystal_line_<index>}; their visible text comes
 * from {@link Score#customName(Component)} and their order from a descending score value,
 * so neither teams nor placeholder strings are needed.
 */
@ApiStatus.Internal
@NullMarked
final class BukkitSidebarBoard implements SidebarBoard {

    private static final String OBJECTIVE_NAME = "crystal_sidebar";
    private static final String ENTRY_PREFIX = "crystal_line_";

    private final Scoreboard scoreboard;
    private final Objective objective;

    BukkitSidebarBoard(Component title, NumberFormat numberFormat) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.numberFormat(numberFormat);
    }

    @Override
    public Scoreboard scoreboard() {
        return scoreboard;
    }

    @Override
    public void title(Component title) {
        objective.displayName(title);
    }

    @Override
    public void numberFormat(NumberFormat format) {
        objective.numberFormat(format);
    }

    @Override
    public void line(int index, Component text) {
        final Score score = objective.getScore(entry(index));
        score.setScore(Sidebar.MAX_LINES - index);
        score.customName(text);
    }

    @Override
    public void clearLine(int index) {
        scoreboard.resetScores(entry(index));
    }

    @Override
    public void show(Player player) {
        player.setScoreboard(scoreboard);
    }

    @Override
    public void hide(Player player) {
        objective.unregister();

        if (player.isOnline() && player.getScoreboard() == scoreboard) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    private static String entry(int index) {
        return ENTRY_PREFIX + index;
    }
}

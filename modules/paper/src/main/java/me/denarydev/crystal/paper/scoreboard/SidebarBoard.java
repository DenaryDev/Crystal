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
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * The scoreboard-side half of a {@link Sidebar}: everything that actually touches Bukkit.
 * <p>
 * Kept separate so the sidebar logic can be tested without a server that implements
 * {@code Score.customName} and {@code Objective.numberFormat}.
 */
@ApiStatus.Internal
@NullMarked
interface SidebarBoard {

    /**
     * Returns the scoreboard this board draws on.
     */
    Scoreboard scoreboard();

    /**
     * Sets the sidebar title.
     */
    void title(Component title);

    /**
     * Sets the number format shared by all lines.
     */
    void numberFormat(NumberFormat format);

    /**
     * Writes the line at the given zero-based index from the top.
     */
    void line(int index, Component text);

    /**
     * Removes the line at the given zero-based index from the top.
     */
    void clearLine(int index);

    /**
     * Shows this board to the player.
     */
    void show(Player player);

    /**
     * Hides this board and gives the player back the main scoreboard.
     */
    void hide(Player player);
}

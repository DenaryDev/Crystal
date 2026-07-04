/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui.actions;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * An action executed when a player clicks a slot in a menu.
 */
@FunctionalInterface
public interface ClickAction {

    /**
     * Called when a player clicks a slot in the menu.
     *
     * @param event the inventory click event.
     */
    void click(final InventoryClickEvent event);
}

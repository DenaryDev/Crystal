/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui.actions;

import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * An action executed when a player closes a menu.
 */
@FunctionalInterface
public interface CloseAction {

    /**
     * Called when the menu is closed.
     *
     * @param event the inventory close event.
     */
    void close(final InventoryCloseEvent event);
}

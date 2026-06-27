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
 * Действие, выполняемое при закрытии меню игроком.
 */
@FunctionalInterface
public interface CloseAction {

    /**
     * Вызывается при закрытии меню.
     *
     * @param event событие закрытия инвентаря
     */
    void close(final InventoryCloseEvent event);
}

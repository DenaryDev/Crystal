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
 * Действие, выполняемое при клике по слоту в меню.
 */
@FunctionalInterface
public interface ClickAction {

    /**
     * Вызывается при клике по слоту в меню.
     *
     * @param event событие клика по инвентарю
     */
    void click(final InventoryClickEvent event);
}

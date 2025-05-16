/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Вы должны зарегистрировать этот слушатель в методе onEnable() вашего плагина.
 * @see org.bukkit.plugin.PluginManager#registerEvents
 */
public final class InventoryListener implements Listener {
    private final Map<UUID, Long> activeCooldowns = new WeakHashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEarlyInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof Menu) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLateInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof final Menu menu
            && event.getCurrentItem() != null && event.getWhoClicked() instanceof final Player player) {
            // Cancel the event again just in case a plugin un-cancels it
            event.setCancelled(true);

            final int slot = event.getSlot();

            if (!menu.hasItem(slot)) return;

            final UUID uuid = player.getUniqueId();
            final Long cooldownUntil = activeCooldowns.get(uuid);
            final long now = System.currentTimeMillis();
            final long cooldown = menu.template().cooldown();

            if (cooldown > 0) {
                if (cooldownUntil != null && cooldownUntil > now) {
                    return;
                } else {
                    activeCooldowns.put(uuid, now + cooldown);
                }
            }

            menu.clickInternal(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof final Menu Menu && event.getPlayer() instanceof Player) {
            Menu.closeInternal(event);
        }
    }
}

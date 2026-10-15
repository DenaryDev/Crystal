/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui;

import me.denarydev.crystal.paper.gui.actions.ClickAction;
import me.denarydev.crystal.paper.listener.InventoryListener;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MenuTest {

    private static final ClickAction NOOP = event -> {
    };

    private ServerMock server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void build_withoutSizeDefaultsTo54AndKeepsItems() {
        final Menu menu = Menu.builder()
            .item(ItemStack.of(Material.STONE), 0, 53)
            .build();

        assertEquals(54, menu.getInventory().getSize());
        assertTrue(menu.hasItem(0));
        assertTrue(menu.hasItem(53));
    }

    @Test
    public void build_withTypeOnly() {
        final Menu menu = Menu.builder()
            .type(InventoryType.HOPPER)
            .item(ItemStack.of(Material.STONE), 4)
            .build();

        assertEquals(InventoryType.HOPPER, menu.getInventory().getType());
        assertEquals(5, menu.getInventory().getSize());
        assertTrue(menu.hasItem(4));
    }

    @Test
    public void addItem_ignoresSlotsOutsideInventory() {
        final Menu menu = Menu.builder().size(9).build();

        menu.addItem(ItemStack.of(Material.STONE), -1, 9, 100);

        assertTrue(menu.getInventory().isEmpty());
    }

    @Test
    public void removeItem_clearsItemAndAction() {
        final Menu menu = Menu.builder()
            .size(9)
            .item(ItemStack.of(Material.STONE), NOOP, 1, 2)
            .build();

        menu.removeItem(1, 42);

        assertFalse(menu.hasItem(1));
        assertNull(menu.action(1));
        assertTrue(menu.hasItem(2));
        assertEquals(NOOP, menu.action(2));
    }

    @Test
    public void removeAction_keepsItem() {
        final Menu menu = Menu.builder()
            .size(9)
            .item(ItemStack.of(Material.STONE), NOOP, 1)
            .build();

        menu.removeAction(1);

        assertTrue(menu.hasItem(1));
        assertNull(menu.action(1));
    }

    @Test
    public void clear_removesEverything() {
        final Menu menu = Menu.builder()
            .size(9)
            .item(ItemStack.of(Material.STONE), NOOP, 1)
            .action(NOOP, 5)
            .build();

        menu.clear();

        assertTrue(menu.getInventory().isEmpty());
        assertNull(menu.action(1));
        assertNull(menu.action(5));
    }

    @Test
    public void reset_restoresInitialState() {
        final ItemStack stone = ItemStack.of(Material.STONE);
        final Menu menu = Menu.builder()
            .size(9)
            .item(stone, NOOP, 1)
            .build();

        menu.removeItem(1);
        menu.addItem(ItemStack.of(Material.DIRT), NOOP, 7);
        menu.reset();

        assertEquals(stone, menu.getInventory().getItem(1));
        assertEquals(NOOP, menu.action(1));
        assertFalse(menu.hasItem(7));
        assertNull(menu.action(7));
    }

    @Test
    public void slots_workWithMenu() {
        final Menu menu = Menu.builder().size(27).build();

        menu.addItem(ItemStack.of(Material.BLACK_STAINED_GLASS_PANE), Slots.border(27));
        menu.addItem(ItemStack.of(Material.DIAMOND), Slots.at(1, 4));

        assertTrue(menu.hasItem(0));
        assertTrue(menu.hasItem(26));
        assertFalse(menu.hasItem(10));
        assertEquals(Material.DIAMOND, menu.getInventory().getItem(13).getType());
    }

    @Test
    public void show_setsViewerAndOpens() {
        final PlayerMock player = server.addPlayer();
        final Menu menu = Menu.builder().size(9).build();

        assertFalse(menu.isOpen());
        assertNull(menu.viewer());

        menu.show(player);

        assertTrue(menu.isOpen());
        assertEquals(player, menu.viewer());
        assertEquals(menu.getInventory(), player.getOpenInventory().getTopInventory());
    }

    @Test
    public void closeInternal_firesCloseActionAndForgetsViewer() {
        final PlayerMock player = server.addPlayer();
        final AtomicInteger closes = new AtomicInteger();
        final Menu menu = Menu.builder().size(9).closeAction(event -> closes.incrementAndGet()).build();

        menu.show(player);
        menu.closeInternal(new InventoryCloseEvent(player.getOpenInventory(), InventoryCloseEvent.Reason.PLAYER));

        assertEquals(1, closes.get());
        assertNull(menu.viewer());
    }

    @Test
    public void reopen_doesNothingWhenNeverShown() {
        final Menu menu = Menu.builder().size(9).build();

        menu.reopen();

        assertFalse(menu.isOpen());
        assertNull(menu.viewer());
    }

    @Test
    public void reopen_showsToLastViewerAgain() {
        final PlayerMock player = server.addPlayer();
        final Menu menu = Menu.builder().size(9).build();

        menu.show(player);
        menu.closeInternal(new InventoryCloseEvent(player.getOpenInventory(), InventoryCloseEvent.Reason.PLAYER));
        player.closeInventory();
        assertNull(menu.viewer());

        menu.reopen();

        assertTrue(menu.isOpen());
        assertEquals(player, menu.viewer());
    }

    @Test
    public void reopen_doesNotShowToOfflinePlayer() {
        final PlayerMock player = server.addPlayer();
        final Menu menu = Menu.builder().size(9).build();

        menu.show(player);
        player.disconnect();
        menu.reopen();

        assertEquals(player, menu.viewer());
        assertFalse(player.isOnline());
    }

    @Test
    public void reopen_doesNotFireCloseAction() {
        server.getPluginManager().registerEvents(new InventoryListener(), MockBukkit.createMockPlugin());

        final PlayerMock player = server.addPlayer();
        final AtomicInteger closes = new AtomicInteger();
        final Menu menu = Menu.builder().size(9).closeAction(event -> closes.incrementAndGet()).build();

        menu.show(player);
        menu.reopen();

        assertEquals(0, closes.get());
        assertTrue(menu.isOpen());
        assertEquals(player, menu.viewer());
    }

    @Test
    public void switchingMenus_firesCloseActionOfTheFirst() {
        server.getPluginManager().registerEvents(new InventoryListener(), MockBukkit.createMockPlugin());

        final PlayerMock player = server.addPlayer();
        final AtomicInteger closes = new AtomicInteger();
        final Menu first = Menu.builder().size(9).closeAction(event -> closes.incrementAndGet()).build();
        final Menu second = Menu.builder().size(9).build();

        first.show(player);
        second.show(player);

        assertEquals(1, closes.get());
        assertNull(first.viewer());
        assertFalse(first.isOpen());
        assertTrue(second.isOpen());
    }
}

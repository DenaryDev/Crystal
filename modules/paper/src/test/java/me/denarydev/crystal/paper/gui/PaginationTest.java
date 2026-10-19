/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui;

import me.denarydev.crystal.paper.gui.actions.ClickAction;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaginationTest {

    private static final ItemStack PREVIOUS = ItemStack.of(Material.ARROW);
    private static final ItemStack NEXT = ItemStack.of(Material.SPECTRAL_ARROW);
    private static final ItemStack EMPTY = ItemStack.of(Material.BARRIER);
    private static final ItemStack BORDER = ItemStack.of(Material.GLASS_PANE);

    private static final int[] CONTENT = Slots.range(0, 2); // 3 entries per page
    private static final int PREVIOUS_SLOT = 6;
    private static final int NEXT_SLOT = 8;
    private static final int EMPTY_SLOT = 4;
    private static final int BORDER_SLOT = 7;

    private ServerMock server;
    private Menu menu;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        menu = Menu.builder().size(9).item(BORDER, BORDER_SLOT).build();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    private static List<Integer> numbers(int count) {
        return IntStream.range(0, count).boxed().toList();
    }

    private static ItemStack render(Integer number) {
        final ItemStack item = ItemStack.of(Material.PAPER);
        item.setAmount(number + 1);
        return item;
    }

    private Pagination.Builder<Integer> builder() {
        return Pagination.<Integer>builder(menu)
            .slots(CONTENT)
            .renderer(PaginationTest::render)
            .previous(PREVIOUS, PREVIOUS_SLOT)
            .next(NEXT, NEXT_SLOT)
            .empty(EMPTY, EMPTY_SLOT);
    }

    private int amountAt(int slot) {
        final ItemStack item = menu.getInventory().getItem(slot);
        assertNotNull(item);
        return item.getAmount();
    }

    @Test
    public void build_drawsFirstPageAndHidesPreviousButton() {
        final Pagination<Integer> pagination = builder().items(numbers(7)).build();

        assertEquals(0, pagination.page());
        assertEquals(3, pagination.pages());
        assertEquals(1, amountAt(0));
        assertEquals(2, amountAt(1));
        assertEquals(3, amountAt(2));
        assertFalse(menu.hasItem(PREVIOUS_SLOT));
        assertTrue(menu.hasItem(NEXT_SLOT));
        assertFalse(menu.hasItem(EMPTY_SLOT));
        assertTrue(menu.hasItem(BORDER_SLOT));
    }

    @Test
    public void page_switchesContentAndButtons() {
        final Pagination<Integer> pagination = builder().items(numbers(7)).build();

        pagination.page(2);

        assertEquals(2, pagination.page());
        assertEquals(7, amountAt(0));
        assertFalse(menu.hasItem(1));
        assertFalse(menu.hasItem(2));
        assertTrue(menu.hasItem(PREVIOUS_SLOT));
        assertFalse(menu.hasItem(NEXT_SLOT));
        assertTrue(menu.hasItem(BORDER_SLOT));
    }

    @Test
    public void page_isClampedToRange() {
        final Pagination<Integer> pagination = builder().items(numbers(7)).build();

        pagination.page(99);
        assertEquals(2, pagination.page());

        pagination.page(-5);
        assertEquals(0, pagination.page());
    }

    @Test
    public void nextAndPrevious_moveOnePage() {
        final Pagination<Integer> pagination = builder().items(numbers(7)).build();

        pagination.next();
        assertEquals(1, pagination.page());
        assertEquals(4, amountAt(0));
        assertTrue(menu.hasItem(PREVIOUS_SLOT));
        assertTrue(menu.hasItem(NEXT_SLOT));

        pagination.previous();
        assertEquals(0, pagination.page());
    }

    @Test
    public void navigationButtons_haveClickActions() {
        final Pagination<Integer> pagination = builder().items(numbers(7)).build();
        final PlayerMock player = server.addPlayer();
        pagination.show(player);

        final ClickAction nextAction = menu.action(NEXT_SLOT);
        assertNotNull(nextAction);
        nextAction.click(click(player, NEXT_SLOT));
        assertEquals(1, pagination.page());

        final ClickAction previousAction = menu.action(PREVIOUS_SLOT);
        assertNotNull(previousAction);
        previousAction.click(click(player, PREVIOUS_SLOT));
        assertEquals(0, pagination.page());
        assertNull(menu.action(PREVIOUS_SLOT));
    }

    @Test
    public void onClick_receivesEntry() {
        final AtomicReference<Integer> clicked = new AtomicReference<>();
        final Pagination<Integer> pagination = builder()
            .items(numbers(7))
            .onClick((number, event) -> clicked.set(number))
            .build();
        final PlayerMock player = server.addPlayer();
        pagination.show(player);
        pagination.page(1);

        final ClickAction action = menu.action(2);
        assertNotNull(action);
        action.click(click(player, 2));

        assertEquals(5, clicked.get());
    }

    @Test
    public void emptyList_showsPlaceholderAndNoButtons() {
        final Pagination<Integer> pagination = builder().build();

        assertEquals(1, pagination.pages());
        assertTrue(menu.hasItem(EMPTY_SLOT));
        assertFalse(menu.hasItem(0));
        assertFalse(menu.hasItem(PREVIOUS_SLOT));
        assertFalse(menu.hasItem(NEXT_SLOT));
    }

    @Test
    public void items_keepsPageWhenItStillExists() {
        final Pagination<Integer> pagination = builder().items(numbers(7)).build();
        pagination.page(1);

        pagination.items(numbers(5));

        assertEquals(1, pagination.page());
        assertEquals(4, amountAt(0));
        assertEquals(5, amountAt(1));
        assertFalse(menu.hasItem(2));
    }

    @Test
    public void items_fallsBackToLastPageWhenCurrentIsGone() {
        final Pagination<Integer> pagination = builder().items(numbers(7)).build();
        pagination.page(2);

        pagination.items(numbers(4));

        assertEquals(1, pagination.page());
        assertEquals(4, amountAt(0));
    }

    @Test
    public void items_emptyShowsPlaceholderAndClearsContent() {
        final Pagination<Integer> pagination = builder().items(numbers(7)).build();

        pagination.items(List.of());

        assertEquals(0, pagination.page());
        assertTrue(menu.hasItem(EMPTY_SLOT));
        assertFalse(menu.hasItem(0));
        assertFalse(menu.hasItem(NEXT_SLOT));
    }

    @Test
    public void items_returnsUnmodifiableCopy() {
        final List<Integer> source = new ArrayList<>(numbers(3));
        final Pagination<Integer> pagination = builder().items(source).build();

        source.add(99);

        assertEquals(3, pagination.items().size());
        assertThrows(UnsupportedOperationException.class, () -> pagination.items().add(1));
    }

    @Test
    public void menu_isTheOneGiven() {
        assertEquals(menu, builder().build().menu());
    }

    @Test
    public void build_requiresSlotsAndRenderer() {
        assertThrows(IllegalStateException.class, () -> Pagination.<Integer>builder(menu).renderer(PaginationTest::render).build());
        assertThrows(IllegalStateException.class, () -> Pagination.<Integer>builder(menu).slots(0, 1).build());
        assertThrows(IllegalArgumentException.class, () -> Pagination.<Integer>builder(menu).slots());
    }

    private InventoryClickEvent click(PlayerMock player, int slot) {
        return new InventoryClickEvent(player.getOpenInventory(), InventoryType.SlotType.CONTAINER, slot,
            ClickType.LEFT, InventoryAction.PICKUP_ALL);
    }
}

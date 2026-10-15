/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.scheduler;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaperSchedulerTest {

    private ServerMock server;
    private Plugin plugin;
    private PaperScheduler scheduler;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
        scheduler = new PaperScheduler(plugin);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void plugin_isTheOneGiven() {
        assertSame(plugin, scheduler.plugin());
    }

    @Test
    public void sync_runsOnNextTick() {
        final AtomicBoolean ran = new AtomicBoolean();

        scheduler.sync().execute(() -> ran.set(true));

        assertFalse(ran.get());
        server.getScheduler().performOneTick();
        assertTrue(ran.get());
    }

    @Test
    public void async_runsOffTheTickLoop() {
        final CompletableFuture<Thread> thread = new CompletableFuture<>();

        scheduler.async().execute(() -> thread.complete(Thread.currentThread()));

        server.getScheduler().performOneTick();
        server.getScheduler().waitAsyncTasksFinished();
        assertTrue(thread.isDone());
    }

    @Test
    public void entity_runsOnNextTick() {
        final PlayerMock player = server.addPlayer();
        final AtomicBoolean ran = new AtomicBoolean();

        scheduler.entity(player).execute(() -> ran.set(true));

        assertFalse(ran.get());
        server.getScheduler().performOneTick();
        assertTrue(ran.get());
    }

    @Test
    public void asyncThenSync_deliversValueOnTick() {
        final List<Integer> received = new ArrayList<>();

        final CompletableFuture<Void> future = scheduler.asyncThenSync(() -> 5, received::add);

        server.getScheduler().performOneTick();
        server.getScheduler().waitAsyncTasksFinished();
        server.getScheduler().performOneTick();

        assertTrue(future.isDone());
        assertEquals(List.of(5), received);
    }
}

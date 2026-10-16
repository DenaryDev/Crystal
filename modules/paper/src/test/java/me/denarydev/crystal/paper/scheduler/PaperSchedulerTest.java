/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.scheduler;

import org.bukkit.Location;
import org.bukkit.World;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @TestPaperSchedulerTest > runAsyncAtFixedRate_repeats() FAILED
    org.opentest4j.AssertionFailedError at PaperSchedulerTest.java:148
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

        server.getScheduler().performOneTick();PaperSchedulerTest > runAsyncAtFixedRate_repeats() FAILED
    org.opentest4j.AssertionFailedError at PaperSchedulerTest.java:148
        server.getScheduler().waitAsyncTasksFinished();
        server.getScheduler().performOneTick();

        assertTrue(future.isDone());
        assertEquals(List.of(5), received);
    }

    @Test
    public void runDelayed_runsAfterDelay() {
        final AtomicBoolean ran = new AtomicBoolean();

        scheduler.runDelayed(() -> ran.set(true), 3);

        server.getScheduler().performTicks(2);
        assertFalse(ran.get());
        server.getScheduler().performOneTick();
        assertTrue(ran.get());
    }

    @Test
    public void runAtFixedRate_repeatsWithPeriod() {
        final AtomicInteger runs = new AtomicInteger();

        // MockBukkit does not implement ScheduledTask.cancel(), so only the cadence is checked here
        assertNotNull(scheduler.runAtFixedRate(runs::incrementAndGet, 1, 2));

        server.getScheduler().performOneTick();
        assertEquals(1, runs.get());
        server.getScheduler().performTicks(4);
        assertEquals(3, runs.get());
    }

    @Test
    public void runAtFixedRate_consumerReceivesItsTask() {
        final CompletableFuture<Object> received = new CompletableFuture<>();

        final var task = scheduler.runAtFixedRate(received::complete, 1, 1);

        server.getScheduler().performOneTick();
        assertTrue(received.isDone());
        assertEquals(task, received.join());
    }

    @Test
    public void runAsyncAtFixedRate_repeats() throws InterruptedException {
        // MockBukkit resubmits the same task object every tick and its "submitted" flag is reset by the first run,
        // so waitAsyncTasksFinished() may cancel the later submissions before the pool picks them up.
        // Wait for the runs directly instead of relying on that method
        final CountDownLatch runs = new CountDownLatch(2);

        scheduler.runAsyncAtFixedRate(runs::countDown, 50, 50, TimeUnit.MILLISECONDS);

        server.getScheduler().performTicks(2);
        assertTrue(runs.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void runAsyncDelayed_runsOffTheTickLoop() throws InterruptedException {
        final CountDownLatch ran = new CountDownLatch(1);

        scheduler.runAsyncDelayed(ran::countDown, 50, TimeUnit.MILLISECONDS);

        server.getScheduler().performOneTick();
        assertTrue(ran.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void entityDelayed_runsAfterDelay() {
        final PlayerMock player = server.addPlayer();
        final AtomicBoolean ran = new AtomicBoolean();

        assertNotNull(scheduler.runDelayed(player, () -> ran.set(true), 2));

        server.getScheduler().performOneTick();
        assertFalse(ran.get());
        server.getScheduler().performOneTick();
        assertTrue(ran.get());
    }

    @Test
    public void entityAtFixedRate_repeats() {
        final PlayerMock player = server.addPlayer();
        final AtomicInteger runs = new AtomicInteger();

        assertNotNull(scheduler.runAtFixedRate(player, runs::incrementAndGet, 1, 1));

        server.getScheduler().performTicks(3);
        assertEquals(3, runs.get());
    }

    @Test
    public void region_runsOnNextTick() {
        final World world = server.addSimpleWorld("world");
        final AtomicBoolean ran = new AtomicBoolean();

        scheduler.region(new Location(world, 100, 64, -200)).execute(() -> ran.set(true));
        scheduler.region(world, 3, -5).execute(() -> ran.set(true));

        assertFalse(ran.get());
        server.getScheduler().performOneTick();
        assertTrue(ran.get());
    }

    @Test
    public void regionDelayed_runsAfterDelay() {
        final World world = server.addSimpleWorld("world");
        final AtomicBoolean ran = new AtomicBoolean();

        assertNotNull(scheduler.runDelayed(new Location(world, 0, 64, 0), () -> ran.set(true), 2));

        server.getScheduler().performOneTick();
        assertFalse(ran.get());
        server.getScheduler().performOneTick();
        assertTrue(ran.get());
    }

    @Test
    public void regionAtFixedRate_repeats() {
        final World world = server.addSimpleWorld("world");
        final AtomicInteger runs = new AtomicInteger();

        assertNotNull(scheduler.runAtFixedRate(new Location(world, 0, 64, 0), runs::incrementAndGet, 1, 1));

        server.getScheduler().performTicks(3);
        assertEquals(3, runs.get());
    }
}

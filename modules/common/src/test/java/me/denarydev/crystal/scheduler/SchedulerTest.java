/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.scheduler;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchedulerTest {

    /**
     * Runs everything immediately but records which executor each task went through.
     */
    private static final class RecordingScheduler implements Scheduler {
        final List<String> log = new ArrayList<>();

        @Override
        public Executor async() {
            return task -> {
                log.add("async");
                task.run();
            };
        }

        @Override
        public Executor sync() {
            return task -> {
                log.add("sync");
                task.run();
            };
        }
    }

    @Test
    public void runAsync_usesAsyncExecutor() {
        final RecordingScheduler scheduler = new RecordingScheduler();

        scheduler.runAsync(() -> {
        }).join();

        assertEquals(List.of("async"), scheduler.log);
    }

    @Test
    public void runSync_usesSyncExecutor() {
        final RecordingScheduler scheduler = new RecordingScheduler();

        scheduler.runSync(() -> {
        }).join();

        assertEquals(List.of("sync"), scheduler.log);
    }

    @Test
    public void supplyAsync_returnsValue() {
        final RecordingScheduler scheduler = new RecordingScheduler();

        assertEquals(42, scheduler.supplyAsync(() -> 42).join());
        assertEquals(List.of("async"), scheduler.log);
    }

    @Test
    public void asyncThenSync_runsSupplierAsyncAndConsumerSync() {
        final RecordingScheduler scheduler = new RecordingScheduler();
        final List<Integer> received = new ArrayList<>();

        scheduler.asyncThenSync(() -> 7, received::add).join();

        assertEquals(List.of(7), received);
        assertEquals(List.of("async", "sync"), scheduler.log);
    }

    @Test
    public void asyncThenSync_skipsConsumerWhenSupplierFails() {
        final RecordingScheduler scheduler = new RecordingScheduler();
        final List<Object> received = new ArrayList<>();

        final CompletableFuture<Void> future = scheduler.asyncThenSync(() -> {
            throw new IllegalStateException("boom");
        }, received::add);

        final CompletionException thrown = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(IllegalStateException.class, thrown.getCause());
        assertTrue(received.isEmpty());
        assertEquals(List.of("async"), scheduler.log);
    }
}

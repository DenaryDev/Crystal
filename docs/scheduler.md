# Crystal — Scheduler

`Scheduler` gives platform-independent access to the asynchronous and synchronous executors of the platform Crystal is running on. It is a thin layer over plain `java.util.concurrent.Executor`s, so it composes with `CompletableFuture` and anything else that accepts an executor.

```java
Scheduler scheduler = Scheduler.current();

// Load off the main thread, then touch the world on it
scheduler.asyncThenSync(() -> repository.load(uuid), data -> menu.show(player));

// Same thing written out with CompletableFuture
CompletableFuture.supplyAsync(() -> repository.load(uuid), scheduler.async())
    .thenAcceptAsync(data -> menu.show(player), scheduler.sync());
```

## Methods

| Method | Description |
|---|---|
| `Scheduler.current()` | The scheduler of the current platform |
| `async()` | `Executor` that runs tasks off the main thread |
| `sync()` | `Executor` that runs tasks on the main thread |
| `runAsync(Runnable)` | Runs the task on `async()`, returns a `CompletableFuture<Void>` |
| `supplyAsync(Supplier<T>)` | Computes a value on `async()`, returns a `CompletableFuture<T>` |
| `runSync(Runnable)` | Runs the task on `sync()`, returns a `CompletableFuture<Void>` |
| `asyncThenSync(Supplier<T>, Consumer<T>)` | Computes on `async()`, consumes on `sync()`; the consumer is skipped if the supplier throws |

## Platform behaviour

| Platform | `async()` | `sync()` |
|---|---|---|
| Paper / Folia | `AsyncScheduler` | `GlobalRegionScheduler` (the main thread on Paper) |
| Velocity | Proxy scheduler | Proxy scheduler — Velocity has no main thread |
| Core | `Async.run` | Runs in the calling thread — Core has no synchronous scheduler |

### Paper: entities and Folia

On Folia the global region is **not** where entities live. Anything that touches a player or another entity (opening a menu, teleporting, modifying inventory) must run on that entity's region thread. `PaperScheduler` adds an executor for exactly that:

```java
PaperScheduler scheduler = PaperScheduler.current();

scheduler.supplyAsync(() -> repository.load(uuid))
    .thenAcceptAsync(data -> menu.show(player), scheduler.entity(player));
```

On regular Paper `entity(...)` runs on the main thread like `sync()`, so code written this way works on both.

Tasks submitted to `entity(...)` after the entity has been removed are silently dropped.

Blocks and chunks follow the same rule: on Folia they belong to the region at their location, so modify them from `region(location)` (or `region(world, chunkX, chunkZ)` when there is no `Location` at hand), not from `sync()`.

```java
scheduler.region(block.getLocation()).execute(() -> block.setType(Material.AIR));
```

### Delayed and repeating tasks

`PaperScheduler` also wraps the delayed and repeating variants of the three schedulers. Each returns the Paper `ScheduledTask`, so the caller can cancel it; the `Consumer<ScheduledTask>` overloads hand the task to itself so it can stop on its own.

```java
scheduler.runDelayed(() -> announce(), 20);                               // main thread, 1 second
scheduler.runAtFixedRate(task -> { if (done()) task.cancel(); }, 1, 20);  // main thread, every second
scheduler.runAsyncAtFixedRate(this::flushStats, 1, 1, TimeUnit.MINUTES);  // off the main thread
scheduler.runAtFixedRate(player, () -> tickEffect(player), 1, 5);         // player's thread, stops when they leave
scheduler.runDelayed(location, () -> regrow(location), 100);              // region's thread, 5 seconds
```

| Method | Thread | Delay unit |
|---|---|---|
| `runDelayed(task, delayTicks)` | main thread (global region) | ticks, at least 1 |
| `runAtFixedRate(task, initialDelayTicks, periodTicks)` | main thread (global region) | ticks, at least 1 |
| `runAsyncDelayed(task, delay, unit)` | async | `TimeUnit` |
| `runAsyncAtFixedRate(task, initialDelay, period, unit)` | async | `TimeUnit` |
| `runDelayed(entity, task, delayTicks)` | entity's thread | ticks, at least 1 |
| `runAtFixedRate(entity, task, initialDelayTicks, periodTicks)` | entity's thread | ticks, at least 1 |
| `runDelayed(location, task, delayTicks)` | region's thread | ticks, at least 1 |
| `runAtFixedRate(location, task, initialDelayTicks, periodTicks)` | region's thread | ticks, at least 1 |

The entity variants return `null` if the entity has already been removed, and a repeating entity task stops when the entity is removed.

### Owning the tasks

`Scheduler.current()` schedules tasks under the Crystal plugin, so they are not cancelled when *your* plugin is disabled. If that matters, create a scheduler bound to your plugin:

```java
PaperScheduler scheduler = new PaperScheduler(plugin);           // Paper
VelocityScheduler scheduler = new VelocityScheduler(proxy, this); // Velocity
```

## Package structure

```
me.denarydev.crystal.scheduler
└── Scheduler                  — interface and default CompletableFuture helpers (crystal-common)

me.denarydev.crystal.paper.scheduler
└── PaperScheduler             — region-aware implementation, adds entity(Entity), region(Location) and delayed/repeating tasks

me.denarydev.crystal.velocity.scheduler
└── VelocityScheduler          — proxy scheduler implementation

me.denarydev.crystal.core.scheduler
└── CoreScheduler              — Async.run implementation
```

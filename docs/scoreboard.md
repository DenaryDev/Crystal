# Crystal — Scoreboard Module *(Paper only)*

A per-player sidebar built on a Bukkit `Scoreboard`. Each line is a `Score` whose text is set through `Score.customName(Component)`, so lines are not limited in length and need no teams or placeholder entries. Score numbers are hidden with `NumberFormat.blank()` by default.

Folia does not support Bukkit scoreboards, so this module is Paper only.

---

## Creating a sidebar

```java
Sidebar sidebar = Sidebar.create(player)
    .title(Component.text("My Server"))
    .lines(
        Component.text("Online: 12"),
        Component.empty(),
        Component.text("play.example.com")
    );
```

`Sidebar.create(player)` gives the player their own scoreboard and shows the sidebar immediately. If the player already had a sidebar, it is replaced.

## Updating

The sidebar is editable at any time after creation, and only the lines whose text changed are re-sent to the client:

```java
sidebar.title(Component.text("New title"));
sidebar.lines(List.of(Component.text("Online: 13"), Component.empty(), Component.text("play.example.com")));

sidebar.line(0, Component.text("Online: 14"));   // one line; pads with empty lines if past the end
sidebar.removeLine(1);                           // shifts the lines below it up
sidebar.clearLines();                            // leaves only the title

sidebar.numberFormat(NumberFormat.noStyle());    // show score numbers again
```

At most `Sidebar.MAX_LINES` (15) lines are rendered by the client.

## Looking up and removing

```java
Sidebar.get(player).ifPresent(Sidebar::remove);
```

`remove()` hides the sidebar and gives the player back the server's main scoreboard; the sidebar cannot be modified after that. Sidebars are removed automatically when the player quits.

## Threading

Every setter may be called from any thread. Calls made off the global tick thread are re-scheduled through `PaperScheduler.sync()`, so the Bukkit scoreboard is only ever touched on the global tick thread. Getters (`title()`, `lines()`, `numberFormat()`) return the last state that was applied.

## Sharing the scoreboard

`sidebar.scoreboard()` exposes the underlying board, so other features that need the player's scoreboard (for example teams for name-tag prefixes) can attach to the same one instead of replacing it.

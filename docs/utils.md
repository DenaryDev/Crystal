# Crystal — Utilities

Standalone static-method utility classes bundled with the `crystal-common` and `crystal-paper` modules.

---

## crystal-common (`me.denarydev.crystal.utils`)

### ColorConverter

Converts legacy colour codes inside a string into MiniMessage tags, so that text written in any common format can be passed straight to `MiniMessage.deserialize`. Plain string processing with no dependency on Adventure, so it is safe to use anywhere `crystal-common` is.

Supported legacy formats, with both `§` and `&` prefixes, case-insensitive:

- named colours `§0`–`§f`, decorations `§k`–`§o` and `§r`
- hex colours `§#rrggbb`
- Bungee hex colours `§x§r§r§g§g§b§b`

Legacy semantics are preserved: a colour code cancels active decorations, so a colour following a decoration is emitted as `<reset><colour>`.

| Method | Description |
|---|---|
| `toMiniMessage(String text)` | Detects the format in order: legacy `§` codes, MiniMessage, legacy `&` codes. `&` codes are converted only when the text contains no MiniMessage tags (so `<green>R&B` stays intact). Text without codes is returned as is |
| `toMiniMessage(List<String> lines)` | Applies `toMiniMessage(String)` to every line |
| `toMiniMessage(String text, char prefix)` | Converts every code with the given prefix, without any heuristics |
| `hasLegacyCodes(String text, char prefix)` | Whether the text contains at least one valid legacy code with the given prefix |
| `hasMiniMessageTags(String text)` | Whether the text contains a standard MiniMessage tag (`<red>`, `</bold>`, `<#ff0000>`, `<click:...>`). Unknown tags like `<world>` don't count |

```java
ColorConverter.toMiniMessage("&a&lHello &cworld");      // "<green><bold>Hello <reset><red>world"
ColorConverter.toMiniMessage("&#12ab34Hex");            // "<#12ab34>Hex"
ColorConverter.toMiniMessage("&x&1&2&a&b&3&4Bungee");   // "<#12ab34>Bungee"
ColorConverter.toMiniMessage("<green>R&B party");       // "<green>R&B party" (MiniMessage, & left alone)
ColorConverter.toMiniMessage("<green>R&B party", '&'); // "<green>R<aqua> party"
```

### NumberUtils

| Method | Description |
|---|---|
| `formatNumber(double)` | Formats a number with `#,###` (whole) or `#,###.00` (fractional), always using `,`/`.` as separators regardless of locale |
| `formatNumber(double, DecimalFormat)` | Formats a number with a custom `DecimalFormat`, still forcing `,`/`.` separators |
| `isInteger(String)` | Whether the string parses as an `int` (via `Integer.parseInt`) |
| `isNumeric(String)` | Whether the string matches a numeric regex (`[-+]?\d*\.?\d+`) |
| `randomValue(Map<Integer, T>)` | Picks a random value from a weighted map, where each key is a percentage chance (**must sum to 100**) |
| `roundAvoid(double, int)` | Rounds a value to the given number of decimal places |

```java
NumberUtils.formatNumber(1234.5);  // "1,234.50"
NumberUtils.isNumeric("-12.3");    // true

T reward = NumberUtils.randomValue(Map.of(
    70, common,   // 70% chance
    25, rare,     // 25% chance
    5, legendary  // 5% chance
));
```

### TextUtils

| Method | Description |
|---|---|
| `capitalize(String)` | Uppercases the first character of a string |
| `capitalizeAll(List<String>)` | Capitalizes each string in a list |
| `capitalizeAll(String...)` | Capitalizes each string in an array |
| `wrapText(String, int)` | Wraps text into lines of at most `maxLength` characters, breaking only at word boundaries |

### Wildcards

Matches strings against glob-style patterns: `*` matches any number of characters, `?` matches exactly one.

| Method | Description |
|---|---|
| `matches(String wildcard, String string)` | Whether the string matches the wildcard pattern |

```java
Wildcards.matches("crystal.*.use", "crystal.gui.use"); // true
```

### TimeFormatter (`me.denarydev.crystal.utils.time`)

Formats durations into localized, human-readable strings (e.g. `"2 d 5 h 30 m"`), skipping any unit whose value is zero. All labels (day/hour/minute/second) are passed in for localization.

| Method | Description |
|---|---|
| `timeToString(long time, TimeUnit unit, String day, String hour, String minute, String second)` | Formats a duration given in an arbitrary `TimeUnit` |
| `ticksToString(long ticks, String day, String hour, String minute, String second)` | Formats a duration given in game ticks (1 tick = 50 ms) |
| `millisToString(long millis, String day, String hour, String minute, String second)` | Formats a duration given in milliseconds |

```java
TimeFormatter.millisToString(93_784_000L, "d", "h", "m", "s"); // "1 d 2 h 3 m 4 s"
```

---

## crystal-paper (`me.denarydev.crystal.paper.utils`)

### ComponentUtils

Shorthand wrappers around `MiniMessage.miniMessage()` for (de)serializing Adventure components, including list variants, plus `send` helpers that parse and deliver a message to any `Audience` (players, the console, command senders) in one call.

| Method | Description |
|---|---|
| `deserialize(String message, TagResolver... tags)` | Parses a MiniMessage string into a `Component` |
| `deserialize(List<String> message, TagResolver... tags)` | Parses a list of MiniMessage strings into `Component`s |
| `serialize(Component component)` | Serializes a `Component` back to a MiniMessage string |
| `serialize(List<Component> components)` | Serializes a list of `Component`s to MiniMessage strings |
| `send(Audience audience, String message, TagResolver... tags)` | Parses a MiniMessage string and sends it to the audience |
| `send(Audience audience, List<String> lines, TagResolver... tags)` | Sends each line as a separate message |
| `sendActionBar(Audience audience, String message, TagResolver... tags)` | Parses a MiniMessage string and shows it in the action bar |

### HeadUtils

Creates and configures player head items from Base64 skin textures (or texture hashes), independent of `ItemBuilder`.

| Method | Description |
|---|---|
| `createHead(String texture)` | Creates a 1-size player head item with the given texture |
| `createHead(String texture, int amount)` | Creates a player head item stack with the given texture and amount (clamped 1–64) |
| `createHead(String texture, String signature)` | Creates a player head item with texture and Mojang signature |
| `createHead(String texture, String signature, int amount)` | Full variant: texture, signature, and amount |
| `setTexture(SkullMeta meta, String texture)` | Applies a texture to existing skull metadata |
| `setTexture(SkullMeta meta, String texture, String signature)` | Applies a texture and signature to existing skull metadata |

> `ItemBuilder.playerHead(texture)` / `ItemBuilder.texture(texture)` use `HeadUtils` internally — see the [Item Builder module](item-builder.md).

### LocationUtils

| Method | Description |
|---|---|
| `inArea(Location loc, Location pos1, Location pos2)` | Whether a location is inside the axis-aligned box defined by two corners |
| `centerLocation(Location location)` | Returns a copy snapped to the horizontal block center (X + 0.5, Z + 0.5); Y is unchanged |
| `findClosestBlock(Location loc, Material type, int radius)` | Finds the nearest block of the given type within `radius` blocks horizontally and Y ± 1 vertically, or `null` if none found. Large radii are discouraged for performance |
| `locationToString(Location location)` | Serializes a location to `world;x;y;z` (world omitted if not loaded; `;yaw;pitch` appended only when either is greater than zero) |
| `locationFromString(String string)` | Parses a string produced by `locationToString` back into a `Location`. Supports the world-less variants too; world is `null` if omitted or not currently loaded. Throws `IllegalArgumentException` for an unrecognized format or `NumberFormatException` if a coordinate/yaw/pitch component isn't a valid number |

### PermissionUtils

| Method | Description |
|---|---|
| `numberFromPermission(Player player, String permission)` | Returns the highest numeric suffix among the player's effective permissions starting with the given prefix (e.g. `"example.limit."` + holding `example.limit.5` and `example.limit.10` → `10`) |

---

## crystal-velocity (`me.denarydev.crystal.velocity.utils`)

### ComponentUtils

Same shorthand wrappers around `MiniMessage.miniMessage()` as the Paper module's [`ComponentUtils`](#componentutils) — a separate class since `crystal-velocity` doesn't depend on `crystal-paper`.

| Method | Description |
|---|---|
| `deserialize(String message, TagResolver... tags)` | Parses a MiniMessage string into a `Component` |
| `deserialize(List<String> message, TagResolver... tags)` | Parses a list of MiniMessage strings into `Component`s |
| `serialize(Component component)` | Serializes a `Component` back to a MiniMessage string |
| `serialize(List<Component> components)` | Serializes a list of `Component`s to MiniMessage strings |
| `send(Audience audience, String message, TagResolver... tags)` | Parses a MiniMessage string and sends it to the audience |
| `send(Audience audience, List<String> lines, TagResolver... tags)` | Sends each line as a separate message |
| `sendActionBar(Audience audience, String message, TagResolver... tags)` | Parses a MiniMessage string and shows it in the action bar |

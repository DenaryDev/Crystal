# Crystal

A shared utility library for Minecraft plugins targeting **Paper** and **Velocity** platforms. Provides common infrastructure for configuration loading, database access, inventory GUI menus, skin management, and more — all built around a clean fluent API.

## Modules

| Module | Description |
|---|---|
| `crystal-common` | Core utilities: config loading, database API, error reporting, skin provider |
| `crystal-paper` | Paper extensions: inventory GUI, item builder, Configurate serializers, chat prompts |
| `crystal-velocity` | Velocity extensions: skin provider, component utilities |

## Requirements

- Java 25+
- Paper 26.2+ *(paper module)*
- Velocity 4.x *(velocity module)*

## Installation

Crystal is deployed as a server-side plugin. Add the repository and declare the module you need as a provided/compile-only dependency.

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://repo.rafaelkauwu.me/public/")
}

dependencies {
    // Paper plugins
    compileOnly("me.denarydev.crystal:crystal-paper:3.2.0-SNAPSHOT")

    // Velocity plugins
    compileOnly("me.denarydev.crystal:crystal-velocity:3.2.0-SNAPSHOT")

    // Platform-agnostic code only
    compileOnly("me.denarydev.crystal:crystal-common:3.2.0-SNAPSHOT")
}
```

### Maven

```xml
<repository>
    <id>rafaelkauwu</id>
    <url>https://repo.rafaelkauwu.me/public/</url>
</repository>
```

```xml
<!-- Paper plugins -->
<dependency>
    <groupId>me.denarydev.crystal</groupId>
    <artifactId>crystal-paper</artifactId>
    <version>3.2.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>

<!-- Velocity plugins -->
<dependency>
    <groupId>me.denarydev.crystal</groupId>
    <artifactId>crystal-velocity</artifactId>
    <version>3.2.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>

<!-- Platform-agnostic code only -->
<dependency>
    <groupId>me.denarydev.crystal</groupId>
    <artifactId>crystal-common</artifactId>
    <version>3.2.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

> **Snapshots** are available from the same repository URL. Use version `3.x.x-SNAPSHOT` to pull the latest development build.

## API Overview

### Error Reporter

`ErrorReporter<T>` is a platform-agnostic interface for structured error handling. On error, it generates a short unique code, logs the full stack trace, and sends the player a clickable in-game message that copies the code to their clipboard.

```java
// Paper
ErrorReporter<Player> reporter = PaperErrorReporter.create(plugin.getSLF4JLogger());

try {
    // ...
} catch (Exception e) {
    reporter.report(player, e, "Failed to load data for {}", player.getName());
}
```

Platform implementations: `PaperErrorReporter` (`crystal-paper`), `VelocityErrorReporter` (`crystal-velocity`).

---

### Skin Provider

`SkinProvider` is an abstract registry for resolving player skin textures by UUID or username. The appropriate implementation is selected automatically: `PaperSkinProvider` on Paper, `VelocitySkinProvider` on Velocity, and `SkinsRestorerSkinProvider` if SkinsRestorer is installed.

```java
Optional<SkinProperty> skin = SkinProvider.current().playerSkin(player.getUniqueId());
skin.ifPresent(s -> applyTexture(s.value(), s.signature()));
```

---

### Chat Prompt *(Paper only)*

`ChatPrompt` intercepts the next chat message from a player and passes it to your handler — without any visible chat output. Supports an optional cancel word, timeout, and a close callback with a reason.

```java
ChatPrompt.show(plugin, player, message -> {
        player.sendMessage("You entered: " + message);
    })
    .cancelWord("cancel")
    .timeout(200L) // 10 seconds
    .closeAction(reason -> {
        if (reason == CloseReason.TIMEOUT) {
            player.sendMessage("Input timed out.");
        }
    });
```

`CloseReason` values: `SUCCESS`, `CANCELLED`, `TIMEOUT`, `ERROR`.

---

### Shared Suggestion Provider *(Paper only)*

`SharedSuggestionProvider` simplifies tab-completion for commands built with Brigadier: it filters a list of candidates against the remaining input, matching on substrings between `.`, `_`, and `/` separators (not just a plain prefix).

```java
public CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggest(plugin.getConfig().getKeys(), builder);
}
```

For example, with input `gui.` it suggests both `crystal.gui.use` and `other.gui.admin`.

---

## Documentation

### [Configurate module](docs/configurate.md)

Config loaders (YAML / HOCON), ObjectMapper-based class deserialization, and Paper type serializers for `Component`, `Material`, `NamespacedKey`, `Location`, and `ItemStack`.

### [Database module](docs/database.md)

Connection pools (file-based and HikariCP), a fluent query builder (SELECT / INSERT / UPDATE / DELETE / CREATE TABLE / raw / batch), and schema loading from SQL files.

### [GUI module](docs/gui.md) *(Paper only)*

Inventory menus with slot-based and character-mask templates, click and close action callbacks, and live menu updates.

### [Item Builder module](docs/item-builder.md) *(Paper only)*

Fluent `ItemStack` builder covering display name/lore, flags, item model/custom model data, enchantments, potion effects, and persistent data.

### [Utilities](docs/utils.md)

Standalone helper classes: numbers, text, wildcards, and time formatting (`crystal-common`); components, player heads, locations, and permissions (`crystal-paper`); components (`crystal-velocity`).

## Development

Code style is enforced with [Spotless](https://github.com/diffplug/spotless). Run `./gradlew spotlessCheck` to verify formatting or `./gradlew spotlessApply` to fix it.

After cloning, install the pre-push hook once so pushes with unformatted code are caught automatically:

`./gradlew spotlessInstallGitPrePushHook`

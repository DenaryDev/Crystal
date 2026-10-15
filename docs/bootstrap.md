# Crystal — Bootstrap Access *(Paper only)*

How to use Crystal classes from your plugin's [bootstrapper](https://docs.papermc.io/paper/dev/getting-started/paper-plugins#bootstrapper), and which parts of the library actually work at that stage.

---

## Why Crystal ships a bootstrapper

Paper loads plugins in two phases: first every bootstrapper, then every plugin main class. A plugin's class loader is made visible to other plugins only when Paper processes that plugin in the current phase, and in the bootstrap phase Paper processes only plugins that declare a `bootstrapper`. A library without one is simply not on the class path of anybody's bootstrapper: loading `me.denarydev.crystal.*` from there ends in `ClassNotFoundException`, and a `required` bootstrap dependency on it fails with `UnknownDependencyException`.

For that reason `crystal-paper` ships an empty `PaperBootstrap`. It does nothing itself, but it makes Crystal a valid bootstrap dependency.

---

## Declaring the dependency

Add Crystal to the `bootstrap` section in addition to `server`:

```yaml
dependencies:
  bootstrap:
    Crystal:
      load: BEFORE
      required: true
      join-classpath: true
  server:
    Crystal:
      load: BEFORE
      required: true
```

With the [plugin-yml](https://github.com/eldoria/plugin-yml) Gradle plugin:

```kotlin
paper {
    bootstrapper = "com.example.MyBootstrap"

    bootstrapDependencies {
        register("Crystal") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
    serverDependencies {
        register("Crystal") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}
```

`join-classpath` defaults to `true`, so leaving it out is fine. Keep the `server` entry: bootstrap dependencies only affect the bootstrap phase.

---

## What is available during bootstrap

The bootstrapper runs before the server exists and before Crystal's own plugin has been constructed. Two things follow:

- **Nothing that needs `Bukkit.getServer()` works.** There are no worlds, players, registries, item factory or scheduler.
- **Crystal itself is not initialised.** `Crystal.instance()` and `Platform.current()` return `null`, `PoolManager.get()` is not set up, and Crystal's `config.yml`, `messages.yml` and `pools.yml` have not been loaded.

So only "pure" classes are usable: ones that depend on neither the server nor the Crystal instance.

### Safe to use

| Class | Module | Notes |
|---|---|---|
| `ConfigLoaders`, `ConfigMapper` | common | Load your own configs; see the [Configurate module](configurate.md) |
| `ColorConverter`, `NumberUtils`, `TextUtils`, `Wildcards`, `TimeFormatter` | common | Static helpers with no runtime state |
| `StringGenerator` | common | Random string generation |
| `ComponentUtils` | paper | Pure Adventure/MiniMessage, no server access |
| `PaperSerializers` | paper | Registering the collection is fine; see the caveat below |
| `SharedSuggestionProvider` | paper | Pure Brigadier, but there are no commands to attach it to yet |

Caveat for `PaperSerializers`: `Component`, `Material` and `NamespacedKey` deserialize without a server, while `Location` and `ItemStack` need a running server and will fail if such a value is read during bootstrap.

### Not available

| Class | Module | Why |
|---|---|---|
| `Crystal.instance()`, `Platform.current()` | common | Set when Crystal's main plugin is constructed, after all bootstrappers |
| `PoolManager`, `ConnectionPoolBuilders`, every query | common | Pools are created in Crystal's `onEnable`; connection pools also log through `Crystal.instance()` |
| `PaperErrorReporter` | paper | Reads Crystal's `messages.yml` when reporting |
| `SkinProvider` | common/paper | No provider is selected until Crystal enables; `PaperSkinProvider` needs online players |
| `Menu`, `Template`, `ItemBuilder`, `ChatPrompt` | paper | Need inventories, `ItemMeta`, the scheduler or players |
| `HeadUtils`, `LocationUtils`, `PermissionUtils` | paper | Need the item factory, worlds or a `Player` |

If you need Crystal's database or messages, do that work in `onEnable` (or in a lifecycle event handler that runs after the server has started), not in the bootstrapper.

---

## Typical use

The bootstrapper is the right place to register [lifecycle events](https://docs.papermc.io/paper/dev/lifecycle) and to read your own config early. Crystal's config tooling covers the second part:

```java
public final class MyBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        MyConfig config;
        try {
            Path file = context.getDataDirectory().resolve("bootstrap.yml");
            config = ConfigMapper.load(ConfigLoaders.yaml(file), MyConfig.class);
        } catch (ConfigurateException e) {
            throw new IllegalStateException("Failed to load bootstrap.yml", e);
        }

        context.getLifecycleManager().registerEventHandler(/* ... */);
    }
}
```

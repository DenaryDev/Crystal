# Crystal — Configurate Module

Integration with [Configurate](https://github.com/SpongePowered/Configurate) for loading and saving plugin configuration files. Crystal uses a custom fork of Configurate that adds **comment preservation in YAML files**.

> **Important:** always import from `io.sapphiremc.lib.configurate`, not `org.spongepowered.configurate`. The API is identical, but the fork is what Crystal ships and what gets shadowed into the JAR.

---

## Creating loaders

`ConfigLoaders` is a factory class with pre-configured defaults for YAML and HOCON.

### YAML

```java
// Simple loader
YamlConfigurationLoader loader = ConfigLoaders.yaml(dataFolder.resolve("config.yml"));

// With a file header
YamlConfigurationLoader loader = ConfigLoaders.yaml(
    dataFolder.resolve("config.yml"),
    MyConfig.HEADER
);

// With additional type serializers
YamlConfigurationLoader loader = ConfigLoaders.yaml(
    dataFolder.resolve("config.yml"),
    PaperSerializers.get()
);

// With full options control
YamlConfigurationLoader loader = ConfigLoaders.yaml(
    dataFolder.resolve("config.yml"),
    options -> options.header(MyConfig.HEADER)
                      .serializers(b -> b.registerAll(PaperSerializers.get()))
);
```

YAML loaders are pre-configured with block node style, 2-space indentation, and no line splitting.

### HOCON

```java
HoconConfigurationLoader loader = ConfigLoaders.hocon(dataFolder.resolve("config.conf"));

// With a header
HoconConfigurationLoader loader = ConfigLoaders.hocon(
    dataFolder.resolve("config.conf"),
    MyConfig.HEADER
);
```

---

## Loading configs into classes

`ConfigMapper` uses Configurate's ObjectMapper to deserialize a configuration file directly into an annotated class instance. If the file does not exist, it is created with all default values on the first load.

```java
YamlConfigurationLoader loader = ConfigLoaders.yaml(
    dataFolder.resolve("config.yml"),
    MyConfig.HEADER
);

MyConfig config = ConfigMapper.load(loader, MyConfig.class);
```

Pass `true` as the third argument to rewrite the file and refresh the order of keys after loading (useful when new fields are added to the class):

```java
MyConfig config = ConfigMapper.load(loader, MyConfig.class, true);
```

### Annotating your config class

Config classes must be annotated with `@ConfigSerializable`. Fields are mapped by name. Use `@Comment` to add comments above a field in the output file.

```java
import io.sapphiremc.lib.configurate.objectmapping.ConfigSerializable;
import io.sapphiremc.lib.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class MyConfig {

    public static final String HEADER = """
        +--------------------------------+
        |           My Plugin            |
        +--------------------------------+
        """;

    @Comment("Maximum number of players allowed.")
    private int maxPlayers = 10;

    @Comment("Welcome message shown on join.")
    private String welcomeMessage = "<green>Welcome, <player>!";

    private DatabaseSection database = new DatabaseSection();

    public int maxPlayers() { return maxPlayers; }
    public String welcomeMessage() { return welcomeMessage; }
    public DatabaseSection database() { return database; }

    @ConfigSerializable
    public static final class DatabaseSection {
        private String host = "localhost";
        private int port = 3306;

        public String host() { return host; }
        public int port() { return port; }
    }
}
```

---

## Paper serializers

`PaperSerializers` provides Configurate type serializers for common Bukkit/Paper types. Register them when creating a loader, then use those types directly in `@ConfigSerializable` classes.

```java
// Option A: get the collection and pass it to ConfigLoaders
YamlConfigurationLoader loader = ConfigLoaders.yaml(path, PaperSerializers.get());

// Option B: register into an existing builder
YamlConfigurationLoader loader = ConfigLoaders.yaml(path, options ->
    options.serializers(PaperSerializers::apply)
);
```

### Registered types

#### `Component` (MiniMessage)

Serialized as a plain MiniMessage-formatted string.

```java
@ConfigSerializable
public final class MyConfig {
    private Component displayName = Component.text("Dragon Sword");
    private Component motd = Component.text("Welcome to the server!");
}
```

```yaml
display-name: "<gold><bold>Dragon Sword"
motd: "<rainbow>Welcome to the server!"
```

---

#### `Material`

Serialized as the Bukkit material name (case-insensitive on read, uppercase on write).

```java
@ConfigSerializable
public final class MyConfig {
    private Material block = Material.DIAMOND_BLOCK;
    private Material tool = Material.IRON_PICKAXE;
}
```

```yaml
block: DIAMOND_BLOCK
tool: IRON_PICKAXE
```

---

#### `NamespacedKey`

Serialized as `namespace:key`.

```java
@ConfigSerializable
public final class MyConfig {
    private NamespacedKey effect = NamespacedKey.minecraft("speed");
    private NamespacedKey customBuff = new NamespacedKey(plugin, "special_buff");
}
```

```yaml
effect: minecraft:speed
custom-buff: myplugin:special_buff
```

---

#### `Location`

Serialized as a semicolon-separated string. Three formats are supported:

| Format | Example |
|---|---|
| `X;Y;Z` | `0.5;64.0;-128.5` |
| `WORLD;X;Y;Z` | `world;100;65;200` |
| `WORLD;X;Y;Z;YAW;PITCH` | `world;100;65;200;90.0;0.0` |

```java
@ConfigSerializable
public final class MyConfig {
    private Location spawn = new Location(Bukkit.getWorld("world"), 0, 64, 0);
    private Location checkpoint = new Location(Bukkit.getWorld("world_nether"), 50, 100, 50, 180.0f, -10.0f);
    private Location relativePoint = new Location(null, 128.0, 70.0, -64.0);
}
```

```yaml
spawn: "world;0.0;64.0;0.0"
checkpoint: "world_nether;50.0;100.0;50.0;180.0;-10.0"
relative-point: "128.0;70.0;-64.0"
```

---

#### `ItemStack`

Serialized as a map of item properties. All fields except `material` are optional.

```java
@ConfigSerializable
public final class MyConfig {
    private ItemStack rewardItem = new ItemBuilder(Material.DIAMOND_SWORD)
        .displayName(Component.text("Excalibur"))
        .lore(
            Component.text("A legendary blade."),
            Component.text("Handle with care.")
        )
        .unbreakable(true)
        .enchantment(Enchantment.SHARPNESS, 5)
        .enchantment(Enchantment.UNBREAKING, 3)
        .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
        .build();
    private ItemStack customHead = HeadUtils.createHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU5ODRmY2JjZTA3MDAwYmI4Y2ZjZGRkNzQ2NTMyN2I0YmMxMzQzY2E3NTVmZDg3ZjkxY2M4NWU0M2VmMjZjZiJ9fX0=");
}
```

```yaml
reward-item:
  material: DIAMOND_SWORD
  name: "<white>Excalibur"
  lore:
    - "<white>A legendary blade."
    - "<white>Handle with care."
  unbreakable: true
  enchants:
    sharpness: 5
    unbreaking: 3
  flags:
    - HIDE_ATTRIBUTES
    - HIDE_ENCHANTS
custom-head:
  material: PLAYER_HEAD
  texture: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU5ODRmY2JjZTA3MDAwYmI4Y2ZjZGRkNzQ2NTMyN2I0YmMxMzQzY2E3NTVmZDg3ZjkxY2M4NWU0M2VmMjZjZiJ9fX0="
```
R
For player heads, use `PLAYER_HEAD` as the material and provide a `texture` (the Base64 texture ID from `textures.minecraft.net/texture/<id>`).

---

## NameAndLore

`NameAndLore` is a lightweight `@ConfigSerializable` record for storing an item's display name and lore as raw MiniMessage strings. It handles MiniMessage parsing on demand, with support for placeholder resolvers.

```yaml
display:
  name: "<gold>Daily Reward"
  lore:
    - "<gray>Come back tomorrow for more!"
    - "<yellow>Streak: <streak> days"
```

```java
@ConfigSerializable
public final class MyConfig {
    private NameAndLore display = NameAndLore.of("<gold>Daily Reward");
}
```

```java
TagResolver streak = Placeholder.unparsed("streak", String.valueOf(player.streak()));

// Apply directly to an item
ItemStack item = new ItemStack(Material.CHEST);
config.display().apply(item, streak);

// Or access name/lore separately
Component name = config.display().name(streak);
List<Component> lore = config.display().lore(streak);
```

---

## Package structure

```
me.denarydev.crystal.config
├── ConfigLoaders              — factory for YAML and HOCON loaders
└── ConfigMapper               — loads config files into @ConfigSerializable classes

me.denarydev.crystal.paper.configurate
├── PaperSerializers           — registers all Paper type serializers
├── common
│   └── NameAndLore            — @ConfigSerializable name + lore helper
└── serializers
    ├── ComponentSerializer    — Component ↔ MiniMessage string
    ├── ItemStackSerializer    — ItemStack ↔ YAML map
    ├── LocationSerializer     — Location ↔ semicolon-separated string
    ├── MaterialSerializer     — Material ↔ material name string
    └── NamespacedKeySerializer — NamespacedKey ↔ namespace:key string
```

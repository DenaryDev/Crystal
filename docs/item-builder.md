# Crystal — Item Builder Module

A fluent builder for constructing `ItemStack`s on the Paper platform. Covers display name/lore, flags, durability, item model/custom model data, enchantments, potion effects, stored enchantments, and persistent data.

---

## Creating a builder

```java
// Empty (AIR) builder
ItemBuilder.empty();

// From a material
ItemBuilder.fromMaterial(Material.DIAMOND_SWORD);

// From an existing item (the original is not modified)
ItemBuilder.fromItem(existingStack);

// Player head with a Base64-encoded skin texture
ItemBuilder.playerHead(texture);
```

## Basic usage

```java
ItemStack sword = ItemBuilder.fromMaterial(Material.DIAMOND_SWORD)
    .displayNameRich("<gold>Excalibur")
    .lorePlain(List.of("A legendary blade"))
    .enchantment(Enchantment.SHARPNESS, 5)
    .unbreakable()
    .itemFlags(ItemFlag.HIDE_ATTRIBUTES)
    .build();
```

`build()` returns a **clone** of the internal item — the builder can keep being reused or reconfigured afterward. `duplicate()` returns a new `ItemBuilder` wrapping a clone of the current state, useful for branching off variants of the same base item.

---

## Method reference

### Type, display name, lore

| Method | Description |
|---|---|
| `type(Material)` | Sets the item material type |
| `texture(String)` | Sets the skin texture for a `PLAYER_HEAD` item (Base64) |
| `amount(int)` | Sets the stack size (1–64) |
| `displayName(Component)` | Sets the display name from an Adventure `Component` |
| `displayNameRich(String, resolvers...)` | Sets the display name from a MiniMessage string |
| `displayNamePlain(String)` | Sets the display name from a plain string |
| `lore(List<Component>)` | Sets the lore from a list of `Component`s |
| `loreRich(List<String>, resolvers...)` | Sets the lore from MiniMessage strings |
| `lorePlain(List<String>)` | Sets the lore from plain strings |
| `appendLore(List<Component>)` / `appendLore(Component...)` | Appends `Component`s to the existing lore |
| `appendLoreRich(List<String>, resolvers...)` | Appends MiniMessage lines to the existing lore |
| `appendLorePlain(List<String>)` | Appends plain lines to the existing lore |

> All `Rich`/`Plain` name and lore setters silently no-op when passed `null`, leaving the current value unchanged. `displayName(Component)` and `lore(List<Component>)` remove the value instead.

> Minecraft renders custom names and lore in italics by default; names and lore set through the builder are not italic unless requested (e.g. `<i>` in MiniMessage). The existing name and lore of items passed to `fromItem(...)` and values set via `editMeta(...)` are left as-is.

### Flags, unbreakability, damage

| Method | Description |
|---|---|
| `itemFlags(ItemFlag...)` | Adds the given item flags |
| `removeFlags(ItemFlag...)` | Removes the given item flags |
| `unbreakable()` / `unbreakable(boolean)` | Sets whether the item can take durability damage |
| `damage(int)` | Sets the item's damage value (items with durability only) |
| `glow(boolean)` | Overrides the enchantment glint: `true` glows without enchantments, `false` hides it even with them |
| `resetGlow()` | Removes the glint override |

### Item model and custom model data

| Method | Description |
|---|---|
| `itemModel(NamespacedKey)` | Sets the item model key |
| `resetItemModel()` | Removes the item model override |
| `customModelDataFloats(List<Float>)` | Sets the float list on the custom model data component |
| `customModelDataFlags(List<Boolean>)` | Sets the boolean flag list on the custom model data component |
| `customModelDataStrings(List<String>)` | Sets the string list on the custom model data component |
| `customModelDataColors(List<Color>)` | Sets the color list on the custom model data component |
| `customModelData(Integer)` | Sets the legacy custom model data integer (`@Deprecated`, use the component-based methods) |

### Enchantments

| Method | Description |
|---|---|
| `enchantment(Enchantment, int)` | Adds one enchantment at the given level |
| `enchantments(Enchantment...)` | Adds enchantments, each at level 1 |
| `enchantments(Map<Enchantment, Integer>)` | Adds enchantments at their specified levels |

### Potion effects (`POTION` / `SPLASH_POTION` / `LINGERING_POTION`)

| Method | Description |
|---|---|
| `potionType(PotionType)` | Sets the base potion type |
| `potionEffects(PotionEffect...)` | Adds effects, overwriting existing effects of the same type |
| `potionEffects(boolean overwrite, PotionEffect...)` | Adds effects with a shared overwrite flag |
| `potionEffects(Map<PotionEffect, Boolean>)` | Adds effects with per-effect overwrite control |
| `removePotionEffect(PotionEffectType)` | Removes the custom effect of the given type |
| `clearPotionEffects()` | Removes all custom effects |

### Stored enchantments (`ENCHANTED_BOOK`)

| Method | Description |
|---|---|
| `storedEnchantment(Enchantment, int)` | Stores one enchantment at the given level |
| `storedEnchantments(Enchantment...)` | Stores enchantments at level 1 each |
| `storedEnchantments(Map<Enchantment, Integer>)` | Stores enchantments at their specified levels |

### Persistent data

| Method | Description |
|---|---|
| `persistentData(NamespacedKey, Object)` | Writes a value to the item's persistent data container. Supports `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `Boolean`, `String`, `Enum<?>` (stored as its name), `byte[]`, `int[]`, `long[]`, and `PersistentDataContainer` |

### Escape hatch and finalization

| Method | Description |
|---|---|
| `editMeta(Consumer<? super ItemMeta>)` | Applies the given consumer to the item's metadata directly, for modifications not covered by other methods |
| `build()` | Returns a clone of the configured `ItemStack` |
| `duplicate()` | Returns a copy of this builder |

> Methods that only apply to specific item types (potions, enchanted books, player heads, damageable items) are no-ops when the current item type doesn't match — they check the meta type internally rather than throwing.

---

## ItemTemplate

`ItemTemplate` is an immutable description of an item whose name and lore are kept as **MiniMessage strings** instead of parsed `Component`s. It is turned into an `ItemStack` on every `build(...)`, so per-player text (PlaceholderAPI placeholders, `<player>` tags) can be substituted each time. It is also a Configurate type, see [`ItemTemplate` in the Configurate docs](configurate.md#itemtemplate).

```java
ItemTemplate template = ItemTemplate.builder(Material.PLAYER_HEAD)
    .texture(texture)
    .name("<gold><player>")
    .lore("<gray>Balance: <yellow>%vault_eco_balance%")
    .glow(true)
    .build();

// Plain MiniMessage build with extra resolvers
ItemStack item = template.build(Placeholder.unparsed("player", player.getName()));

// Strings pass through the preprocessor first, then MiniMessage; Crystal does not depend on PlaceholderAPI
ItemStack item = template.build(
    text -> PlaceholderAPI.setPlaceholders(player, text),
    Placeholder.unparsed("player", player.getName())
);

// Copy an existing item (name and lore are serialized back to MiniMessage)
ItemTemplate copy = ItemTemplate.of(existingStack);

// Templates without '%' placeholders can be built once and cloned
if (!template.hasPlaceholders()) cached = template.build();
```

| Method | Description |
|---|---|
| `builder(Material)` | Creates a builder; `material`, `amount`, `texture`, `name`, `lore`, `flags`, `enchants`, `unbreakable`, `damage`, `glow`, `customModelData`, `itemModel` |
| `of(ItemStack)` | Builds a template from an existing item |
| `toBuilder()` | Builder pre-filled with the template's values |
| `build(TagResolver...)` | Builds the item, parsing name and lore with MiniMessage |
| `build(UnaryOperator<String>, TagResolver...)` | Same, but every string goes through the operator first |
| `hasPlaceholders()` | Whether the name or any lore line contains `%` |

> `build(...)` goes through `ItemBuilder`, so the name and lore get the default non-italic decoration. `of(...)` strips it again, so a build/of round trip keeps the original strings.

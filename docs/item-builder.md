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

> All `Rich`/`Plain` name and lore setters silently no-op when passed `null`, leaving the current value unchanged.

### Flags, unbreakability, damage

| Method | Description |
|---|---|
| `itemFlags(ItemFlag...)` | Adds the given item flags |
| `removeFlags(ItemFlag...)` | Removes the given item flags |
| `unbreakable()` / `unbreakable(boolean)` | Sets whether the item can take durability damage |
| `damage(int)` | Sets the item's damage value (items with durability only) |

### Item model and custom model data

| Method | Description |
|---|---|
| `itemModel(NamespacedKey)` | Sets the item model key |
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

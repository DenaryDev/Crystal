# Crystal — GUI Module

A module for creating inventory menus on the Paper platform. Provides a fluent API for defining menu structure, placing items in slots, and handling click/close events.

---

## Core concepts

**`Menu`** — a live menu instance bound to a specific Bukkit inventory. Created via `Menu.builder()` and shown to a player via `menu.show(player)`.

**`Template`** — an immutable description of a menu's structure (title, size, items, cooldown). Templates are designed to be cached and reused: one template → many menus for different players.

Using templates is **optional** — for simple or fully dynamic menus, `Menu.builder()` without a template is enough.

> Menu items are plain `ItemStack`s. It's recommended to build them with the [Item Builder module](item-builder.md)'s `ItemBuilder` rather than assembling `ItemMeta` by hand.

---

## Creating a menu

### Without a template

```java
Menu menu = Menu.builder()
    .titleRich("<dark_gray>Settings")
    .size(27)
    .item(settingsItem, event -> openSettings(player), 13)
    .closeAction(event -> player.sendMessage("Menu closed"))
    .build();

menu.show(player);
```

### With SimpleTemplate

`SimpleTemplate` is a slot-number-based template. Suitable for fixed menus and standard chest layouts.

```java
// Create the template once (e.g. on plugin load)
SimpleTemplate template = Template.simpleBuilder()
    .titlePlain("Shop")
    .size(54)
    .item(borderItem, 0, 1, 2, 3, 4, 5, 6, 7, 8)
    .cooldown(200L)
    .build();

// Build a menu from the template, adding dynamic elements
Menu menu = Menu.builder(template)
    .item(shopItem, event -> buyItem(player), 26)
    .build();

menu.show(player);
```

### With MatrixTemplate

> **Experimental (`@ApiStatus.Experimental`)**

`MatrixTemplate` is a character-mask-based template. Each line of the mask is exactly 9 characters (one row of the inventory); each character maps to an item via `element(char, item)`.

```java
MatrixTemplate template = Template.matrixBuilder()
    .titlePlain("Inventory")
    .matrix(
        "BBBBBBBBB\n" +
        "B       B\n" +
        "B  III  B\n" +
        "B       B\n" +
        "BBBBBBBBB"
    )
    .element('B', borderItem)
    .element('I', infoItem)
    .cooldown(100L)
    .build();

// Get all slots for a character (to bind actions)
int[] infoSlots = template.slotsByCharacter('I');

Menu menu = Menu.builder(template)
    .action(event -> showInfo(player), infoSlots)
    .build();

menu.show(player);
```

The matrix accepts 1 to 6 rows. Characters with no `element` binding are treated as empty slots.

---

## Managing an open menu

```java
// Show to a player
menu.show(player);

// Update content after show()
menu.addItem(updatedItem, 13);
menu.update();

// Close programmatically
menu.close();

// Check whether a slot has an item
boolean occupied = menu.hasItem(13);

// Get the current viewer (null if nobody is viewing)
Player viewer = menu.viewer();
```

> `addItem` and `addAction` take effect **only after calling `update()`** when the menu is already open.

> `addAction` registers an action on a slot, but it only fires if that slot contains an item.

---

## Actions

### ClickAction

A functional interface for handling slot clicks.

```java
ClickAction action = event -> {
    // The event is already cancelled — no need to call setCancelled(true)
    Player player = (Player) event.getWhoClicked();
    player.sendMessage("You clicked slot " + event.getSlot());
};
```

> `InventoryClickEvent` is **already cancelled** when `ClickAction` is invoked: the listener cancels it at `LOWEST` priority and again at `MONITOR` (just before the action fires) to prevent interference from other plugins.

> `ClickAction` fires **only for slots that contain an item**. Clicks on empty slots are ignored by the listener regardless of whether an action is registered on them.

### CloseAction

A functional interface for handling menu close events.

```java
CloseAction onClose = event -> {
    Player player = (Player) event.getPlayer();
    player.sendMessage("Menu closed");
};
```

---

## Builder reference

### Menu.Builder (via `Menu.builder()` or `Menu.builder(template)`)

| Method | Description |
|---|---|
| `title(Component)` | Title as an Adventure `Component` |
| `titleRich(String, resolvers...)` | Title with MiniMessage formatting |
| `titlePlain(String)` | Plain-text title without formatting |
| `size(int)` | Inventory size (multiple of 9, from 9 to 54) |
| `type(InventoryType)` | Inventory type (HOPPER, DISPENSER, etc.); overrides `size` |
| `item(item, slots...)` | Place an item in the given slots |
| `item(item, action, slots...)` | Place an item with a click action in the given slots |
| `action(action, slots...)` | Register a click action on slots; fires only if the slot contains an item |
| `cooldown(long)` | Delay between click handling (ms) |
| `closeAction(action)` | Action to run when the menu is closed |
| `build()` | Create the `Menu` instance |

### Template.Builder (shared by SimpleTemplate and MatrixTemplate)

| Method | Description |
|---|---|
| `title(Component)` | Title as an Adventure `Component` |
| `titleRich(String, resolvers...)` | Title with MiniMessage formatting |
| `titlePlain(String)` | Plain-text title |
| `cooldown(long)` | Delay between clicks (ms, ≥ 0) |

### SimpleTemplate.Builder (via `Template.simpleBuilder()`)

Inherits `Template.Builder` methods, plus:

| Method | Description |
|---|---|
| `size(int)` | Inventory size (multiple of 9, from 9 to 54) |
| `type(InventoryType)` | Inventory type; overrides `size` |
| `item(item, slots...)` | Place an item in the given slots |
| `items(Map<Integer, ItemStack>)` | Place multiple items at once |
| `build()` | Create the `SimpleTemplate` |

### MatrixTemplate.Builder (via `Template.matrixBuilder()`) `@Experimental`

Inherits `Template.Builder` methods, plus:

| Method | Description |
|---|---|
| `matrix(String)` | Define the mask as a `\n`-delimited string |
| `matrix(List<String>)` | Define the mask as a list of strings (1–6 rows of 9 characters each) |
| `element(char, item)` | Bind an item to a character |
| `elements(Map<Character, ItemStack>)` | Bind multiple characters at once |
| `build()` | Create the `MatrixTemplate` (requires `matrix` and at least one `element`) |

---

## Package structure

```
me.denarydev.crystal.paper.gui
├── Menu                       — menu instance and its Builder
├── Template                   — abstract base class for templates and its Builder
├── template
│   ├── SimpleTemplate         — slot-number-based template
│   └── MatrixTemplate         — character-mask-based template (@Experimental)
└── actions
    ├── ClickAction            — @FunctionalInterface: handles a slot click
    └── CloseAction            — @FunctionalInterface: handles menu close
```
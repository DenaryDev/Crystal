# Crystal — GUI Module

Модуль для создания инвентарных меню на платформе Paper. Предоставляет fluent API для описания структуры меню, привязки предметов к слотам и обработки кликов/закрытия.

---

## Основные концепции

**`Menu`** — живой экземпляр меню, привязанный к конкретному Bukkit-инвентарю. Создаётся через `Menu.builder()` и отображается игроку через `menu.show(player)`.

**`Template`** — неизменяемое описание структуры меню (заголовок, размер, предметы, кулдаун). Шаблоны удобно кешировать и переиспользовать: один шаблон → много меню для разных игроков.

Использование шаблонов **необязательно** — для простых или полностью динамических меню достаточно `Menu.builder()` без шаблона.

---

## Создание меню

### Без шаблона

```java
Menu menu = Menu.builder()
    .titleRich("<dark_gray>Настройки")
    .size(27)
    .item(settingsItem, event -> openSettings(player), 13)
    .closeAction(event -> player.sendMessage("Меню закрыто"))
    .build();

menu.show(player);
```

### С SimpleTemplate

`SimpleTemplate` — шаблон на основе прямой нумерации слотов. Подходит для фиксированных меню и стандартных сундуков.

```java
// Создать шаблон один раз (например, при загрузке плагина)
SimpleTemplate template = Template.simpleBuilder()
    .titlePlain("Магазин")
    .size(54)
    .item(borderItem, 0, 1, 2, 3, 4, 5, 6, 7, 8)
    .cooldown(200L)
    .build();

// Создать меню по шаблону, добавив динамические элементы
Menu menu = Menu.builder(template)
    .item(shopItem, event -> buyItem(player), 26)
    .build();

menu.show(player);
```

### С MatrixTemplate

> **Экспериментально (`@ApiStatus.Experimental`)**

`MatrixTemplate` — шаблон на основе символьной маски. Каждая строка маски — ровно 9 символов (одна строка инвентаря); каждый символ сопоставляется с предметом через `element(char, item)`.

```java
MatrixTemplate template = Template.matrixBuilder()
    .titlePlain("Инвентарь")
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

// Получить все слоты символа (для привязки действий)
int[] infoSlots = template.slotsByCharacter('I');

Menu menu = Menu.builder(template)
    .action(event -> showInfo(player), infoSlots)
    .build();

menu.show(player);
```

Матрица принимает от 1 до 6 строк. Символы, для которых не задан `element`, считаются пустыми слотами.

---

## Управление открытым меню

```java
// Открыть
menu.show(player);

// Обновить содержимое после show()
menu.addItem(updatedItem, 13);
menu.update();

// Программно закрыть
menu.close();

// Проверить, есть ли предмет в слоте
boolean occupied = menu.hasItem(13);

// Узнать, кому открыто меню (null, если никому)
Player viewer = menu.viewer();
```

> Методы `addItem` и `addAction` вступают в силу **только после вызова `update()`**, если меню уже открыто игроку.

> `addAction` регистрирует действие на слот, но оно сработает только если в этом слоте есть предмет.

---

## Действия

### ClickAction

Функциональный интерфейс для обработки кликов по слоту.

```java
ClickAction action = event -> {
    // event уже отменён — вызывать setCancelled(true) не нужно
    Player player = (Player) event.getWhoClicked();
    player.sendMessage("Вы нажали на слот " + event.getSlot());
};
```

> `InventoryClickEvent` **уже отменён** к моменту вызова `ClickAction`: слушатель отменяет его на приоритете `LOWEST`, и повторно — на `MONITOR` (перед самым вызовом действия), чтобы исключить вмешательство других плагинов.

> `ClickAction` срабатывает **только для слотов с предметом**. Клики по пустым слотам игнорируются слушателем независимо от того, зарегистрировано ли на них действие.

### CloseAction

Функциональный интерфейс для обработки закрытия меню.

```java
CloseAction onClose = event -> {
    Player player = (Player) event.getPlayer();
    player.sendMessage("Меню закрыто");
};
```

---

## Builder — справочник методов

### Menu.Builder (через `Menu.builder()` или `Menu.builder(template)`)

| Метод | Описание |
|---|---|
| `title(Component)` | Заголовок через Adventure `Component` |
| `titleRich(String, resolvers...)` | Заголовок с MiniMessage-форматированием |
| `titlePlain(String)` | Заголовок как обычный текст без форматирования |
| `size(int)` | Размер инвентаря (кратно 9, от 9 до 54) |
| `type(InventoryType)` | Тип инвентаря (HOPPER, DISPENSER и т.д.); перекрывает `size` |
| `item(item, slots...)` | Добавить предмет в слоты |
| `item(item, action, slots...)` | Добавить предмет с действием в слоты |
| `action(action, slots...)` | Добавить действие на слоты; срабатывает только если в слоте есть предмет |
| `cooldown(long)` | Задержка между обработкой кликов (мс) |
| `closeAction(action)` | Действие при закрытии меню |
| `build()` | Создать экземпляр `Menu` |

### Template.Builder (общие методы для SimpleTemplate и MatrixTemplate)

| Метод | Описание |
|---|---|
| `title(Component)` | Заголовок через Adventure `Component` |
| `titleRich(String, resolvers...)` | Заголовок с MiniMessage-форматированием |
| `titlePlain(String)` | Заголовок как обычный текст |
| `cooldown(long)` | Задержка между кликами (мс, ≥ 0) |

### SimpleTemplate.Builder (через `Template.simpleBuilder()`)

Наследует методы `Template.Builder`, плюс:

| Метод | Описание |
|---|---|
| `size(int)` | Размер инвентаря (кратно 9, от 9 до 54) |
| `type(InventoryType)` | Тип инвентаря; перекрывает `size` |
| `item(item, slots...)` | Добавить предмет в слоты |
| `items(Map<Integer, ItemStack>)` | Добавить несколько предметов сразу |
| `build()` | Создать `SimpleTemplate` |

### MatrixTemplate.Builder (через `Template.matrixBuilder()`) `@Experimental`

Наследует методы `Template.Builder`, плюс:

| Метод | Описание |
|---|---|
| `matrix(String)` | Задать маску строкой, разделённой `\n` |
| `matrix(List<String>)` | Задать маску списком строк (1–6 строк по 9 символов) |
| `element(char, item)` | Привязать предмет к символу |
| `elements(Map<Character, ItemStack>)` | Привязать несколько символов сразу |
| `build()` | Создать `MatrixTemplate` (требует `matrix` и хотя бы один `element`) |

---

## Структура пакета

```
me.denarydev.crystal.paper.gui
├── Menu                       — экземпляр меню и его Builder
├── Template                   — абстрактный базовый класс шаблона и его Builder
├── template
│   ├── SimpleTemplate         — шаблон по номерам слотов
│   └── MatrixTemplate         — шаблон по символьной маске (@Experimental)
└── actions
    ├── ClickAction            — @FunctionalInterface: обработка клика по слоту
    └── CloseAction            — @FunctionalInterface: обработка закрытия меню
```

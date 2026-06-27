# Crystal — Database Module

Модуль для работы с базами данных в Minecraft-плагинах. Предоставляет единый API для подключения к различным СУБД, управления пулами соединений и выполнения SQL-запросов через fluent-интерфейс.

---

## Поддерживаемые базы данных

| Тип          | `DatabaseType`  | Драйвер             |
|--------------|-----------------|---------------------|
| SQLite       | `SQLITE`        | файловый (встроен)  |
| H2           | `H2`            | файловый (встроен)  |
| MySQL        | `MYSQL`         | HikariCP            |
| MariaDB      | `MARIADB`       | HikariCP            |
| PostgreSQL   | `POSTGRESQL`    | HikariCP            |

Файловые БД (SQLite, H2) хранят данные в указанном `.db`-файле. Удалённые (MySQL, MariaDB, PostgreSQL) используют пул соединений на основе [HikariCP](https://github.com/brettwooldridge/HikariCP).

---

## Пулы соединений

### Через PoolManager (рекомендуется)

`PoolManager` — синглтон, который читает конфигурацию пулов из файла `pools.yml` и инициализирует их лениво при первом обращении.

```java
// Получить пул по имени (Optional)
Optional<ConnectionPool> pool = PoolManager.get().getPool("my_pool");

// Получить пул или выбросить исключение, если не найден
ConnectionPool pool = PoolManager.get().requirePool("my_pool");
```

Пулы создаются один раз и переиспользуются. Псевдонимы (aliases) позволяют обращаться к одному пулу под несколькими именами.

### Вручную через ConnectionPoolBuilders

Если пул нужно создать без конфигурационного файла:

```java
// MariaDB
ConnectionPool pool = ConnectionPoolBuilders.mariadb()
    .poolPrefix("MyPlugin")
    .address("127.0.0.1")
    .database("my_db")
    .username("root")
    .password("secret")
    .build();

pool.initialize();

// SQLite
ConnectionPool pool = ConnectionPoolBuilders.sqlite()
    .file(dataFolder.resolve("storage.db"))
    .build();

pool.initialize();
```

Доступные билдеры: `sqlite()`, `h2()`, `mysql()`, `mariadb()`, `postgresql()`.

---

## Query API

Получив `ConnectionPool`, можно строить запросы через `pool.query()`:

```java
QueryBuilder q = pool.query();
```

> **Важно:** все методы выполнения запросов бросают проверяемое `SQLException`, которое необходимо обрабатывать.

---

### Методы выполнения (AbstractQuery)

Доступны на любом типе запроса.

| Метод | Описание |
|---|---|
| `update()` | Выполняет запрос как UPDATE/INSERT/DELETE, возвращает кол-во затронутых строк |
| `updateWithKeys(connection)` | Выполняет запрос на переданном соединении и возвращает `ResultSetWrapper` со сгенерированными ключами (соединение закрывает вызывающая сторона) |
| `updateWithKeysAndMap(mapper)` | Выполняет запрос и маппит первый сгенерированный ключ в `Optional<T>` |
| `updateWithKeysAndMapAll(mapper)` | Выполняет запрос и маппит все сгенерированные ключи в `List<T>` |
| `query(connection)` | Выполняет SELECT на переданном соединении и возвращает `ResultSetWrapper` (соединение закрывает вызывающая сторона) |
| `queryAndMap(mapper)` | Выполняет SELECT и маппит первую строку в `Optional<T>` |
| `queryAndMapAll(mapper)` | Выполняет SELECT и маппит все строки в `List<T>` |

> `updateWithKeys(connection)` и `query(connection)` принимают соединение явно — это нужно для транзакций, когда несколько запросов должны выполниться на одном `Connection`.

`ResultSetMapper<T>` — функциональный интерфейс `T map(ResultSet set) throws SQLException`.

---

### WHERE-условия (SELECT, UPDATE, DELETE)

| Метод | Генерирует |
|---|---|
| `where(column, value)` | `` `column` = ? `` |
| `whereExpr(expr, params...)` | произвольное SQL-выражение |
| `whereNull(column)` | `` `column` IS NULL `` |
| `whereNullable(column, value)` | IS NULL или `= ?` в зависимости от значения |

Несколько вызовов объединяются через `AND`.

---

### SELECT

```java
try {
    // Одна запись
    Optional<MyObject> result = q.select("id", "name")
        .from("users")
        .where("id", userId)
        .queryAndMap(rs -> new MyObject(rs.getInt("id"), rs.getString("name")));

    // Все записи
    List<MyObject> all = q.select().all()
        .from("users")
        .whereExpr("`score` > ?", 100)
        .orderBy("name").desc()
        .limit(50).offset(0)
        .queryAndMapAll(rs -> /* ... */);
} catch (SQLException e) { }
```

| Метод | Описание |
|---|---|
| `all()` | Выбрать все столбцы (`SELECT *`) |
| `column(name)` | Добавить столбец по имени |
| `expression(expr, params...)` | Добавить произвольное SQL-выражение |
| `from(table)` | Указать таблицу |
| `from(database, table)` | Указать базу данных и таблицу |
| `orderBy(column)` | Сортировка по столбцу |
| `orderByExpr(expr, params...)` | Сортировка по произвольному выражению |
| `desc()` | Добавить `DESC` к сортировке |
| `limit(n)` | Ограничить количество строк |
| `offset(n)` | Пропустить n строк |
| `forUpdate()` | Добавить `FOR UPDATE` |

---

### INSERT

```java
try {
    // Обычная вставка
    q.insertInto("users")
        .value("id", uuid)
        .value("name", name)
        .valueNull("avatar")
        .update();

    // INSERT IGNORE
    q.insertInto("users")
        .ignore()
        .value("id", uuid)
        .value("name", name)
        .update();

    // Upsert: ON DUPLICATE KEY UPDATE
    q.insertInto("users")
        .value("id", uuid)
        .value("name", name)
        .value("score", score)
        .onDuplicateKeyUpdateExcept("id")   // обновить все столбцы кроме ключа
        .update();
} catch (SQLException e) { }
```

| Метод | Описание                                                                |
|---|-------------------------------------------------------------------------|
| `ignore()` | Добавить `INSERT IGNORE`                                                |
| `value(column, value)` | Вставить значение                                                       |
| `valueExpr(column, expr, params...)` | Вставить произвольное SQL-выражение                                     |
| `valueNull(column)` | Вставить NULL                                                           |
| `valueNullable(column, value)` | Вставить NULL или значение в зависимости от value                       |
| `onDuplicateKeyUpdateExcept(keys...)` | `ON DUPLICATE KEY UPDATE` для всех столбцов, кроме помеченных как ключи |

---

### UPDATE

```java
try {
    q.update("users")
        .value("name", newName)
        .valueExpr("score", "`score` + ?", delta)
        .valueNull("avatar")
        .where("id", uuid)
        .update();
} catch (SQLException e) { }
```

| Метод | Описание |
|---|---|
| `value(column, value)` | Установить значение |
| `valueExpr(column, expr, params...)` | Установить произвольное SQL-выражение |
| `valueNull(column)` | Установить NULL |
| `valueNullable(column, value)` | NULL или значение в зависимости от value |

---

### DELETE

```java
try {
    q.deleteFrom("users")
        .where("id", uuid)
        .update();
} catch (SQLException e) { }
```

Только WHERE-условия, других методов нет.

---

### CREATE TABLE

Модификаторы `notNull()`, `primaryKey()`, `autoIncrement()`, `defaultValue()` применяются к **последнему добавленному** столбцу.

```java
try {
    q.createTable("users")
        .ifNotExists()
        .intKey("id")                        // INT AUTO_INCREMENT PRIMARY KEY
        .varchar("name", 64).notNull()
        .integer("score").notNull().defaultValue(0)
        .bool("active").notNull().defaultValue(1)
        .update();
} catch (SQLException e) { }
```

**Методы добавления столбцов:**

| Метод | SQL-тип |
|---|---|
| `column(name, type)` | произвольный тип |
| `integer(name)` | `INT` |
| `bigint(name)` | `BIGINT` |
| `bool(name)` | `TINYINT(1)` |
| `varchar(name, size)` | `VARCHAR(size)` |
| `character(name, size)` | `CHAR(size)` |
| `text(name)` | `TEXT` |
| `serial(name)` | `SERIAL` (только PostgreSQL) |
| `bigSerial(name)` | `BIGSERIAL` (только PostgreSQL) |
| `intKey(name)` | `INT AUTO_INCREMENT PRIMARY KEY` |

**Модификаторы последнего столбца:**

| Метод | Добавляет |
|---|---|
| `notNull()` | `NOT NULL` |
| `autoIncrement()` | `AUTO_INCREMENT` |
| `primaryKey()` | `PRIMARY KEY` |
| `defaultValue(value)` | `DEFAULT value` |

---

### Raw-запросы

```java
try {
    pool.query()
        .raw("UPDATE users SET score = score + ? WHERE id = ?", 10, uuid)
        .update();
} catch (SQLException e) { }
```

---

### Batch-запросы

Для массового выполнения однотипных запросов через `pool.batch()`. Все запросы в пакете должны иметь одинаковый SQL-текст.

```java
try {
    BatchBuilder batch = pool.batch();

    for (User user : users) {
        batch.add(
            pool.query().insertInto("users")
                .value("id", user.id())
                .value("name", user.name())
        );
    }

    batch.execute();
} catch (SQLException e) { }
```

| Метод | Описание |
|---|---|
| `add(query)` | Добавить запрос в пакет |
| `execute()` | Выполнить пакет, вернуть `int[]` с кол-вом затронутых строк по каждому запросу |

---

## SchemaReader

Позволяет загрузить схему таблиц из `.sql`-файла (например, из ресурсов плагина):

```java
List<String> statements = SchemaReader.getStatements(
    getClass().getResourceAsStream("/schema.sql")
);

for (String sql : statements) {
    pool.query().raw(sql).update();
}
```

Комментарии (`--`, `#`) и пустые строки пропускаются автоматически.

---

## Структура пакета

```
me.denarydev.crystal.database
├── DatabaseType                  — перечисление поддерживаемых СУБД
├── connection
│   ├── ConnectionPool            — абстрактный пул соединений (базовый класс)
│   ├── ConnectionPoolBuilders    — фабричные методы для создания билдеров пулов
│   ├── file
│   │   ├── FlatfileConnectionPool — база для файловых пулов
│   │   ├── SQLiteConnectionPool
│   │   └── H2ConnectionPool
│   └── hikari
│       ├── HikariConnectionPool  — база для удалённых пулов (HikariCP)
│       ├── MySqlConnectionPool
│       ├── MariaDBConnectionPool
│       └── PostgresConnectionPool
├── pool
│   ├── PoolManager               — синглтон-менеджер именованных пулов
│   └── impl.PoolManagerImpl      — внутренняя реализация (конфиг + lazy-инициализация)
├── query
│   ├── QueryBuilder              — точка входа для построения запросов
│   ├── AbstractQuery             — базовый класс запроса (execute/query/update)
│   ├── ConditionalQuery          — расширение с поддержкой WHERE
│   ├── BatchBuilder              — пакетное выполнение запросов
│   ├── Dialect                   — перечисление диалектов SQL
│   ├── Expression                — SQL-выражение с параметрами
│   ├── impl                      — конкретные реализации (Select, Insert, Update, Delete, CreateTable, Raw)
│   └── set
│       ├── ResultSetWrapper      — обёртка над ResultSet с методами map/mapAll
│       └── ResultSetMapper       — функциональный интерфейс для маппинга строк
└── schema
    └── SchemaReader              — чтение SQL-схем из файлов/потоков
```

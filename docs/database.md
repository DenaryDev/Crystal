# Crystal — Database Module

A module for working with databases in Minecraft plugins. Provides a unified API for connecting to various database engines, managing connection pools, and executing SQL queries through a fluent interface.

---

## Supported databases

| Type | `DatabaseType` | Driver |
|---|---|---|
| SQLite | `SQLITE` | file-based |
| H2 | `H2` | file-based |
| MySQL | `MYSQL` | HikariCP |
| MariaDB | `MARIADB` | HikariCP |
| PostgreSQL | `POSTGRESQL` | HikariCP |

File-based databases (SQLite, H2) store data in the specified `.db` file. Remote databases (MySQL, MariaDB, PostgreSQL) use a connection pool backed by [HikariCP](https://github.com/brettwooldridge/HikariCP).

---

## Connection pools

### Via PoolManager (recommended)

`PoolManager` is a singleton that reads pool configuration from `pools.yml` and initializes each pool lazily on first access.

```java
// Get a pool by name (Optional)
Optional<ConnectionPool> pool = PoolManager.get().getPool("my_pool");

// Get a pool or throw if not found
ConnectionPool pool = PoolManager.get().requirePool("my_pool");
```

Pools are created once and reused. Aliases let you refer to a single pool under multiple names.

### Manually via ConnectionPoolBuilders

When you need to create a pool without a configuration file:

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

Available builders: `sqlite()`, `h2()`, `mysql()`, `mariadb()`, `postgresql()`.

---

## Query API

Once you have a `ConnectionPool`, build queries via `pool.query()`:

```java
QueryBuilder q = pool.query();
```

> **Note:** all query execution methods throw a checked `SQLException` that must be handled.

---

### Execution methods (AbstractQuery)

Available on every query type.

| Method | Description |
|---|---|
| `update()` | Executes the query as UPDATE/INSERT/DELETE; returns the number of affected rows |
| `updateWithKeys(connection)` | Executes the query on the given connection and returns a `ResultSetWrapper` containing the generated keys (the caller is responsible for closing the connection) |
| `updateWithKeysAndMap(mapper)` | Executes the query and maps the first generated key into `Optional<T>` |
| `updateWithKeysAndMapAll(mapper)` | Executes the query and maps all generated keys into `List<T>` |
| `query(connection)` | Executes a SELECT on the given connection and returns a `ResultSetWrapper` (the caller closes the connection) |
| `queryAndMap(mapper)` | Executes a SELECT and maps the first row into `Optional<T>` |
| `queryAndMapAll(mapper)` | Executes a SELECT and maps all rows into `List<T>` |

> `updateWithKeys(connection)` and `query(connection)` accept an explicit connection — useful for transactions where multiple queries must run on the same `Connection`.

`ResultSetMapper<T>` is a functional interface: `T map(ResultSet set) throws SQLException`.

---

### WHERE conditions (SELECT, UPDATE, DELETE)

| Method | Generates |
|---|---|
| `where(column, value)` | `` `column` = ? `` |
| `whereExpr(expr, params...)` | arbitrary SQL expression |
| `whereNull(column)` | `` `column` IS NULL `` |
| `whereNullable(column, value)` | IS NULL or `= ?` depending on the value |

Multiple calls are joined with `AND`.

---

### SELECT

```java
try {
    // Single record
    Optional<MyObject> result = q.select("id", "name")
        .from("users")
        .where("id", userId)
        .queryAndMap(rs -> new MyObject(rs.getInt("id"), rs.getString("name")));

    // All records
    List<MyObject> all = q.select().all()
        .from("users")
        .whereExpr("`score` > ?", 100)
        .orderBy("name").desc()
        .limit(50).offset(0)
        .queryAndMapAll(rs -> /* ... */);
} catch (SQLException e) { }
```

| Method | Description |
|---|---|
| `all()` | Select all columns (`SELECT *`) |
| `column(name)` | Add a column by name |
| `expression(expr, params...)` | Add an arbitrary SQL expression |
| `from(table)` | Specify the table |
| `from(database, table)` | Specify the database and table |
| `orderBy(column)` | Sort by a column |
| `orderByExpr(expr, params...)` | Sort by an arbitrary expression |
| `desc()` | Append `DESC` to the sort |
| `limit(n)` | Limit the number of rows |
| `offset(n)` | Skip n rows |
| `forUpdate()` | Append `FOR UPDATE` |

---

### INSERT

```java
try {
    // Plain insert
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
        .onDuplicateKeyUpdateExcept("id")   // update all columns except the key
        .update();
} catch (SQLException e) { }
```

| Method | Description |
|---|---|
| `ignore()` | Add `INSERT IGNORE` |
| `value(column, value)` | Insert a value |
| `valueExpr(column, expr, params...)` | Insert an arbitrary SQL expression |
| `valueNull(column)` | Insert NULL |
| `valueNullable(column, value)` | Insert NULL or a value depending on the argument |
| `onDuplicateKeyUpdateExcept(keys...)` | `ON DUPLICATE KEY UPDATE` for all columns except the specified keys |

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

| Method | Description |
|---|---|
| `value(column, value)` | Set a value |
| `valueExpr(column, expr, params...)` | Set an arbitrary SQL expression |
| `valueNull(column)` | Set NULL |
| `valueNullable(column, value)` | NULL or a value depending on the argument |

---

### DELETE

```java
try {
    q.deleteFrom("users")
        .where("id", uuid)
        .update();
} catch (SQLException e) { }
```

Only WHERE conditions are available; no other methods.

---

### CREATE TABLE

The modifiers `notNull()`, `primaryKey()`, `autoIncrement()`, and `defaultValue()` apply to the **last added** column.

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

**Column methods:**

| Method | SQL type |
|---|---|
| `column(name, type)` | arbitrary type |
| `integer(name)` | `INT` |
| `bigint(name)` | `BIGINT` |
| `bool(name)` | `TINYINT(1)` |
| `varchar(name, size)` | `VARCHAR(size)` |
| `character(name, size)` | `CHAR(size)` |
| `text(name)` | `TEXT` |
| `serial(name)` | `SERIAL` (PostgreSQL only) |
| `bigSerial(name)` | `BIGSERIAL` (PostgreSQL only) |
| `intKey(name)` | `INT AUTO_INCREMENT PRIMARY KEY` |

**Column modifiers:**

| Method | Appends |
|---|---|
| `notNull()` | `NOT NULL` |
| `autoIncrement()` | `AUTO_INCREMENT` |
| `primaryKey()` | `PRIMARY KEY` |
| `defaultValue(value)` | `DEFAULT value` |

---

### Raw queries

```java
try {
    pool.query()
        .raw("UPDATE users SET score = score + ? WHERE id = ?", 10, uuid)
        .update();
} catch (SQLException e) { }
```

---

### Batch queries

For bulk execution of repeated queries via `pool.batch()`. All queries in a batch must share the same SQL text.

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

| Method | Description |
|---|---|
| `add(query)` | Add a query to the batch |
| `execute()` | Execute the batch; returns `int[]` with the affected row count per query |

---

## SchemaReader

Loads a table schema from a `.sql` file (e.g. from plugin resources):

```java
List<String> statements = SchemaReader.getStatements(
    getClass().getResourceAsStream("/schema.sql")
);

for (String sql : statements) {
    pool.query().raw(sql).update();
}
```

Comments (`--`, `#`) and blank lines are skipped automatically.

---

## Package structure

```
me.denarydev.crystal.database
├── DatabaseType                  — enum of supported database engines
├── connection
│   ├── ConnectionPool            — abstract connection pool (base class)
│   ├── ConnectionPoolBuilders    — factory methods for pool builders
│   ├── file
│   │   ├── FlatfileConnectionPool — base for file-based pools
│   │   ├── SQLiteConnectionPool
│   │   └── H2ConnectionPool
│   └── hikari
│       ├── HikariConnectionPool  — base for remote pools (HikariCP)
│       ├── MySqlConnectionPool
│       ├── MariaDBConnectionPool
│       └── PostgresConnectionPool
├── pool
│   ├── PoolManager               — singleton manager for named pools
│   └── impl.PoolManagerImpl      — internal implementation (config + lazy init)
├── query
│   ├── QueryBuilder              — entry point for building queries
│   ├── AbstractQuery             — base query class (execute/query/update)
│   ├── ConditionalQuery          — extension with WHERE support
│   ├── BatchBuilder              — batch query execution
│   ├── Dialect                   — SQL dialect enum
│   ├── Expression                — SQL expression with bound parameters
│   ├── impl                      — concrete implementations (Select, Insert, Update, Delete, CreateTable, Raw)
│   └── set
│       ├── ResultSetWrapper      — ResultSet wrapper with map/mapAll helpers
│       └── ResultSetMapper       — functional interface for row mapping
└── schema
    └── SchemaReader              — reads SQL schemas from files or streams
```
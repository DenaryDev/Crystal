/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.TestCrystal;
import me.denarydev.crystal.database.connection.ConnectionPoolBuilders;
import me.denarydev.crystal.database.query.impl.Insert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InsertQueryTest {

    static QueryBuilder q;      // DEFAULT dialect (MySQL)
    static QueryBuilder h2;     // H2 dialect
    static QueryBuilder sqlite; // SQLITE dialect
    static QueryBuilder pg;     // POSTGRES dialect

    @BeforeAll
    static void setup() {
        if (Crystal.instance() == null) new TestCrystal();

        q = ConnectionPoolBuilders.mysql()
            .address("localhost")
            .database("test")
            .username("user")
            .password("pass")
            .build()
            .query();
        h2 = ConnectionPoolBuilders.h2().file(Path.of("test.db")).build().query();
        sqlite = ConnectionPoolBuilders.sqlite().file(Path.of("test.db")).build().query();
        pg = ConnectionPoolBuilders.postgresql()
            .address("localhost")
            .database("test")
            .username("user")
            .password("pass")
            .build()
            .query();
    }

    // --- DEFAULT dialect (MySQL / MariaDB) ---

    @Test
    void insertBasic() {
        String sql = q.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .getSQL();
        assertEquals("INSERT INTO `users` (`id`, `name`) VALUES (?, ?);", sql);
    }

    @Test
    void insertIgnore() {
        String sql = q.insertInto("users")
            .ignore()
            .value("id", 1)
            .getSQL();
        assertEquals("INSERT IGNORE INTO `users` (`id`) VALUES (?);", sql);
    }

    @Test
    void insertValueNull() {
        String sql = q.insertInto("users")
            .valueNull("avatar")
            .getSQL();
        assertEquals("INSERT INTO `users` (`avatar`) VALUES (NULL);", sql);
    }

    @Test
    void insertValueNullable_null() {
        String sql = q.insertInto("users")
            .valueNullable("avatar", null)
            .getSQL();
        assertEquals("INSERT INTO `users` (`avatar`) VALUES (NULL);", sql);
    }

    @Test
    void insertValueNullable_nonNull() {
        String sql = q.insertInto("users")
            .valueNullable("name", "Bob")
            .getSQL();
        assertEquals("INSERT INTO `users` (`name`) VALUES (?);", sql);
    }

    @Test
    void insertValueExpr() {
        String sql = q.insertInto("logs")
            .valueExpr("created_at", "NOW()")
            .getSQL();
        assertEquals("INSERT INTO `logs` (`created_at`) VALUES (NOW());", sql);
    }

    @Test
    void insertOnDuplicateKeyUpdate() {
        String sql = q.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .value("score", 100)
            .onDuplicateKeyUpdateExcept("id")
            .getSQL();
        assertEquals("INSERT INTO `users` (`id`, `name`, `score`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `name` = ?, `score` = ?;", sql);
    }

    @Test
    void insertOnDuplicateKeyUpdateParams() {
        List<Object> params = q.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .value("score", 100)
            .onDuplicateKeyUpdateExcept("id")
            .getParams();
        assertEquals(List.of(1, "Alice", 100, "Alice", 100), params);
    }

    @Test
    void insertOnDuplicateKeyUpdateExpr() {
        Insert insert = q.insertInto("users")
            .value("id", 1)
            .value("score", 100)
            .onDuplicateKeyUpdateExpr("score", "`score` + ?", 5);
        assertEquals("INSERT INTO `users` (`id`, `score`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `score` = `score` + ?;", insert.getSQL());
        assertEquals(List.of(1, 100, 5), insert.getParams());
    }

    @Test
    void insertOnDuplicateKeyUpdateValue() {
        Insert insert = q.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .onDuplicateKeyUpdate("name", "Alicia");
        assertEquals("INSERT INTO `users` (`id`, `name`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `name` = ?;", insert.getSQL());
        assertEquals(List.of(1, "Alice", "Alicia"), insert.getParams());
    }

    @Test
    void insertOnDuplicateKeyUpdateExceptKeepsExplicitExpr_anyOrder() {
        String expected = "INSERT INTO `users` (`id`, `name`, `score`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `score` = `score` + ?, `name` = ?;";

        Insert exprFirst = q.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .value("score", 100)
            .onDuplicateKeyUpdateExpr("score", "`score` + ?", 5)
            .onDuplicateKeyUpdateExcept("id");
        assertEquals(expected, exprFirst.getSQL());
        assertEquals(List.of(1, "Alice", 100, 5, "Alice"), exprFirst.getParams());

        Insert exceptFirst = q.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .value("score", 100)
            .onDuplicateKeyUpdateExcept("id")
            .onDuplicateKeyUpdateExpr("score", "`score` + ?", 5);
        assertEquals("INSERT INTO `users` (`id`, `name`, `score`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `name` = ?, `score` = `score` + ?;", exceptFirst.getSQL());
        assertEquals(List.of(1, "Alice", 100, "Alice", 5), exceptFirst.getParams());
    }

    @Test
    void insertIgnoreWithDuplicateKeyThrows() {
        assertThrows(IllegalStateException.class, () ->
            q.insertInto("users")
                .ignore()
                .value("id", 1)
                .value("name", "Alice")
                .onDuplicateKeyUpdateExcept("id")
                .getSQL()
        );
    }

    @Test
    void insertNoValuesThrows() {
        assertThrows(IllegalStateException.class, () ->
            q.insertInto("users").getSQL()
        );
    }

    @Test
    void insertParams() {
        List<Object> params = q.insertInto("users")
            .value("id", 42)
            .value("name", "Carol")
            .getParams();
        assertEquals(List.of(42, "Carol"), params);
    }

    @Test
    void keysRejectsInvalidIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> q.insertInto("users").keys("id`"));
    }

    // --- SQLITE dialect ---

    @Test
    void sqliteBasicIsUnchanged() {
        String sql = sqlite.insertInto("users").value("id", 1).getSQL();
        assertEquals("INSERT INTO `users` (`id`) VALUES (?);", sql);
    }

    @Test
    void sqliteIgnore() {
        String sql = sqlite.insertInto("users").ignore().value("id", 1).getSQL();
        assertEquals("INSERT OR IGNORE INTO `users` (`id`) VALUES (?);", sql);
    }

    @Test
    void sqliteUpsertWithoutKeys() {
        String sql = sqlite.insertInto("users")
            .value("id", 1)
            .value("score", 100)
            .onDuplicateKeyUpdateExpr("score", "`score` + ?", 5)
            .getSQL();
        assertEquals("INSERT INTO `users` (`id`, `score`) VALUES (?, ?) ON CONFLICT DO UPDATE SET `score` = `score` + ?;", sql);
    }

    @Test
    void sqliteUpsertWithKeys() {
        String sql = sqlite.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .value("score", 100)
            .onDuplicateKeyUpdateExcept("id")
            .getSQL();
        assertEquals("INSERT INTO `users` (`id`, `name`, `score`) VALUES (?, ?, ?) ON CONFLICT (`id`) DO UPDATE SET `name` = ?, `score` = ?;", sql);
    }

    // --- POSTGRES dialect ---

    @Test
    void postgresIgnoreWithoutKeys() {
        String sql = pg.insertInto("users").ignore().value("id", 1).getSQL();
        assertEquals("INSERT INTO `users` (`id`) VALUES (?) ON CONFLICT DO NOTHING;", sql);
    }

    @Test
    void postgresIgnoreWithKeys() {
        String sql = pg.insertInto("users").ignore().keys("id").value("id", 1).getSQL();
        assertEquals("INSERT INTO `users` (`id`) VALUES (?) ON CONFLICT (`id`) DO NOTHING;", sql);
    }

    @Test
    void postgresUpsert() {
        String sql = pg.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .value("score", 100)
            .onDuplicateKeyUpdateExcept("id")
            .getSQL();
        assertEquals("INSERT INTO `users` (`id`, `name`, `score`) VALUES (?, ?, ?) ON CONFLICT (`id`) DO UPDATE SET `name` = ?, `score` = ?;", sql);
    }

    @Test
    void postgresUpsertCompositeKeys() {
        String sql = pg.insertInto("stats")
            .value("player", "p")
            .value("stat", "kills")
            .value("count", 1)
            .keys("player", "stat")
            .onDuplicateKeyUpdateExpr("count", "`count` + ?", 1)
            .getSQL();
        assertEquals("INSERT INTO `stats` (`player`, `stat`, `count`) VALUES (?, ?, ?) ON CONFLICT (`player`, `stat`) DO UPDATE SET `count` = `count` + ?;", sql);
    }

    @Test
    void postgresUpsertWithoutKeysThrows() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
            pg.insertInto("users")
                .value("id", 1)
                .onDuplicateKeyUpdate("name", "x")
                .getSQL()
        );
        assertTrue(e.getMessage().contains("keys"));
    }

    // --- H2 dialect ---

    @Test
    void h2BasicIsUnchanged() {
        String sql = h2.insertInto("users").value("id", 1).value("name", "Alice").getSQL();
        assertEquals("INSERT INTO `users` (`id`, `name`) VALUES (?, ?);", sql);
    }

    @Test
    void h2Ignore() {
        String sql = h2.insertInto("users").ignore().keys("id").value("id", 1).value("name", "Alice").getSQL();
        assertEquals("MERGE INTO `users` USING (VALUES (?, ?)) AS `src`(`c0`, `c1`) ON `users`.`id` = `src`.`c0`"
            + " WHEN NOT MATCHED THEN INSERT (`id`, `name`) VALUES (`src`.`c0`, `src`.`c1`);", sql);
    }

    @Test
    void h2Upsert() {
        Insert insert = h2.insertInto("users")
            .value("id", 1)
            .value("name", "Alice")
            .value("score", 100)
            .onDuplicateKeyUpdateExpr("score", "`score` + ?", 5)
            .onDuplicateKeyUpdateExcept("id");
        assertEquals("MERGE INTO `users` USING (VALUES (?, ?, ?)) AS `src`(`c0`, `c1`, `c2`) ON `users`.`id` = `src`.`c0`"
            + " WHEN MATCHED THEN UPDATE SET `score` = `score` + ?, `name` = ?"
            + " WHEN NOT MATCHED THEN INSERT (`id`, `name`, `score`) VALUES (`src`.`c0`, `src`.`c1`, `src`.`c2`);", insert.getSQL());
        assertEquals(List.of(1, "Alice", 100, 5, "Alice"), insert.getParams());
    }

    @Test
    void h2UpsertCompositeKeysAndExpressions() {
        String sql = h2.insertInto("stats")
            .value("player", "p")
            .value("stat", "kills")
            .valueExpr("updated", "NOW()")
            .value("count", 1)
            .keys("player", "stat")
            .onDuplicateKeyUpdateExpr("count", "`count` + ?", 1)
            .getSQL();
        assertEquals("MERGE INTO `stats` USING (VALUES (?, ?, NOW(), ?)) AS `src`(`c0`, `c1`, `c2`, `c3`)"
            + " ON `stats`.`player` = `src`.`c0` AND `stats`.`stat` = `src`.`c1`"
            + " WHEN MATCHED THEN UPDATE SET `count` = `count` + ?"
            + " WHEN NOT MATCHED THEN INSERT (`player`, `stat`, `updated`, `count`) VALUES (`src`.`c0`, `src`.`c1`, `src`.`c2`, `src`.`c3`);", sql);
    }

    @Test
    void h2IgnoreWithoutKeysThrows() {
        assertThrows(IllegalStateException.class, () -> h2.insertInto("users").ignore().value("id", 1).getSQL());
    }

    @Test
    void h2KeyNotInsertedThrows() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
            h2.insertInto("users").keys("id").value("name", "x").onDuplicateKeyUpdate("name", "y").getSQL()
        );
        assertTrue(e.getMessage().contains("`id`"));
    }
}

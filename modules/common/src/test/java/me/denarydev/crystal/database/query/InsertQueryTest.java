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
import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.connection.ConnectionPoolBuilders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsertQueryTest {

    static QueryBuilder q;

    @BeforeAll
    static void setup() {
        if (Crystal.instance() == null) new TestCrystal();
        ConnectionPool pool = ConnectionPoolBuilders.h2().file(Path.of("test.db")).build();
        q = pool.query();
    }

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
}

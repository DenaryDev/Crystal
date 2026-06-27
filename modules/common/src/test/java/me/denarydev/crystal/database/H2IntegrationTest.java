/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.TestCrystal;
import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.connection.ConnectionPoolBuilders;
import me.denarydev.crystal.database.query.QueryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class H2IntegrationTest {

    @TempDir
    Path tempDir;

    ConnectionPool pool;
    QueryBuilder q;

    @BeforeEach
    void setup() throws SQLException {
        if (Crystal.instance() == null) new TestCrystal();

        pool = ConnectionPoolBuilders.h2().file(tempDir.resolve("testdb")).build();
        pool.initialize();

        q = pool.query();

        q.createTable("users")
            .ifNotExists()
            .varchar("id", 36).notNull().primaryKey()
            .varchar("name", 64).notNull()
            .integer("score").defaultValue(0)
            .update();
    }

    @AfterEach
    void teardown() {
        pool.shutdown();
    }

    @Test
    void insertAndSelectOne() throws SQLException {
        q.insertInto("users").value("id", "u1").value("name", "Alice").update();

        Optional<String> name = q.select("name")
            .from("users")
            .where("id", "u1")
            .queryAndMap(rs -> rs.getString("name"));

        assertTrue(name.isPresent());
        assertEquals("Alice", name.get());
    }

    @Test
    void selectAll() throws SQLException {
        q.insertInto("users").value("id", "u1").value("name", "Alice").update();
        q.insertInto("users").value("id", "u2").value("name", "Bob").update();

        List<String> names = q.select("name")
            .from("users")
            .orderBy("name")
            .queryAndMapAll(rs -> rs.getString("name"));

        assertEquals(List.of("Alice", "Bob"), names);
    }

    @Test
    void selectEmpty() throws SQLException {
        Optional<String> result = q.select("name")
            .from("users")
            .where("id", "nonexistent")
            .queryAndMap(rs -> rs.getString("name"));

        assertFalse(result.isPresent());
    }

    @Test
    void updateRow() throws SQLException {
        q.insertInto("users").value("id", "u1").value("name", "Alice").update();

        q.update("users").value("name", "Alicia").where("id", "u1").update();

        Optional<String> name = q.select("name")
            .from("users")
            .where("id", "u1")
            .queryAndMap(rs -> rs.getString("name"));

        assertEquals("Alicia", name.get());
    }

    @Test
    void deleteRow() throws SQLException {
        q.insertInto("users").value("id", "u1").value("name", "Alice").update();
        q.deleteFrom("users").where("id", "u1").update();

        Optional<String> result = q.select("name").from("users").where("id", "u1")
            .queryAndMap(rs -> rs.getString("name"));

        assertFalse(result.isPresent());
    }

    @Test
    void updateReturnsAffectedRows() throws SQLException {
        q.insertInto("users").value("id", "u1").value("name", "Alice").update();
        q.insertInto("users").value("id", "u2").value("name", "Bob").update();

        int affected = q.update("users").valueExpr("score", "`score` + ?", 10).update();

        assertEquals(2, affected);
    }

    @Test
    void whereNullCondition() throws SQLException {
        // score has DEFAULT 0, so it's not null — but we can test a nullable column
        // by using raw insert with explicit NULL value
        q.insertInto("users").value("id", "u1").value("name", "Alice").valueExpr("score", "NULL").update();
        q.insertInto("users").value("id", "u2").value("name", "Bob").update();

        List<String> withNullScore = q.select("name").from("users")
            .whereNull("score")
            .queryAndMapAll(rs -> rs.getString("name"));

        assertEquals(List.of("Alice"), withNullScore);
    }

    @Test
    void limitAndOffset() throws SQLException {
        q.insertInto("users").value("id", "u1").value("name", "Alice").update();
        q.insertInto("users").value("id", "u2").value("name", "Bob").update();
        q.insertInto("users").value("id", "u3").value("name", "Carol").update();

        List<String> page = q.select("name").from("users")
            .orderBy("name")
            .limit(2).offset(1)
            .queryAndMapAll(rs -> rs.getString("name"));

        assertEquals(List.of("Bob", "Carol"), page);
    }

    @Test
    void batchInsert() throws SQLException {
        var batch = pool.batch();
        for (int i = 1; i <= 5; i++) {
            batch.add(q.insertInto("users").value("id", "u" + i).value("name", "User" + i));
        }

        int[] results = batch.execute();
        assertEquals(5, results.length);

        long count = q.select().expression("COUNT(*)")
            .from("users")
            .queryAndMap(rs -> rs.getLong(1))
            .orElse(0L);

        assertEquals(5L, count);
    }

    @Test
    void insertWithKeysAndMap() throws SQLException {
        // H2 supports generated keys for auto-increment — use a separate table with INT key
        q.createTable("items")
            .ifNotExists()
            .intKey("id")
            .varchar("label", 64).notNull()
            .update();

        Optional<Integer> key = q.insertInto("items").value("label", "sword")
            .updateWithKeysAndMap(rs -> rs.getInt(1));

        assertTrue(key.isPresent());
        assertEquals(1, key.get());
    }
}

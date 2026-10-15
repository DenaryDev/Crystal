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
import me.denarydev.crystal.database.query.impl.Insert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLiteIntegrationTest {

    @TempDir
    Path tempDir;

    ConnectionPool pool;
    QueryBuilder q;

    @BeforeEach
    void setup() throws SQLException {
        if (Crystal.instance() == null) new TestCrystal();

        pool = ConnectionPoolBuilders.sqlite().file(tempDir.resolve("test.db")).build();
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
    void insertIgnoreSkipsExistingRow() throws SQLException {
        q.insertInto("users").value("id", "u1").value("name", "Alice").update();

        int affected = q.insertInto("users").ignore().value("id", "u1").value("name", "Mallory").update();
        assertEquals(0, affected);

        List<String> names = q.select("name").from("users").queryAndMapAll(rs -> rs.getString("name"));
        assertEquals(List.of("Alice"), names);
    }

    @Test
    void upsertInsertsThenUpdates() throws SQLException {
        Insert upsert = q.insertInto("users")
            .value("id", "u1")
            .value("name", "Alice")
            .value("score", 10)
            .onDuplicateKeyUpdateExpr("score", "`score` + ?", 5)
            .onDuplicateKeyUpdateExcept("id");

        upsert.update();
        upsert.update();

        int score = q.select("score").from("users").where("id", "u1").queryAndMap(rs -> rs.getInt("score")).orElseThrow();
        assertEquals(15, score);
    }

    @Test
    void upsertWithoutKeys() throws SQLException {
        Insert upsert = q.insertInto("users")
            .value("id", "u1")
            .value("name", "Alice")
            .onDuplicateKeyUpdate("name", "Alicia");

        upsert.update();
        upsert.update();

        String name = q.select("name").from("users").where("id", "u1").queryAndMap(rs -> rs.getString("name")).orElseThrow();
        assertEquals("Alicia", name);
    }

    @Test
    void whereInSelectsMatchingRows() throws SQLException {
        q.insertInto("users").value("id", "u1").value("name", "Alice").update();
        q.insertInto("users").value("id", "u2").value("name", "Bob").update();

        List<String> names = q.select("name").from("users").whereIn("id", "u2", "u9").queryAndMapAll(rs -> rs.getString("name"));
        assertEquals(List.of("Bob"), names);
    }
}

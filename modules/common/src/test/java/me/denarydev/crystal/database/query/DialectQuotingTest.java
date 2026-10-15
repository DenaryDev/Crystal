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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DialectQuotingTest {

    static QueryBuilder pg;
    static QueryBuilder sqlite;
    static QueryBuilder h2;

    @BeforeAll
    static void setup() {
        if (Crystal.instance() == null) new TestCrystal();

        pg = ConnectionPoolBuilders.postgresql()
            .address("localhost")
            .database("test")
            .username("user")
            .password("pass")
            .build()
            .query();
        sqlite = ConnectionPoolBuilders.sqlite().file(Path.of("test.db")).build().query();
        h2 = ConnectionPoolBuilders.h2().file(Path.of("test.db")).build().query();
    }

    @Test
    void quoteCharPerDialect() {
        assertEquals('`', Dialect.DEFAULT.quoteChar());
        assertEquals('`', Dialect.H2.quoteChar());
        assertEquals('`', Dialect.SQLITE.quoteChar());
        assertEquals('"', Dialect.POSTGRES.quoteChar());
        assertEquals("`users`", Dialect.DEFAULT.quote("users"));
        assertEquals("\"users\"", Dialect.POSTGRES.quote("users"));
    }

    @Test
    void queryExposesDialect() {
        assertEquals(Dialect.POSTGRES, pg.select().from("users").dialect());
        assertEquals(Dialect.SQLITE, sqlite.select().from("users").dialect());
        assertEquals(Dialect.H2, h2.select().from("users").dialect());
    }

    @Test
    void postgresSelect() {
        String sql = pg.select("id", "name")
            .from("app", "users")
            .where("active", true)
            .whereNull("deleted_at")
            .whereIn("role", List.of("admin", "mod"))
            .orderBy("name")
            .getSQL();
        assertEquals("SELECT \"id\", \"name\" FROM \"app\".\"users\" WHERE (\"active\" = ?) AND (\"deleted_at\" IS NULL) AND (\"role\" IN (?, ?)) ORDER BY (\"name\") ASC ;", sql);
    }

    @Test
    void postgresUpdate() {
        String sql = pg.update("users").value("name", "x").where("id", 1).getSQL();
        assertEquals("UPDATE \"users\" SET \"name\" = ? WHERE (\"id\" = ?) ;", sql);
    }

    @Test
    void postgresDelete() {
        String sql = pg.deleteFrom("users").where("id", 1).getSQL();
        assertEquals("DELETE FROM \"users\" WHERE (\"id\" = ?) ;", sql);
    }

    @Test
    void postgresInsert() {
        String sql = pg.insertInto("users").value("id", 1).value("name", "x").getSQL();
        assertEquals("INSERT INTO \"users\" (\"id\", \"name\") VALUES (?, ?);", sql);
    }

    @Test
    void sqliteAndH2KeepBackticks() {
        assertEquals("DELETE FROM `users` WHERE (`id` = ?) ;", sqlite.deleteFrom("users").where("id", 1).getSQL());
        assertEquals("DELETE FROM `users` WHERE (`id` = ?) ;", h2.deleteFrom("users").where("id", 1).getSQL());
    }
}

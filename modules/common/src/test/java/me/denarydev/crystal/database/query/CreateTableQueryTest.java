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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateTableQueryTest {

    static QueryBuilder h2;    // DEFAULT dialect
    static QueryBuilder sqlite; // SQLITE dialect
    static QueryBuilder pg;    // POSTGRES dialect

    @BeforeAll
    static void setup() {
        if (Crystal.instance() == null) new TestCrystal();

        ConnectionPool h2Pool = ConnectionPoolBuilders.h2().file(Path.of("test.db")).build();
        ConnectionPool sqlitePool = ConnectionPoolBuilders.sqlite().file(Path.of("test.db")).build();
        ConnectionPool pgPool = ConnectionPoolBuilders.postgresql()
            .address("localhost")
            .database("test")
            .username("user")
            .password("pass")
            .build();

        h2 = h2Pool.query();
        sqlite = sqlitePool.query();
        pg = pgPool.query();
    }

    // --- DEFAULT dialect (H2) ---

    @Test
    void createTableSimple() {
        String sql = h2.createTable("users").integer("id").getSQL();
        assertEquals("CREATE TABLE `users` (`id` INT);", sql);
    }

    @Test
    void createTableIfNotExists() {
        String sql = h2.createTable("users").ifNotExists().integer("id").getSQL();
        assertEquals("CREATE TABLE IF NOT EXISTS `users` (`id` INT);", sql);
    }

    @Test
    void createTableNotNull() {
        String sql = h2.createTable("users").integer("id").notNull().getSQL();
        assertEquals("CREATE TABLE `users` (`id` INT NOT NULL);", sql);
    }

    @Test
    void createTableAutoIncrementPrimaryKey() {
        String sql = h2.createTable("users").integer("id").autoIncrement().primaryKey().getSQL();
        assertEquals("CREATE TABLE `users` (`id` INT AUTO_INCREMENT, PRIMARY KEY (`id`));", sql);
    }

    @Test
    void createTableIntKey() {
        String sql = h2.createTable("users")
            .intKey("id")
            .varchar("name", 64)
            .getSQL();
        assertEquals("CREATE TABLE `users` (`id` INT AUTO_INCREMENT, `name` VARCHAR(64), PRIMARY KEY (`id`));", sql);
    }

    @Test
    void createTableDefaultValueBool() {
        String sql = h2.createTable("t").bool("active").defaultValue(true).getSQL();
        assertEquals("CREATE TABLE `t` (`active` TINYINT(1) DEFAULT 1);", sql);
    }

    @Test
    void createTableDefaultValueInt() {
        String sql = h2.createTable("t").integer("score").defaultValue(0).getSQL();
        assertEquals("CREATE TABLE `t` (`score` INT DEFAULT 0);", sql);
    }

    @Test
    void createTableVariousColumnTypes() {
        String sql = h2.createTable("t")
            .bigint("big")
            .text("bio")
            .character("code", 3)
            .getSQL();
        assertEquals("CREATE TABLE `t` (`big` BIGINT, `bio` TEXT, `code` CHAR(3));", sql);
    }

    @Test
    void createTableDuplicateColumnThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            h2.createTable("t").integer("id").integer("id")
        );
    }

    @Test
    void createTableModifierWithNoColumnsThrows() {
        assertThrows(IllegalStateException.class, () ->
            h2.createTable("t").notNull()
        );
    }

    // --- SQLITE dialect ---

    @Test
    void createTable_sqlite_intKey() {
        String sql = sqlite.createTable("users")
            .intKey("id")
            .varchar("name", 64)
            .getSQL();
        // SQLite: autoIncrement+primaryKey → INTEGER PRIMARY KEY inline, no separate PRIMARY KEY clause
        assertEquals("CREATE TABLE `users` (`id` INTEGER PRIMARY KEY, `name` VARCHAR(64));", sql);
    }

    @Test
    void createTable_sqlite_notNullIgnoredOnAutoIncrement() {
        // NOT NULL is omitted for autoIncrement columns on SQLite
        String sql = sqlite.createTable("t")
            .integer("id").notNull().autoIncrement().primaryKey()
            .getSQL();
        assertEquals("CREATE TABLE `t` (`id` INTEGER PRIMARY KEY);", sql);
    }

    // --- POSTGRES dialect ---

    @Test
    void createTable_postgres_intKey() {
        // INT + autoIncrement → SERIAL, PRIMARY KEY inline
        String sql = pg.createTable("users")
            .intKey("id")
            .varchar("name", 64)
            .getSQL();
        assertEquals("CREATE TABLE `users` (`id` SERIAL PRIMARY KEY, `name` VARCHAR(64));", sql);
    }

    @Test
    void createTable_postgres_bigintKey() {
        // BIGINT + autoIncrement → BIGSERIAL, PRIMARY KEY inline
        String sql = pg.createTable("t")
            .bigint("id").autoIncrement().primaryKey()
            .getSQL();
        assertEquals("CREATE TABLE `t` (`id` BIGSERIAL PRIMARY KEY);", sql);
    }
}

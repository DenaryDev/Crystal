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

class SelectQueryTest {

    static QueryBuilder q;

    @BeforeAll
    static void setup() {
        if (Crystal.instance() == null) new TestCrystal();
        ConnectionPool pool = ConnectionPoolBuilders.h2().file(Path.of("test.db")).build();
        q = pool.query();
    }

    @Test
    void selectAllColumns() {
        String sql = q.select().all().from("users").getSQL();
        assertEquals("SELECT * FROM `users` ;", sql);
    }

    @Test
    void selectNamedColumns() {
        String sql = q.select("id", "name").from("users").getSQL();
        assertEquals("SELECT `id`, `name` FROM `users` ;", sql);
    }

    @Test
    void selectExpression() {
        String sql = q.select().expression("COUNT(*)").from("users").getSQL();
        assertEquals("SELECT COUNT(*) FROM `users` ;", sql);
    }

    @Test
    void selectFromDatabase() {
        String sql = q.select("id").from("mydb", "users").getSQL();
        assertEquals("SELECT `id` FROM `mydb`.`users` ;", sql);
    }

    @Test
    void selectWithWhere() {
        String sql = q.select("id").from("users").where("name", "Alice").getSQL();
        assertEquals("SELECT `id` FROM `users` WHERE (`name` = ?) ;", sql);
    }

    @Test
    void selectWithWhereNull() {
        String sql = q.select("id").from("users").whereNull("avatar").getSQL();
        assertEquals("SELECT `id` FROM `users` WHERE (`avatar` IS NULL) ;", sql);
    }

    @Test
    void selectWithWhereExpr() {
        String sql = q.select("id").from("users").whereExpr("`score` > ?", 100).getSQL();
        assertEquals("SELECT `id` FROM `users` WHERE (`score` > ?) ;", sql);
    }

    @Test
    void selectWithMultipleWhere() {
        String sql = q.select("id").from("users")
            .where("active", 1)
            .whereExpr("`score` >= ?", 50)
            .getSQL();
        assertEquals("SELECT `id` FROM `users` WHERE (`active` = ?) AND (`score` >= ?) ;", sql);
    }

    @Test
    void selectWithWhereNullable_null() {
        String sql = q.select("id").from("users").whereNullable("avatar", null).getSQL();
        assertEquals("SELECT `id` FROM `users` WHERE (`avatar` IS NULL) ;", sql);
    }

    @Test
    void selectWithWhereNullable_value() {
        String sql = q.select("id").from("users").whereNullable("name", "Bob").getSQL();
        assertEquals("SELECT `id` FROM `users` WHERE (`name` = ?) ;", sql);
    }

    @Test
    void selectWithOrderBy() {
        String sql = q.select("id").from("users").orderBy("name").getSQL();
        assertEquals("SELECT `id` FROM `users` ORDER BY (`name`) ASC ;", sql);
    }

    @Test
    void selectWithOrderByDesc() {
        String sql = q.select("id").from("users").orderBy("score").desc().getSQL();
        assertEquals("SELECT `id` FROM `users` ORDER BY (`score`) DESC ;", sql);
    }

    @Test
    void selectWithLimit() {
        String sql = q.select("id").from("users").limit(10).getSQL();
        assertEquals("SELECT `id` FROM `users` LIMIT 10 ;", sql);
    }

    @Test
    void selectWithLimitAndOffset() {
        String sql = q.select("id").from("users").limit(10).offset(20).getSQL();
        assertEquals("SELECT `id` FROM `users` LIMIT 10 OFFSET 20 ;", sql);
    }

    @Test
    void selectForUpdate() {
        String sql = q.select("id").from("users").forUpdate().getSQL();
        assertEquals("SELECT `id` FROM `users` FOR UPDATE;", sql);
    }

    @Test
    void selectParams() {
        List<Object> params = q.select("id").from("users")
            .where("name", "Alice")
            .whereExpr("`score` > ?", 42)
            .getParams();
        assertEquals(List.of("Alice", 42), params);
    }

    @Test
    void selectAllThrowsWhenAddingColumn() {
        assertThrows(IllegalStateException.class, () ->
            q.select().all().column("id")
        );
    }

    @Test
    void selectEmptyThrowsOnGetSQL() {
        assertThrows(IllegalStateException.class, () ->
            q.select().from("users").getSQL()
        );
    }

    @Test
    void descWithoutOrderByThrows() {
        assertThrows(IllegalStateException.class, () ->
            q.select("id").from("users").desc()
        );
    }

    @Test
    void limitZeroThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            q.select("id").from("users").limit(0)
        );
    }

    @Test
    void negativeOffsetThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            q.select("id").from("users").limit(1).offset(-1)
        );
    }
}

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

class DeleteQueryTest {

    static QueryBuilder q;

    @BeforeAll
    static void setup() {
        if (Crystal.instance() == null) new TestCrystal();
        ConnectionPool pool = ConnectionPoolBuilders.h2().file(Path.of("test.db")).build();
        q = pool.query();
    }

    @Test
    void deleteNoWhere() {
        String sql = q.deleteFrom("users").getSQL();
        assertEquals("DELETE FROM `users` ;", sql);
    }

    @Test
    void deleteWithWhere() {
        String sql = q.deleteFrom("users").where("id", 1).getSQL();
        assertEquals("DELETE FROM `users` WHERE (`id` = ?) ;", sql);
    }

    @Test
    void deleteWithWhereNull() {
        String sql = q.deleteFrom("users").whereNull("avatar").getSQL();
        assertEquals("DELETE FROM `users` WHERE (`avatar` IS NULL) ;", sql);
    }

    @Test
    void deleteWithMultipleConditions() {
        String sql = q.deleteFrom("users")
            .where("active", 0)
            .whereExpr("`score` < ?", 10)
            .getSQL();
        assertEquals("DELETE FROM `users` WHERE (`active` = ?) AND (`score` < ?) ;", sql);
    }

    @Test
    void deleteParams() {
        List<Object> params = q.deleteFrom("users")
            .where("id", 42)
            .getParams();
        assertEquals(List.of(42), params);
    }
}

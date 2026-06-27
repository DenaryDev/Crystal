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

class UpdateQueryTest {

    static QueryBuilder q;

    @BeforeAll
    static void setup() {
        if (Crystal.instance() == null) new TestCrystal();
        ConnectionPool pool = ConnectionPoolBuilders.h2().file(Path.of("test.db")).build();
        q = pool.query();
    }

    @Test
    void updateBasic() {
        String sql = q.update("users").value("name", "Alice").getSQL();
        assertEquals("UPDATE `users` SET `name` = ? ;", sql);
    }

    @Test
    void updateWithWhere() {
        String sql = q.update("users")
            .value("name", "Alice")
            .where("id", 1)
            .getSQL();
        assertEquals("UPDATE `users` SET `name` = ? WHERE (`id` = ?) ;", sql);
    }

    @Test
    void updateValueExpr() {
        String sql = q.update("users")
            .valueExpr("score", "`score` + ?", 10)
            .getSQL();
        assertEquals("UPDATE `users` SET `score` = `score` + ? ;", sql);
    }

    @Test
    void updateValueNull() {
        String sql = q.update("users").valueNull("avatar").getSQL();
        assertEquals("UPDATE `users` SET `avatar` = NULL ;", sql);
    }

    @Test
    void updateValueNullable_null() {
        String sql = q.update("users").valueNullable("avatar", null).getSQL();
        assertEquals("UPDATE `users` SET `avatar` = NULL ;", sql);
    }

    @Test
    void updateValueNullable_nonNull() {
        String sql = q.update("users").valueNullable("name", "Bob").getSQL();
        assertEquals("UPDATE `users` SET `name` = ? ;", sql);
    }

    @Test
    void updateMultipleValues() {
        String sql = q.update("users")
            .value("name", "Alice")
            .valueNull("avatar")
            .valueExpr("score", "`score` + ?", 5)
            .where("id", 99)
            .getSQL();
        assertEquals("UPDATE `users` SET `name` = ?, `avatar` = NULL, `score` = `score` + ? WHERE (`id` = ?) ;", sql);
    }

    @Test
    void updateParams() {
        List<Object> params = q.update("users")
            .value("name", "Alice")
            .valueExpr("score", "`score` + ?", 10)
            .where("id", 7)
            .getParams();
        assertEquals(List.of("Alice", 10, 7), params);
    }

    @Test
    void updateNoValuesThrows() {
        assertThrows(IllegalStateException.class, () ->
            q.update("users").where("id", 1).getSQL()
        );
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query.impl;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.query.AbstractQuery;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Необработанный (raw) запрос с произвольным SQL-кодом.
 *
 * @author DenaryDev
 * @since 2:17 17.01.2026
 */
public final class Raw extends AbstractQuery {

    private final String sql;
    private final List<Object> params;

    @ApiStatus.Internal
    public Raw(ConnectionPool pool, String sql, List<Object> params) {
        super(pool);
        this.sql = sql;
        this.params = params;
    }

    @Override
    public String getSQL() {
        return this.sql;
    }

    @Override
    public List<Object> getParams() {
        return this.params;
    }
}

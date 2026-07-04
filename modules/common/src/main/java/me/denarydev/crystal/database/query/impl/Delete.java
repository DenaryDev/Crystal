/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query.impl;

import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.query.ConditionalQuery;
import me.denarydev.crystal.database.query.Expression;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>DELETE</code> query.
 */
public final class Delete extends ConditionalQuery<Delete> {

    private final String table;

    @ApiStatus.Internal
    public Delete(ConnectionPool pool, String table) {
        super(pool);
        this.table = table;
    }

    @Override
    public String getSQL() {
        final StringBuilder builder = new StringBuilder();

        builder.append("DELETE FROM ");

        builder.append("`").append(this.table).append("` ");

        appendConditions(builder);

        builder.append(";");

        return builder.toString();
    }

    @Override
    public List<Object> getParams() {
        final List<Object> params = new ArrayList<>();

        for (final Expression condition : this.conditions) {
            Collections.addAll(params, condition.params());
        }

        return params;
    }
}

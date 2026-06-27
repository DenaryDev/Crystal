/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.file;

import me.denarydev.crystal.database.DatabaseType;
import org.h2.jdbcx.JdbcDataSource;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * Пул соединений для встроенной базы данных H2.
 */
public final class H2ConnectionPool extends FlatfileConnectionPool {

    H2ConnectionPool(Path file) {
        super(file);
    }

    @Override
    public @NonNull DatabaseType implementationType() {
        return DatabaseType.H2;
    }

    @Override
    public void initialize() {
        final JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:" + file.toAbsolutePath());

        this.dataSource = source;
        this.initialized = true;
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`')
            .replace("LIKE", "ILIKE")
            .replace("value", "`value`")
            .replace("``value``", "`value`");
    }

    public static final class Builder extends FlatfileConnectionPool.Builder<H2ConnectionPool> {
        @Override
        public H2ConnectionPool build() {
            return new H2ConnectionPool(file);
        }
    }
}

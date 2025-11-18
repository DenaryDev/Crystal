/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.file;

import me.denarydev.crystal.database.DatabaseType;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteDataSource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * @author DenaryDev
 * @since 16:46 23.11.2023
 */
public final class SQLiteConnectionPool extends FlatfileConnectionPool {

    SQLiteConnectionPool(Path file) {
        super(file);
    }

    @Override
    public @NotNull DatabaseType implementationType() {
        return DatabaseType.SQLITE;
    }

    @Override
    public void initialize() {
        final SQLiteDataSource source = new SQLiteDataSource();
        source.setUrl("jdbc:sqlite:" + file.toAbsolutePath());
        source.setEncoding(StandardCharsets.UTF_8.name());

        this.dataSource = source;
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`');
    }

    public static final class Builder extends FlatfileConnectionPool.Builder<SQLiteConnectionPool> {
        @Override
        public SQLiteConnectionPool build() {
            return new SQLiteConnectionPool(file);
        }
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.file;

import me.denarydev.crystal.database.DatabaseType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Function;

/**
 * @author DenaryDev
 * @since 16:46 23.11.2023
 */
@ApiStatus.AvailableSince("2.1.0")
public final class SQLiteConnectionFactory extends FlatfileConnectionFactory {
    private Constructor<?> connectionConstructor;

    SQLiteConnectionFactory(Logger logger, Path file) {
        super(logger, file);
    }

    @Override
    public @NotNull DatabaseType implementationType() {
        return DatabaseType.SQLITE;
    }

    @Override
    public void initialize() {
        try {
            final var clazz = Class.forName("org.sqlite.jdbc4.JDBC4Connection");
            this.connectionConstructor = clazz.getConstructor(String.class, String.class, Properties.class);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Connection createConnection(Path file) throws SQLException {
        try {
            return (Connection) this.connectionConstructor.newInstance("jdbc:sqlite:" + file.toAbsolutePath(), file.toAbsolutePath().toString(), new Properties());
        } catch (ReflectiveOperationException e) {
            if (e.getCause() instanceof SQLException cause) {
                throw cause;
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`');
    }

    public static final class Builder extends FlatfileConnectionFactory.Builder<SQLiteConnectionFactory> {
        @Override
        public SQLiteConnectionFactory build() {
            return new SQLiteConnectionFactory(logger, file);
        }
    }
}

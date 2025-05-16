/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.connection.file;

import me.denarydev.crystal.db.DatabaseType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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
@ApiStatus.Internal
@ApiStatus.AvailableSince("2.1.0")
public final class SQLiteConnectionFactory extends FlatfileConnectionFactory {
    private Constructor<?> connectionConstructor;

    public SQLiteConnectionFactory(Path file) {
        super(file);
    }

    @Override
    public @NotNull DatabaseType implementationType() {
        return DatabaseType.SQLITE;
    }

    @Override
    public void initialize() {
        try {
            Class<?> clazz = Class.forName("org.sqlite.jdbc4.JDBC4Connection");
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
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`');
    }
}

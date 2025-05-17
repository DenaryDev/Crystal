/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.common.database.connection.file;

import me.denarydev.crystal.common.database.DatabaseType;
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
public final class H2ConnectionFactory extends FlatfileConnectionFactory {
    private Constructor<?> connectionConstructor;

    H2ConnectionFactory(Logger logger, Path file) {
        super(logger, file);
    }

    @Override
    public @NotNull DatabaseType implementationType() {
        return DatabaseType.H2;
    }

    @Override
    public void initialize() {
        try {
            final var clazz = Class.forName("org.h2.jdbc.JdbcConnection");
            this.connectionConstructor = clazz.getConstructor(String.class, Properties.class, String.class, Object.class, boolean.class);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Connection createConnection(Path file) throws SQLException {
        try {
            return (Connection) this.connectionConstructor.newInstance("jdbc:h2:" + file.toAbsolutePath(), new Properties(), null, null, false);
        } catch (ReflectiveOperationException e) {
            if (e.getCause() instanceof SQLException cause) {
                throw cause;
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`')
            .replace("LIKE", "ILIKE")
            .replace("value", "`value`")
            .replace("``value``", "`value`");
    }

    public static final class Builder extends FlatfileConnectionFactory.Builder<H2ConnectionFactory> {
        @Override
        public H2ConnectionFactory build() {
            return new H2ConnectionFactory(logger, file);
        }
    }
}

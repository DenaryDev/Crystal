/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.file;

import me.denarydev.crystal.database.connection.ConnectionFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author DenaryDev
 * @since 16:40 23.11.2023
 */
@ApiStatus.Internal
@ApiStatus.AvailableSince("2.1.0")
public sealed abstract class FlatfileConnectionFactory extends ConnectionFactory permits H2ConnectionFactory, SQLiteConnectionFactory {
    private final Path file;
    private NonClosableConnection connection;

    FlatfileConnectionFactory(Logger logger, Path file) {
        super(logger);
        this.file = file;
    }

    protected abstract Connection createConnection(Path file) throws SQLException;

    @Override
    public @NotNull Connection connection() throws SQLException {
        NonClosableConnection connection = this.connection;
        if (connection == null || connection.isClosed()) {
            connection = new NonClosableConnection(createConnection(this.file));
            this.connection = connection;
        }
        return connection;
    }

    @Override
    public void shutdown() throws SQLException {
        if (this.connection != null) {
            this.connection.shutdown();
        }
    }

    @Deprecated
    @Override
    public void connect(@NotNull ConnectionCallback callback) {
        try {
            callback.accept(connection());
        } catch (Exception ex) {
            logger.error("An error occurred executing an {} query", implementationType().name().toLowerCase(), ex);
        }
    }

    public static sealed abstract class Builder<T extends FlatfileConnectionFactory> extends ConnectionFactory.Builder<T> permits H2ConnectionFactory.Builder, SQLiteConnectionFactory.Builder {
        protected Path file;

        /**
         * Файл, в котором будет храниться база данных.
         * <p>
         * <i>Должен иметь расширение .db, например: storage.db</i>
         *
         * @param file файл хранения базы данных
         */
        public Builder<T> file(@NotNull Path file) {
            this.file = file;

            return this;
        }
    }
}

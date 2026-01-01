/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.file;

import me.denarydev.crystal.database.connection.ConnectionPool;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * @author DenaryDev
 * @since 16:40 23.11.2023
 */
public sealed abstract class FlatfileConnectionPool extends ConnectionPool permits H2ConnectionPool, SQLiteConnectionPool {

    protected final Path file;
    protected DataSource dataSource;

    FlatfileConnectionPool(Path file) {
        this.file = file;
    }

    @Override
    public final @NotNull DataSource dataSource() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }

        return this.dataSource;
    }

    @Override
    public void shutdown() {
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

    public static sealed abstract class Builder<T extends FlatfileConnectionPool> extends ConnectionPool.Builder<T> permits H2ConnectionPool.Builder, SQLiteConnectionPool.Builder {
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

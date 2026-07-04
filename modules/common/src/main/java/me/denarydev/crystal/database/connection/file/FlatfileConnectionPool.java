/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.connection.file;

import me.denarydev.crystal.database.connection.ConnectionPool;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * Base class for file-based connection pools (SQLite, H2)
 * that store data locally in a file on disk.
 */
public sealed abstract class FlatfileConnectionPool extends ConnectionPool permits H2ConnectionPool, SQLiteConnectionPool {

    protected final Path file;
    protected DataSource dataSource;
    protected volatile boolean initialized;

    FlatfileConnectionPool(Path file) {
        this.file = file;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public final @NonNull DataSource dataSource() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }

        return this.dataSource;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void connect(@NonNull ConnectionCallback callback) {
        try {
            callback.accept(connection());
        } catch (Exception ex) {
            logger.error("An error occurred executing an {} query", implementationType().name().toLowerCase(), ex);
        }
    }

    public static sealed abstract class Builder<T extends FlatfileConnectionPool> extends ConnectionPool.Builder<T> permits H2ConnectionPool.Builder, SQLiteConnectionPool.Builder {
        protected Path file;

        /**
         * The file in which the database will be stored.
         * <p>
         * <i>Must have a .db extension, for example: storage.db</i>
         *
         * @param file the database storage file
         */
        public Builder<T> file(@NonNull Path file) {
            this.file = file;

            return this;
        }
    }
}

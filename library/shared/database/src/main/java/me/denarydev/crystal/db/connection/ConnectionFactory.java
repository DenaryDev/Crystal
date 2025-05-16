/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.connection;

import me.denarydev.crystal.db.DatabaseType;
import me.denarydev.crystal.db.connection.file.FlatfileConnectionFactory;
import me.denarydev.crystal.db.connection.hikari.HikariConnectionFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

@ApiStatus.AvailableSince("2.1.0")
public sealed interface ConnectionFactory permits FlatfileConnectionFactory, HikariConnectionFactory {

    /**
     * Returns selected database type.
     *
     * @return database type
     */
    @NotNull
    DatabaseType implementationType();

    void initialize();

    /**
     * Shutdown the database
     */
    void shutdown() throws SQLException;

    /**
     * @return {@link Connection}
     * @throws SQLException when the connection could not be received
     */
    @NotNull
    Connection connection() throws SQLException;

    /**
     * Executes a callback with a Connection passed and automatically closes it when finished
     *
     * @param callback The callback to execute once the connection is retrieved
     */
    void connect(@NotNull final ConnectionCallback callback);

    Function<String, String> statementProcessor();

    /**
     * Wraps a connection in a callback which will automagically handle catching sql errors
     */
    interface ConnectionCallback {
        void accept(@NotNull final Connection connection) throws SQLException;
    }
}

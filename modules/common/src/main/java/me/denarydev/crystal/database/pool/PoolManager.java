/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.pool;

import me.denarydev.crystal.database.connection.ConnectionPool;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Manager for database connection pools.
 * Provides access to pools registered in the Crystal configuration.
 */
public abstract class PoolManager {

    private static PoolManager impl;

    /**
     * Returns the current pool manager instance.
     *
     * @return the current {@link PoolManager} instance
     * @throws IllegalStateException if the manager has not been initialized yet
     */
    public static PoolManager get() {
        if (impl == null) {
            throw new IllegalStateException("PoolManager has not been initialized");
        }

        return impl;
    }

    /**
     * Attempts to retrieve a connection pool by name and returns it wrapped in an
     * {@link Optional} if found in the configuration.
     * <p>
     * Returns {@link Optional#empty()} otherwise.
     *
     * @param poolName the name of the pool.
     * @return an {@link Optional} containing the {@link ConnectionPool} if found, otherwise an empty {@link Optional}.
     */
    public abstract Optional<ConnectionPool> getPool(@NonNull String poolName);

    /**
     * Attempts to retrieve a connection pool by name and returns it if found.
     * <p>
     * Throws {@link IllegalStateException} if the pool is not found.
     *
     * @param poolName the name of the pool.
     * @return the database connection pool.
     * @throws IllegalStateException if the pool is not found.
     */
    public abstract ConnectionPool requirePool(@NonNull String poolName);

    @ApiStatus.Internal
    protected static void setImpl(PoolManager impl) {
        PoolManager.impl = impl;
    }
}

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

public abstract class PoolManager {

    private static PoolManager impl;

    public static PoolManager get() {
        if (impl == null) {
            throw new IllegalStateException("PoolManager has not been initialized");
        }

        return impl;
    }

    /**
     * Пытается получить пул соединений с БД, и возвращает его,
     * обёрнутый в {@link Optional}, если он найден в конфиге.
     * <p>
     * В ином случае возвращает {@link Optional#empty()}.
     *
     * @param poolName название пула.
     * @return {@link Optional} с объектом {@link DataSource}, если найден, иначе пустой {@link Optional}.
     */
    public abstract Optional<ConnectionPool> getPool(@NonNull String poolName);

    /**
     * Пытается получить пул соединений с БД и возвращает его,
     * если удалось соединиться.
     * <p>
     * В ином случае выкидывает {@link IllegalStateException}.
     *
     * @param poolName название пула.
     * @return пул соединений с бд.
     * @throws IllegalStateException если пул не найден.
     */
    public abstract ConnectionPool requirePool(@NonNull String poolName);

    @ApiStatus.Internal
    protected static void setImpl(PoolManager impl) {
        PoolManager.impl = impl;
    }
}

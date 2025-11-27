/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.pool.impl;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.config.ConfigLibLoader;
import me.denarydev.crystal.config.internal.PoolsConfiguration;
import me.denarydev.crystal.database.connection.ConnectionPool;
import me.denarydev.crystal.database.connection.ConnectionPoolBuilders;
import me.denarydev.crystal.database.connection.file.FlatfileConnectionPool;
import me.denarydev.crystal.database.connection.hikari.HikariConnectionPool;
import me.denarydev.crystal.database.pool.PoolManager;
import me.denarydev.crystal.database.util.LazyValue;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author DenaryDev
 * @since 19:34 27.10.2025
 */
@ApiStatus.Internal
public final class PoolManagerImpl extends PoolManager {

    private final Crystal platform;

    private final Map<String, LazyValue<ConnectionPool>> pools = new HashMap<>();

    private volatile boolean disabled = false;

    public PoolManagerImpl(Crystal platform) {
        this.platform = platform;

        setImpl(this);
    }

    public void initialize() {
        final PoolsConfiguration config = ConfigLibLoader.updateConfig(platform.config().poolsPath(), PoolsConfiguration.class, PoolsConfiguration.HEADER);
        if (config == null) {
            platform.logger().error("Pools configuration could not be loaded, Crystal PoolManager disabled!");
            disabled = true;
            return;
        }

        config.pools().forEach((name, pool) -> {
            final LazyValue<ConnectionPool> lazySource = new LazyValue<>(() -> {
                platform.logger().info("Creating pool {}: {}", name, pool);

                final ConnectionPool.Builder<?> builder = switch (pool.type()) {
                    case SQLITE -> ConnectionPoolBuilders.sqlite();
                    case H2 -> ConnectionPoolBuilders.h2();
                    case MYSQL -> ConnectionPoolBuilders.mysql();
                    case MARIADB -> ConnectionPoolBuilders.mariadb();
                    case POSTGRESQL -> ConnectionPoolBuilders.postgresql();
                };

                final ConnectionPool factory = switch (builder) {
                    case FlatfileConnectionPool.Builder<?> flatFileBuilder -> flatFileBuilder
                        .file(pool.file())
                        .build();
                    case HikariConnectionPool.Builder<?> hikariBuilder -> hikariBuilder
                        .address(pool.address())
                        .port(pool.port())
                        .database(pool.database())
                        .username(pool.username())
                        .password(pool.password())
                        .maxPoolSize(pool.maxPoolSize())
                        .minimumIdle(pool.minimumIdle())
                        .maxLifetime(pool.maxLifetime())
                        .keepAliveTime(pool.keepAliveTime())
                        .connectionTimeout(pool.connectionTimeout())
                        .properties(pool.properties())
                        .build();
                };

                factory.initialize();

                return factory;
            });

            this.pools.put(name, lazySource);

            if (pool.aliases() != null) {
                for (final String alias : pool.aliases()) {
                    this.pools.put(alias, lazySource);
                }
            }

            if (config.eagerConnect()) {
                platform.runAsync(lazySource::get);
            }
        });
    }

    @Override
    public Optional<ConnectionPool> getPool(@NotNull String poolName) {
        if (this.disabled) {
            throw new IllegalStateException("PoolManager is disabled!");
        }

        this.platform.logger().info("A pool was requested with name {}", poolName);

        return Optional.ofNullable(this.pools.get(poolName)).map(LazyValue::get);
    }

    @Override
    public ConnectionPool requirePool(@NotNull String poolName) {
        return getPool(poolName).orElseThrow(() -> new IllegalStateException("Pool " + poolName + " was not found, check pool configuration!"));
    }

    public void shutdown() {
        this.disabled = true;

        platform.logger().info("Closing {} pools", pools.size());

        for (final LazyValue<ConnectionPool> pool : pools.values()) {
            pool.ifPresent(ConnectionPool::shutdown);
        }
    }
}

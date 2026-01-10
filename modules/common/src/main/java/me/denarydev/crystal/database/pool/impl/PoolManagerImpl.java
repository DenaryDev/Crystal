/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.pool.impl;

import io.sapphiremc.lib.configurate.ConfigurateException;
import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.config.ConfigLoaders;
import me.denarydev.crystal.config.ConfigMapper;
import me.denarydev.crystal.config.internal.PoolsConfig;
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

    public void initialize() throws ConfigurateException {
        final PoolsConfig config = ConfigMapper.load(ConfigLoaders.yaml(platform.config().poolsPath(), PoolsConfig.HEADER), PoolsConfig.class);

        if (config == null) {
            platform.logger().error("Pools configuration could not be loaded, Crystal PoolManager disabled!");
            disabled = true;
            return;
        }

        config.pools().forEach((name, pool) -> {
            final LazyValue<ConnectionPool> lazySource = new LazyValue<>(() -> {
                platform.logger().info("Creating pool '{}': {}", name, pool);

                final ConnectionPool factory = createPool(pool);

                factory.initialize();

                return factory;
            });

            this.pools.put(name, lazySource);

            if (pool.aliases() != null) {
                for (String alias : pool.aliases()) {
                    this.pools.put(alias, lazySource);
                }
            }

            if (config.eagerConnect()) {
                platform.runAsync(lazySource::get);
            }
        });
    }

    private ConnectionPool createPool(PoolsConfig.PoolConfig pool) {
        final ConnectionPool.Builder<?> builder = switch (pool.type()) {
            case SQLITE -> ConnectionPoolBuilders.sqlite();
            case H2 -> ConnectionPoolBuilders.h2();
            case MYSQL -> ConnectionPoolBuilders.mysql();
            case MARIADB -> ConnectionPoolBuilders.mariadb();
            case POSTGRESQL -> ConnectionPoolBuilders.postgresql();
        };

        return switch (builder) {
            case FlatfileConnectionPool.Builder<?> flatFileBuilder -> flatFileBuilder
                .file(pool.file())
                .build();
            case HikariConnectionPool.Builder<?> hikariBuilder -> hikariBuilder
                .poolPrefix("Crystal-PoolManager")
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
    }

    @Override
    public Optional<ConnectionPool> getPool(@NotNull String poolName) {
        if (this.disabled) {
            throw new IllegalStateException("PoolManager is disabled!");
        }

        this.platform.logger().info("A pool was requested with name '{}'", poolName);

        final LazyValue<ConnectionPool> lazySource = this.pools.get(poolName);
        if (lazySource != null) {
            try {
                return Optional.of(lazySource.get());
            } catch (Throwable t) {
                this.platform.logger().error("Failed to initialize pool '{}'", poolName, t);
            }
        }

        return Optional.empty();
    }

    @Override
    public ConnectionPool requirePool(@NotNull String poolName) {
        return getPool(poolName).orElseThrow(() -> new IllegalStateException("Pool '" + poolName + "' was not found, check Crystal pools configuration!"));
    }

    public void shutdown() {
        this.disabled = true;

        platform.logger().info("Closing {} pools", pools.size());

        for (LazyValue<ConnectionPool> pool : pools.values()) {
            pool.ifPresent(ConnectionPool::shutdown);
        }
    }
}

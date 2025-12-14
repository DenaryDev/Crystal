/*
 * Copyright (c) 2025 DenaryDev
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

        final PoolsConfig.PoolConfig def = config.defaultSettings();

        config.pools().forEach((name, pool) -> {
            final LazyValue<ConnectionPool> lazySource = new LazyValue<>(() -> {
                platform.logger().info("Creating pool {}: {}", name, pool);

                final ConnectionPool factory = createPool(pool, def);

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

    private ConnectionPool createPool(PoolsConfig.PoolConfig pool, PoolsConfig.PoolConfig def) {
        final ConnectionPool.Builder<?> builder = switch (valueOrDefault(pool.type(), def.type())) {
            case SQLITE -> ConnectionPoolBuilders.sqlite();
            case H2 -> ConnectionPoolBuilders.h2();
            case MYSQL -> ConnectionPoolBuilders.mysql();
            case MARIADB -> ConnectionPoolBuilders.mariadb();
            case POSTGRESQL -> ConnectionPoolBuilders.postgresql();
        };

        return switch (builder) {
            case FlatfileConnectionPool.Builder<?> flatFileBuilder -> flatFileBuilder
                .file(valueOrDefault(pool.file(), def.file()))
                .build();
            case HikariConnectionPool.Builder<?> hikariBuilder -> hikariBuilder
                .address(valueOrDefault(pool.address(), def.address()))
                .port(valueOrDefault(pool.port(), def.port()))
                .database(valueOrDefault(pool.database(), def.database()))
                .username(valueOrDefault(pool.username(), def.username()))
                .password(valueOrDefault(pool.password(), def.password()))
                .maxPoolSize(valueOrDefault(pool.maxPoolSize(), valueOrDefault(def.maxPoolSize(), 6)))
                .minimumIdle(valueOrDefault(pool.minimumIdle(), valueOrDefault(def.minimumIdle(), 6)))
                .maxLifetime(valueOrDefault(pool.maxLifetime(), valueOrDefault(def.maxLifetime(), 1800000)))
                .keepAliveTime(valueOrDefault(pool.keepAliveTime(), valueOrDefault(def.keepAliveTime(), 0)))
                .connectionTimeout(valueOrDefault(pool.connectionTimeout(), valueOrDefault(def.connectionTimeout(), 5000)))
                .properties(valueOrDefault(pool.properties(), valueOrDefault(def.properties(), Map.of("useUnicode", "true", "characterEncoding", "utf8"))))
                .build();
        };
    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    public Optional<ConnectionPool> getPool(@NotNull String poolName) {
        if (this.disabled) {
            throw new IllegalStateException("PoolManager is disabled!");
        }

        this.platform.logger().info("A pool was requested with name {}", poolName);

        final LazyValue<ConnectionPool> lazySource = this.pools.get(poolName);
        if (lazySource != null) {
            try {
                return Optional.of(lazySource.get());
            } catch (Throwable t) {
                this.platform.logger().error("Failed to initialize pool {}", poolName, t);
            }
        }

        return Optional.empty();
    }

    @Override
    public ConnectionPool requirePool(@NotNull String poolName) {
        return getPool(poolName).orElseThrow(() -> new IllegalStateException("Pool " + poolName + " was not found, check Crystal pools configuration!"));
    }

    public void shutdown() {
        this.disabled = true;

        platform.logger().info("Closing {} pools", pools.size());

        for (LazyValue<ConnectionPool> pool : pools.values()) {
            pool.ifPresent(ConnectionPool::shutdown);
        }
    }
}

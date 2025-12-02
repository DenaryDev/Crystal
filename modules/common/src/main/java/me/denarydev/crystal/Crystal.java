/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal;

import me.denarydev.crystal.config.ConfigLoaders;
import me.denarydev.crystal.config.ConfigMapper;
import me.denarydev.crystal.config.internal.CrystalConfig;
import me.denarydev.crystal.database.pool.impl.PoolManagerImpl;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;

import java.nio.file.Path;

/**
 * @author DenaryDev
 * @since 19:39 27.10.2025
 */
@ApiStatus.Internal
public abstract class Crystal {

    private static Crystal instance;

    private PoolManagerImpl poolManager;

    private CrystalConfig config;

    public static Crystal instance() {
        return instance;
    }

    public final void enable() {
        this.poolManager = new PoolManagerImpl(this);

        try {
            this.config = ConfigMapper.load(ConfigLoaders.yaml(dataFolder().resolve("config.yml"), CrystalConfig.HEADER), CrystalConfig.class);

            poolManager.initialize();
        } catch (ConfigurateException e) {
            throw new RuntimeException("Failed to load crystal configuration", e);
        }
    }

    public final void disable() {
        if (poolManager != null) {
            poolManager.shutdown();
        }
    }

    public CrystalConfig config() {
        return config;
    }

    public abstract Logger logger();

    public abstract Path dataFolder();

    public abstract void runAsync(Runnable task);

    protected static void setInstance(Crystal platform) {
        instance = platform;
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal;

import me.denarydev.crystal.config.ConfigLibLoader;
import me.denarydev.crystal.config.internal.CrystalConfig;
import me.denarydev.crystal.database.pool.impl.PoolManagerImpl;
import me.denarydev.crystal.skin.SkinProviders;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

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

        this.config = ConfigLibLoader.updateConfig(dataFolder().resolve("config.yml"), CrystalConfig.class, CrystalConfig.HEADER);

        poolManager.initialize();

        SkinProviders.initialize(logger());
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

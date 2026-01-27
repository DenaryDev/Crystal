/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal;

import io.sapphiremc.lib.configurate.ConfigurateException;
import me.denarydev.crystal.config.ConfigLoaders;
import me.denarydev.crystal.config.ConfigMapper;
import me.denarydev.crystal.config.internal.CrystalConfig;
import me.denarydev.crystal.config.internal.MessagesConfig;
import me.denarydev.crystal.database.pool.impl.PoolManagerImpl;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.file.Path;

@ApiStatus.Internal
public abstract class Crystal {
    private static Crystal instance;

    private CrystalConfig config;
    private MessagesConfig messages;

    private PoolManagerImpl poolManager;

    public static Crystal instance() {
        return instance;
    }

    protected Crystal(Platform platform) {
        Platform.current = platform;
    }

    public final void enable() {
        this.poolManager = new PoolManagerImpl(this);

        try {
            this.config = ConfigMapper.load(ConfigLoaders.yaml(dataFolder().resolve("config.yml"), CrystalConfig.HEADER), CrystalConfig.class);

            if (Platform.current() != Platform.CORE) { // На коре сообщения попросту не нужны
                this.messages = ConfigMapper.load(ConfigLoaders.yaml(config.messagesPath(), MessagesConfig.HEADER), MessagesConfig.class);
            }

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

    public final CrystalConfig config() {
        return config;
    }

    public final MessagesConfig messages() {
        if (Platform.current() == Platform.CORE) {
            throw new UnsupportedOperationException("Messages are not supported on Core");
        }

        return messages;
    }

    public abstract Logger logger();

    public abstract Path dataFolder();

    public abstract void runAsync(Runnable task);

    protected static void setInstance(Crystal platform) {
        instance = platform;
    }
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.skin.SkinProvider;
import me.denarydev.crystal.skin.provider.SkinsRestorerSkinProvider;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * @author DenaryDev
 * @since 18:13 17.05.2025
 */
@Plugin(
    id = "crystal",
    name = "Crystal",
    version = BuildConfig.VERSION,
    description = BuildConfig.DESCRIPTION,
    authors = "DenaryDev",
    dependencies = {
        @Dependency(
            id = "skinsrestorer",
            optional = true
        )
    }
)
@ApiStatus.Internal
public final class VelocityPlugin extends Crystal {

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path directory;

    @Inject
    public VelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path directory) {
        this.proxy = proxy;
        this.logger = logger;
        this.directory = directory;
    }

    @Subscribe
    private void onProxyInitialization(ProxyInitializeEvent event) {
        setInstance(this);

        enable();

        if (proxy.getPluginManager().isLoaded("skinsrestorer")) {
            SkinProvider.set(new SkinsRestorerSkinProvider());
        }
    }

    @Subscribe
    private void onProxyShutdown(ProxyShutdownEvent event) {
        disable();
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    @Override
    public Path dataFolder() {
        return this.directory;
    }

    @Override
    public void runAsync(Runnable task) {
        proxy.getScheduler().buildTask(this, task).schedule();
    }
}

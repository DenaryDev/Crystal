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
import me.denarydev.crystal.skin.SkinProvider;
import me.denarydev.crystal.skin.provider.SkinsRestorerSkinProvider;
import me.denarydev.crystal.velocity.skin.VelocitySkinProvider;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.nio.file.Path;

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
public final class VelocityPlugin {
    private final CrystalVelocity crystal = new CrystalVelocity(this);

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
        crystal.enable();

        if (proxy.getPluginManager().isLoaded("skinsrestorer")) {
            SkinProvider.use(new SkinsRestorerSkinProvider());
        } else {
            SkinProvider.use(new VelocitySkinProvider(proxy));
        }
    }

    @Subscribe
    private void onProxyShutdown(ProxyShutdownEvent event) {
        crystal.disable();
    }

    public ProxyServer proxy() {
        return proxy;
    }

    public Logger logger() {
        return logger;
    }

    public Path dataFolder() {
        return directory;
    }
}

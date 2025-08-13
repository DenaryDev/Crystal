/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.denarydev.crystal.skin.SkinProviders;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

/**
 * @author DenaryDev
 * @since 18:13 17.05.2025
 */
@Plugin(
    id = "crystal",
    name = "Crystal",
    version = BuildConfig.VERSION,
    description = "Набор библиотек для плагинов на платформе Velocity",
    authors = "DenaryDev",
    dependencies = {
        @Dependency(id = "skinsrestorer", optional = true)
    }
)
@ApiStatus.Internal
public final class VelocityPlugin {

    private final Logger logger;

    @Inject
    public VelocityPlugin(ProxyServer proxy, Logger logger) {
        this.logger = logger;
    }

    @Subscribe
    private void onProxyInitialization(ProxyInitializeEvent event) {
        SkinProviders.initialize(logger);
    }
}

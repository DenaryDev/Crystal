/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
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
    authors = "DenaryDev"
)
public final class VelocityPlugin {

    @Inject
    public VelocityPlugin(ProxyServer proxy, Logger logger) {
    }
}

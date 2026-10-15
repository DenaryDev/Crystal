/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

/**
 * Crystal's plugin bootstrapper.
 * <p>
 * It intentionally does nothing. Paper exposes a plugin's class loader to other
 * plugins during the bootstrap phase only if that plugin has a bootstrapper of its
 * own, so this class exists purely to let dependants declare Crystal as a
 * {@code bootstrap} dependency and use its classes from their bootstrappers.
 */
@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Internal
public final class PaperBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(final @NonNull BootstrapContext context) {
        // Nothing to do: Crystal is initialised in PaperPlugin#onEnable.
    }
}

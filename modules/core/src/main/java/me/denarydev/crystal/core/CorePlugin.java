/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.core;

import org.jetbrains.annotations.ApiStatus;
import ru.prostocraft.core.api.plugin.Plugin;

@ApiStatus.Internal
public final class CorePlugin extends Plugin {

    private final CrystalCore platform = new CrystalCore(this);

    @Override
    public void onEnable() {
        platform.enable();
    }

    @Override
    public void onDisable() {
        platform.disable();
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper;

import me.denarydev.crystal.paper.listener.InventoryListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author DenaryDev
 * @since 18:09 17.05.2025
 */
@ApiStatus.Internal
public final class PaperPlugin extends JavaPlugin {

    private final CrystalPaper platform = new CrystalPaper(this);

    @Override
    public void onEnable() {
        platform.enable();

        final PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        platform.disable();
    }
}

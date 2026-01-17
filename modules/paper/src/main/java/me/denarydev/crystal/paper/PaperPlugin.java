/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper;

import me.denarydev.crystal.paper.listener.InventoryListener;
import me.denarydev.crystal.paper.skin.PaperSkinProvider;
import me.denarydev.crystal.skin.SkinProvider;
import me.denarydev.crystal.skin.provider.SkinsRestorerSkinProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PaperPlugin extends JavaPlugin {

    private final CrystalPaper platform = new CrystalPaper(this);

    @Override
    public void onEnable() {
        platform.enable();

        final PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new InventoryListener(), this);

        if (manager.isPluginEnabled("SkinsRestorer")) {
            SkinProvider.set(new SkinsRestorerSkinProvider());
        } else {
            SkinProvider.set(new PaperSkinProvider());
        }
    }

    @Override
    public void onDisable() {
        platform.disable();
    }
}

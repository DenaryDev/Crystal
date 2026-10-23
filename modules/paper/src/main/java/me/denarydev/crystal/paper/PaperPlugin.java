/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper;

import me.denarydev.crystal.paper.listener.InventoryListener;
import me.denarydev.crystal.paper.listener.ScoreboardListener;
import me.denarydev.crystal.paper.skin.PaperSkinProvider;
import me.denarydev.crystal.skin.SkinProvider;
import me.denarydev.crystal.skin.provider.SkinsRestorerSkinProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public final class PaperPlugin extends JavaPlugin {
    private final CrystalPaper crystal = new CrystalPaper(this);

    @Override
    public void onEnable() {
        crystal.enable();

        final PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new InventoryListener(), this);
        manager.registerEvents(new ScoreboardListener(), this);

        if (manager.isPluginEnabled("SkinsRestorer")) {
            SkinProvider.use(new SkinsRestorerSkinProvider(
                name -> {
                    final Player online = Bukkit.getPlayerExact(name);
                    if (online != null) return Optional.of(online.getUniqueId());
                    return Optional.ofNullable(Bukkit.getOfflinePlayerIfCached(name)).map(OfflinePlayer::getUniqueId);
                },
                uuid -> {
                    final Player online = Bukkit.getPlayer(uuid);
                    if (online != null) return Optional.of(online.getName());
                    return Optional.ofNullable(Bukkit.getOfflinePlayer(uuid).getName());
                },
                Bukkit.getOnlineMode(),
                Player.class,
                uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid))
            ));
        } else {
            SkinProvider.use(new PaperSkinProvider());
        }
    }

    @Override
    public void onDisable() {
        crystal.disable();
    }
}

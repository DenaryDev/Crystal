/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.skin;

import com.destroystokyo.paper.profile.ProfileProperty;
import me.denarydev.crystal.skin.SkinProperty;
import me.denarydev.crystal.skin.SkinProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApiStatus.Internal
public final class PaperSkinProvider extends SkinProvider {

    @Override
    public @NonNull Optional<SkinProperty> playerSkin(@NonNull UUID uuid) {
        final Player player = Bukkit.getPlayer(uuid);
        if (player == null) return Optional.empty();

        return playerSKin(player);
    }

    @Override
    public Optional<SkinProperty> playerSkin(@NonNull String name) {
        final Player player = Bukkit.getPlayer(name);
        if (player == null) return Optional.empty();

        return playerSKin(player);
    }

    private Optional<SkinProperty> playerSKin(@NonNull Player player) {
        final Set<ProfileProperty> properties = player.getPlayerProfile().getProperties();
        for (ProfileProperty property : properties) {
            if (property.getName().equals("textures")) {
                final String value = property.getValue();
                final String signature = property.getSignature();

                return Optional.of(new SkinProperty(value, signature));
            }
        }

        return Optional.empty();
    }
}

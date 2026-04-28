/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.skin.provider;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.skin.SkinProperty;
import me.denarydev.crystal.skin.SkinProvider;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.exception.DataRequestException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

@ApiStatus.Internal
public final class SkinsRestorerSkinProvider extends SkinProvider {

    private final SkinsRestorer skinsRestorer;

    public SkinsRestorerSkinProvider() {
        this.skinsRestorer = net.skinsrestorer.api.SkinsRestorerProvider.get();
    }

    @Override
    public Optional<SkinProperty> playerSkin(@NonNull UUID uuid) {
        return getPlayerSkin0(uuid.toString());
    }

    @Override
    public Optional<SkinProperty> playerSkin(@NonNull String name) {
        return getPlayerSkin0(name);
    }

    private Optional<SkinProperty> getPlayerSkin0(@NonNull String nameOrUniqueId) {
        if (skinsRestorer == null) return Optional.empty();

        try {
            return skinsRestorer.getSkinStorage().getPlayerSkin(nameOrUniqueId, true)
                .map(srSkin -> new SkinProperty(srSkin.getSkinProperty().getValue(), srSkin.getSkinProperty().getSignature()));
        } catch (DataRequestException e) {
            Crystal.instance().logger().error("Failed to get player skin from SkinsRestorer API", e);
            return Optional.empty();
        }
    }
}

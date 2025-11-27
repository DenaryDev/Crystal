/*
 * Copyright (c) 2025 DenaryDev
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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * @author DenaryDev
 * @since 20:03 13.08.2025
 */
public final class SkinsRestorerProvider implements SkinProvider {

    private final SkinsRestorer skinsRestorer;

    @ApiStatus.Internal
    public SkinsRestorerProvider() {
        this.skinsRestorer = net.skinsrestorer.api.SkinsRestorerProvider.get();
    }

    @Override
    public @NotNull Optional<SkinProperty> getPlayerSkin(@NotNull UUID uuid) {
        return getPlayerSkin0(uuid.toString());
    }

    @Override
    public @NotNull Optional<SkinProperty> getPlayerSkin(@NotNull String name) {
        return getPlayerSkin0(name);
    }

    @NotNull
    private Optional<SkinProperty> getPlayerSkin0(@NotNull String nameOrUniqueId) {
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

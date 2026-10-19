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
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Skin provider backed by SkinsRestorer.
 * <p>
 * Skins chosen by players live in SkinsRestorer's player storage and are keyed by UUID,
 * so this provider resolves names to UUIDs (and back) through the platform before asking
 * SkinsRestorer. Only when the UUID is unknown does it fall back to the Mojang skin of the name.
 */
@NullMarked
@ApiStatus.Internal
public final class SkinsRestorerSkinProvider extends SkinProvider {

    @Nullable
    private final SkinsRestorer skinsRestorer;
    private final Function<String, Optional<UUID>> uniqueIdResolver;
    private final Function<UUID, Optional<String>> nameResolver;
    private final boolean onlineMode;

    /**
     * @param uniqueIdResolver resolves a player name to the UUID the platform uses for that player
     * @param nameResolver     resolves a UUID to the last known player name
     * @param onlineMode       whether the platform runs in online mode
     */
    public SkinsRestorerSkinProvider(Function<String, Optional<UUID>> uniqueIdResolver,
                                     Function<UUID, Optional<String>> nameResolver,
                                     boolean onlineMode) {
        this.skinsRestorer = SkinsRestorerProvider.get();
        this.uniqueIdResolver = uniqueIdResolver;
        this.nameResolver = nameResolver;
        this.onlineMode = onlineMode;
    }

    @Override
    public Optional<SkinProperty> playerSkin(UUID uuid) {
        if (this.skinsRestorer == null) return Optional.empty();

        try {
            final Optional<String> name = this.nameResolver.apply(uuid);
            if (name.isPresent()) {
                return skinForPlayer(uuid, name.get());
            }

            // Without a name SkinsRestorer can only return a skin the player set explicitly
            return skinsRestorer.getPlayerStorage().getSkinOfPlayer(uuid).map(SkinsRestorerSkinProvider::convert);
        } catch (DataRequestException | RuntimeException e) {
            Crystal.instance().logger().error("Failed to get skin of {} from SkinsRestorer API", uuid, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<SkinProperty> playerSkin(String name) {
        if (this.skinsRestorer == null) return Optional.empty();

        try {
            final Optional<UUID> uuid = this.uniqueIdResolver.apply(name);
            if (uuid.isPresent()) {
                return skinForPlayer(uuid.get(), name);
            }

            // Unknown player: the best SkinsRestorer can do is the Mojang skin for that name
            return this.skinsRestorer.getSkinStorage().getPlayerSkin(name, true)
                .map(result -> convert(result.getSkinProperty()));
        } catch (DataRequestException | RuntimeException e) {
            // SkinsRestorer throws unchecked exceptions when Mojang API knows nothing about the name
            Crystal.instance().logger().error("Failed to get skin of {} from SkinsRestorer API", name, e);
            return Optional.empty();
        }
    }

    /**
     * Returns the skin SkinsRestorer would apply to this player on join:
     * the one they set, otherwise their premium skin or the configured default.
     */
    private Optional<SkinProperty> skinForPlayer(UUID uuid, String name) throws DataRequestException {
        if (this.skinsRestorer == null) return Optional.empty();

        return this.skinsRestorer.getPlayerStorage().getSkinForPlayer(uuid, name, this.onlineMode)
            .map(SkinsRestorerSkinProvider::convert);
    }

    private static SkinProperty convert(net.skinsrestorer.api.property.SkinProperty property) {
        return new SkinProperty(property.getValue(), property.getSignature());
    }
}

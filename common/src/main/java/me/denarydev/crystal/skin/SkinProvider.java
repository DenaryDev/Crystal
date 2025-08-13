/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.skin;

import me.denarydev.crystal.skin.provider.SkinsRestorerProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * @author DenaryDev
 * @since 20:04 13.08.2025
 */
@ApiStatus.AvailableSince("3.0.0")
public sealed interface SkinProvider permits SkinsRestorerProvider {

    /**
     * Получает скин игрока по его уникальному идентификатору (UUID).
     *
     * @param uuid уникальный ID игрока
     * @return Optional со скином, или пустой Optional, если скин не найден
     */
    @NotNull
    Optional<SkinProperty> getPlayerSkin(@NotNull UUID uuid);

    /**
     * Получает скин игрока по его никнейму.
     *
     * @param name никнейм игрока
     * @return Optional со скином, или пустой Optional, если скин не найден
     */
    @NotNull
    Optional<SkinProperty> getPlayerSkin(@NotNull String name);
}

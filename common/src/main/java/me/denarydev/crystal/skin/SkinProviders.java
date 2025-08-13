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
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Function;

/**
 * @author DenaryDev
 * @since 3:11 13.08.2025
 */
public final class SkinProviders {

    private static final Map<String, Function<Logger, SkinProvider>> providers = Map.of(
        "net.skinsrestorer.api.SkinsRestorerProvider", SkinsRestorerProvider::new
    );
    private static SkinProvider currentSkinProvider;

    /**
     * Возвращает текущий провайдер скинов, если он инициализирован.
     *
     * @return Провайдер скинов
     * @throws IllegalStateException если провайдер не инициализирован.
     */
    public static SkinProvider current() {
        if (currentSkinProvider == null) {
            throw new IllegalStateException("SkinProvider has not been initialized");
        }

        return currentSkinProvider;
    }

    @ApiStatus.Internal
    public static void initialize(Logger logger) {
        for (final var entry : providers.entrySet()) {
            try {
                Class.forName(entry.getKey());
                currentSkinProvider = entry.getValue().apply(logger);
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (currentSkinProvider != null) {
            logger.info("SkinProvider has been initialized! ({})", currentSkinProvider.getClass().getSimpleName());
        } else {
            logger.error("SkinProvider has not been initialized because compatible skin plugin not found!");
        }
    }
}

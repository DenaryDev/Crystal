/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.skin;

import me.denarydev.crystal.Crystal;
import me.denarydev.crystal.skin.provider.SkinsRestorerProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author DenaryDev
 * @since 3:11 13.08.2025
 */
public final class SkinProviders {

    private static final Map<String, Supplier<SkinProvider>> providers = Map.of(
        "skinsrestorer", SkinsRestorerProvider::new
    );
    private static SkinProvider currentSkinProvider;

    /**
     * Проверяет, инициализирован ли провайдер скинов.
     *
     * @return true, если провайдер инициализирован, иначе false
     */
    public static boolean isInitialized() {
        return currentSkinProvider != null;
    }

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
    public static void initialize(Predicate<String> isLoaded) {
        for (Map.Entry<String, Supplier<SkinProvider>> entry : providers.entrySet()) {
            if (isLoaded.test(entry.getKey())) {
                currentSkinProvider = entry.getValue().get();
            }
        }

        if (currentSkinProvider != null) {
            Crystal.instance().logger().info("SkinProvider has been initialized! ({})", currentSkinProvider.getClass().getSimpleName());
        } else {
            Crystal.instance().logger().error("SkinProvider has not been initialized because compatible skin plugin not found!");
        }
    }
}

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
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author DenaryDev
 * @since 3:11 13.08.2025
 */
public final class SkinProviders {

    private static final Map<String, Class<? extends SkinProvider>> providers = Map.of(
        "net.skinsrestorer.api.SkinsRestorerProvider", SkinsRestorerProvider.class
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
    public static void initialize() {
        for (final var entry : providers.entrySet()) {
            try {
                Class.forName(entry.getKey());

                entry.getValue().getDeclaredConstructor(Logger.class).newInstance();

                break;
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                Crystal.instance().logger().error("Failed to initialize SkinProvider of {}", entry.getKey(), e);
            }
        }

        if (currentSkinProvider != null) {
            Crystal.instance().logger().info("SkinProvider has been initialized! ({})", currentSkinProvider.getClass().getSimpleName());
        } else {
            Crystal.instance().logger().error("SkinProvider has not been initialized because compatible skin plugin not found!");
        }
    }
}

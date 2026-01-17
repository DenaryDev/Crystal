/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.skin;

import me.denarydev.crystal.Crystal;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public abstract class SkinProvider {

    private static SkinProvider current;

    /**
     * Проверяет, инициализирован ли провайдер скинов.
     *
     * @return true, если провайдер инициализирован, иначе false
     */
    public static boolean isInitialized() {
        return current != null;
    }

    /**
     * Возвращает текущий провайдер скинов, если он инициализирован.
     *
     * @return Провайдер скинов
     * @throws IllegalStateException если провайдер не инициализирован.
     */
    public static SkinProvider get() {
        if (current == null) {
            throw new IllegalStateException("SkinProvider has not been initialized");
        }

        return current;
    }

    /**
     * Устанавливает текущий провайдер скинов.
     * <p>
     * <b>Необходимо вызывать на этапе первой инициализации вашего плагина</b>
     *
     * @param provider провайдер скинов
     */
    public static void set(@NotNull SkinProvider provider) {
        current = provider;

        Crystal.instance().logger().info("Using {} as default skin provider", current.getClass().getSimpleName());
    }

    /**
     * Получает скин игрока по его уникальному идентификатору (UUID).
     *
     * @param uuid уникальный ID игрока
     * @return Optional со скином, или пустой Optional, если скин не найден
     */
    public abstract Optional<SkinProperty> getPlayerSkin(@NotNull UUID uuid);

    /**
     * Получает скин игрока по его никнейму.
     *
     * @param name никнейм игрока
     * @return Optional со скином, или пустой Optional, если скин не найден
     */
    public abstract Optional<SkinProperty> getPlayerSkin(@NotNull String name);
}

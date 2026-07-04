/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.skin;

import me.denarydev.crystal.Crystal;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public abstract class SkinProvider {

    private static SkinProvider current;

    /**
     * Returns whether the skin provider has been initialized.
     *
     * @return {@code true} if the provider is initialized; {@code false} otherwise.
     */
    public static boolean isInitialized() {
        return current != null;
    }

    /**
     * Returns the current skin provider.
     *
     * @return the skin provider.
     * @throws IllegalStateException if the provider has not been initialized.
     */
    public static SkinProvider current() {
        if (current == null) {
            throw new IllegalStateException("SkinProvider has not been initialized");
        }

        return current;
    }

    /**
     * Sets the current skin provider.
     * <p>
     * <b>Must be called during your plugin's initialization phase.</b>
     *
     * @param provider the skin provider to use.
     */
    public static void use(@NonNull SkinProvider provider) {
        current = provider;

        Crystal.instance().logger().info("Using {} as default skin provider", current.getClass().getSimpleName());
    }

    /**
     * Returns the skin of the player with the given UUID.
     *
     * @param uuid the player's unique ID.
     * @return an Optional containing the skin property, or an empty Optional if not found.
     */
    public abstract Optional<SkinProperty> playerSkin(@NonNull UUID uuid);

    /**
     * Returns the skin of the player with the given username.
     *
     * @param name the player's username.
     * @return an Optional containing the skin property, or an empty Optional if not found.
     */
    public abstract Optional<SkinProperty> playerSkin(@NonNull String name);
}

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

    /**
     * Returns whether this provider can change player skins.
     * When {@code false}, {@link #skinId(UUID)} is always empty and
     * {@link #setSkin(UUID, String)} and {@link #resetSkin(UUID)} throw.
     *
     * @return {@code true} if skins can be changed through this provider.
     */
    public boolean supportsSkinChange() {
        return false;
    }

    /**
     * Returns the skin explicitly set for the player, in the form accepted by {@link #setSkin(UUID, String)}:
     * the UUID of the player whose skin it is, or the skin name or URL.
     * <p>
     * <b>Blocking, must not be called on the main thread.</b>
     *
     * @param uuid the player's unique ID.
     * @return an Optional containing the skin id, or an empty Optional if the player has no skin set.
     */
    public Optional<String> skinId(@NonNull UUID uuid) {
        return Optional.empty();
    }

    /**
     * Sets the player's skin and applies it if the player is online.
     * <p>
     * <b>Blocking, must not be called on the main thread.</b>
     *
     * @param uuid the player's unique ID.
     * @param skin the UUID of the player whose skin to use, a player name, a skin name or a URL.
     * @return {@code true} if the skin was found and set; {@code false} otherwise.
     * @throws UnsupportedOperationException if {@link #supportsSkinChange()} is {@code false}.
     */
    public boolean setSkin(@NonNull UUID uuid, @NonNull String skin) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot change skins");
    }

    /**
     * Removes the skin set for the player, returning the default one, and applies it if the player is online.
     * <p>
     * <b>Blocking, must not be called on the main thread.</b>
     *
     * @param uuid the player's unique ID.
     * @throws UnsupportedOperationException if {@link #supportsSkinChange()} is {@code false}.
     */
    public void resetSkin(@NonNull UUID uuid) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot change skins");
    }
}

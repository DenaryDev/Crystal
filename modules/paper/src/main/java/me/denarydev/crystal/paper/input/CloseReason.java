/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.input;

/**
 * The reason a {@link ChatPrompt} was closed.
 */
public enum CloseReason {
    /**
     * The player successfully submitted a response.
     */
    SUCCESS,
    /**
     * The prompt was cancelled by the player.
     */
    CANCELLED,
    /**
     * The prompt timed out before the player responded.
     */
    TIMEOUT,
    /**
     * An error occurred while processing the player's response.
     */
    ERROR
}

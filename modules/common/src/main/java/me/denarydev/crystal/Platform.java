/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal;

/**
 * Enumeration of platforms supported by Crystal.
 * <p>
 * Defines the runtime environment, allowing the library to adapt its logic
 * for a specific server core or proxy.
 */
public enum Platform {
    /**
     * The Paper platform and its derivatives.
     */
    PAPER,

    /**
     * The Velocity proxy.
     */
    VELOCITY,

    /**
     * The custom ProstoCraft Core server core.
     */
    CORE;

    static Platform current;

    /**
     * Returns the platform Crystal is currently running on.
     *
     * @return the current platform
     */
    public static Platform current() {
        return current;
    }

    /**
     * Returns whether this platform is the current runtime environment.
     * <p>
     * Example usage: {@code Platform.CORE.isCurrent()}
     *
     * @return {@code true} if this platform is active
     */
    public boolean isCurrent() {
        return this == current;
    }
}

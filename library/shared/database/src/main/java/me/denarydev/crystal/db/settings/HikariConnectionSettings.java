/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.settings;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DenaryDev
 * @since 0:58 24.11.2023
 */
@ApiStatus.AvailableSince("2.1.0")
public non-sealed interface HikariConnectionSettings extends ConnectionSettings {

    /**
     * The IP or address of the database.
     */
    @NotNull
    String address();

    /**
     * The port to connect on.
     * <p>
     * If null, the default port of selected type will be used.
     */
    @Nullable
    default String port() {
        return null;
    }

    /**
     * The database to use in the database.
     */
    @NotNull
    String database();

    /**
     * The username to authenticate as.
     */
    @NotNull
    String username();

    /**
     * The password to authenticate with.
     */
    @NotNull
    String password();

    /**
     * The maximum amount of simultaneous connections.
     * Should be the same as you have cores.
     */
    default int maxPoolSize() {
        return 6;
    }

    /**
     * The amount of connections that should always be open.
     * To avoid issues, set this to the same value as maxPoolSize.
     */
    default int minimumIdle() {
        return 6;
    }

    /**
     * The amount of milliseconds one connection should stay open.
     */
    default int maxLifetime() {
        return 1800000;
    }

    /**
     * The setting in which interval to 'ping' the database. Set to 0 to disable.
     */
    default int keepAliveTime() {
        return 0;
    }

    /**
     * The amount of seconds we wait for a response from the database before timing out.
     */
    default int connectionTimeout() {
        return 5000;
    }

    /**
     * Other properties you may want to set.
     * <p>
     * You can disable SSL by adding the following properties:
     * <pre>
     * <strong>useSSL = false
     * verifyServerCertificate = false</strong>
     * ...and can add any other sql properties</pre>
     */
    @NotNull
    default Map<String, String> properties() {
        final var map = new HashMap<String, String>();
        map.put("useUnicode", "true");
        map.put("characterEncoding", "utf8");
        return map;
    }
}

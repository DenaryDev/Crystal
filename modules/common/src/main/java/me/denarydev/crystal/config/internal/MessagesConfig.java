/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.internal;

import io.sapphiremc.lib.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ApiStatus.Internal
@ConfigSerializable
public final class MessagesConfig {
    public static final String HEADER = """
        +--------------------------------+
        |             Crystal            |
        |          by DenaryDev          |
        +--------------------------------+
        |- This config defines messages sent to players.
        |- If a config file is located outside the plugin folder, provide its full path.
        """;

    private Errors errors = new Errors();

    public Errors errors() {
        return errors;
    }

    @ConfigSerializable
    public static final class Errors {
        private String errorWithCode = "<red>An unknown error has occurred! Please contact the administrators and provide this error code: <yellow><u><code></u>";
        private String errorCodeHover = "<gray>Click to copy the error code";

        public String errorWithCode() {
            return errorWithCode;
        }

        public String errorCodeHover() {
            return errorCodeHover;
        }
    }
}

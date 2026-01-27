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
        |- В этом конфиге настраиваются пути ко всем остальным конфигам.
        |- Если конфиг лежит не в папке плагина, указывайте полный путь к нему!
        """;

    private Errors errors = new Errors();

    public Errors errors() {
        return errors;
    }

    @ConfigSerializable
    public static final class Errors {
        private String errorWithCode = "<red>Произошла неизвестная ошибка! Обратитесь к администрации, сообщив код ошибки: <yellow><u><code></u>";
        private String errorCodeHover = "<gray>Нажми, чтобы скопировать код ошибки";

        public String errorWithCode() {
            return errorWithCode;
        }

        public String errorCodeHover() {
            return errorCodeHover;
        }
    }
}

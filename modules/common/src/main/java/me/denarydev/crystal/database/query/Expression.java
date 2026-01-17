/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query;

import org.jetbrains.annotations.ApiStatus;

/**
 * Внутренний класс, не используйте напрямую.
 *
 * @author DenaryDev
 * @since 2:17 17.01.2026
 */
@ApiStatus.Internal
public record Expression(String expr, Object[] params) {
}

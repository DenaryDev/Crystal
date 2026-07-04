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
 * Internal class; do not use directly.
 */
@ApiStatus.Internal
public record Expression(String expr, Object[] params) {
}

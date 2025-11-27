/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.input.actions;

import me.denarydev.crystal.paper.input.CloseReason;

/**
 * Действие при закрытии запроса.
 *
 * @author DenaryDev
 * @since 4:23 27.11.2025
 */
@FunctionalInterface
public interface CloseAction {
    void onClose(CloseReason reason);
}

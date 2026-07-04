/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.input.actions;

import me.denarydev.crystal.paper.input.CloseReason;

/**
 * An action executed when a {@link me.denarydev.crystal.paper.input.ChatPrompt} closes.
 */
@FunctionalInterface
public interface CloseAction {
    void onClose(CloseReason reason);
}

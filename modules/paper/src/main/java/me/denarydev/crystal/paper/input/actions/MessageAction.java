/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.input.actions;

/**
 * Действие при получении ответа на запрос.
 */
@FunctionalInterface
public interface MessageAction {
    void onMessage(String message);
}

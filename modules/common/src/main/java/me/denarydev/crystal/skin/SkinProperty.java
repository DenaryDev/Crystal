/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.skin;

/**
 * Свойство скина игрока — пара из Base64-значения текстуры и её подписи.
 *
 * @param value     Base64-закодированные данные текстуры
 * @param signature криптографическая подпись Mojang для данной текстуры
 */
public record SkinProperty(String value, String signature) {
}

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.skin;

/**
 * A player skin property — a pair of a Base64-encoded texture value and its signature.
 *
 * @param value     the Base64-encoded texture data.
 * @param signature the Mojang cryptographic signature for this texture.
 */
public record SkinProperty(String value, String signature) {
}

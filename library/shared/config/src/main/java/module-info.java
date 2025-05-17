/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
module crystal.shared.config {
    requires org.spongepowered.configurate.hocon;
    requires org.jetbrains.annotations;
    requires org.spongepowered.configurate;
    requires org.spongepowered.configurate.yaml;

    exports me.denarydev.crystal.config;
}

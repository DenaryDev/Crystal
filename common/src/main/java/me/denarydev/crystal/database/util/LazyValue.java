/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author DenaryDev
 * @since 19:33 27.10.2025
 */
@ApiStatus.Internal
public final class LazyValue<T> {

    private final Supplier<T> supplier;

    private volatile T value;

    public LazyValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        T value = this.value;

        if (value == null) {
            synchronized (this) {
                value = this.value;

                if (value == null) {
                    value = this.value = this.supplier.get();

                    if (value == null) {
                        throw new IllegalStateException("Supplier returned null");
                    }
                }
            }
        }

        return value;
    }

    public void ifPresent(@NotNull Consumer<T> consumer) {
        final T value = this.value;

        if (value != null) {
            consumer.accept(value);
        }
    }
}

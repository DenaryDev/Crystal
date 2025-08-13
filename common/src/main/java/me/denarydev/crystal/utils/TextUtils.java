/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Методы для работы со строками.
 *
 * @author DenaryDev
 * @since 13:47 25.11.2023
 */
@ApiStatus.AvailableSince("2.1.0")
public final class TextUtils {

    /**
     * Делает первый символ строки заглавным.
     *
     * @param string строка со строчной буквы
     * @return Строка с заглавной буквы
     */
    @NotNull
    public static String capitalize(@NotNull final String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Делает первый символ каждой строки в списке заглавным.
     *
     * @param strings список строк со строчной буквы
     * @return список Строк с заглавной буквы
     */
    @NotNull
    public static List<String> capitalizeAll(@NotNull final List<String> strings) {
        return strings.stream().map(TextUtils::capitalize).toList();
    }

    /**
     * Делает первый символ каждой строки в массиве заглавным.
     *
     * @param text массив строк со строчной буквы
     * @return массив Строк с заглавной буквы
     */
    @NotNull
    public static String[] capitalizeAll(@NotNull final String... text) {
        return Arrays.stream(text).map(TextUtils::capitalize).toArray(String[]::new);
    }
}

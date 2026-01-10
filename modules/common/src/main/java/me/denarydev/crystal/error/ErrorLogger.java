/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.error;

import me.denarydev.crystal.random.StringGenerator;
import org.slf4j.Logger;

/**
 * @author DenaryDev
 * @since 3:27 10.01.2026
 */
public final class ErrorLogger {

    /**
     * Выводит указанную ошибку в консоль через указанный логгер, добавляя к ней
     * сгенерированный код ошибки, который возвращается этим методом.
     * <p>
     * Код ошибки затем можно отправить игроку, чтобы проще было идентифицировать
     * логи по жалобе игрока.
     *
     * @param logger  логгер, через который выводить ошибку
     * @param message сообщение об ошибке
     * @param params  параметры сообщения об ошибке (как в обычном slf4j логгере)
     * @return код ошибки, который был выведен в консоль
     */
    public static String logError(Logger logger, String message, Object... params) {
        final StringBuilder builder = new StringBuilder();
        builder.append(message);

        final String code = StringGenerator.generateRandomString(8);
        builder.append(" (error code: ").append(code).append(" )");

        logger.error(builder.toString(), params);

        return code;
    }
}

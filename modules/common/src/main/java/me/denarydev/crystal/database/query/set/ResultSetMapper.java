/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query.set;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Преобразует {@link ResultSet} в объект.
 *
 * @author DenaryDev
 * @since 2:17 17.01.2026
 */
@FunctionalInterface
public interface ResultSetMapper<T> {

    /**
     * Преобразует {@link ResultSet} в объект.
     * Перед вызовом этого метода объект ResultSet должен указывать на строку.
     *
     * @param set Результирующая выборка (Result set).
     * @return Значение, может быть null.
     * @throws SQLException При ошибке SQL.
     */
    T map(ResultSet set) throws SQLException;
}

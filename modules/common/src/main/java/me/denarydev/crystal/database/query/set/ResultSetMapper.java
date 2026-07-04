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
 * Converts a {@link ResultSet} row into an object.
 */
@FunctionalInterface
public interface ResultSetMapper<T> {

    /**
     * Maps the current row of the given {@link ResultSet} to an object.
     * The ResultSet must already be positioned on a row before this method is called.
     *
     * @param set the result set.
     * @return the mapped value; may be null.
     * @throws SQLException on SQL error.
     */
    T map(ResultSet set) throws SQLException;
}

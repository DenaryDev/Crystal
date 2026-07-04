/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.query.set;

import org.jspecify.annotations.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A wrapper around {@link ResultSet} that provides convenient operations such as row mapping.
 * Closing this wrapper also closes the underlying ResultSet.
 *
 * @param set the underlying ResultSet.
 */
public record ResultSetWrapper(ResultSet set) implements AutoCloseable {

    /**
     * Maps the first row in the result set (if any) using the given mapper and returns the result as an Optional.
     * The underlying ResultSet is closed after this method returns.
     * The Optional will be empty if there are no rows or if the mapper returned null.
     *
     * @param mapper the row mapper.
     * @param <T>    the type of the mapped element.
     * @return an Optional containing the mapped row.
     * @throws SQLException on SQL error.
     */
    public <T> Optional<T> map(@NonNull ResultSetMapper<T> mapper) throws SQLException {
        try (final ResultSet set = this.set) {
            return set.next() ? Optional.ofNullable(mapper.map(set)) : Optional.empty();
        }
    }

    /**
     * Maps all rows in the result set using the given mapper and collects the results into a list.
     * The underlying ResultSet is closed after this method returns.
     *
     * @param mapper the row mapper.
     * @param <T>    the type of the mapped element.
     * @return a list of mapped rows.
     * @throws SQLException on SQL error.
     */
    public <T> List<T> mapAll(@NonNull ResultSetMapper<T> mapper) throws SQLException {
        final List<T> list = new ArrayList<>();

        try (final ResultSet set = this.set) {
            while (set.next()) {
                list.add(mapper.map(set));
            }
        }

        return list;
    }

    /**
     * Closes the underlying {@link ResultSet}.
     *
     * @throws SQLException on SQL error.
     */
    @Override
    public void close() throws SQLException {
        this.set.close();
    }
}

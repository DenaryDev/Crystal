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
 * Обертка для {@link ResultSet}, позволяющая выполнять полезные операции, такие как маппинг.
 * При закрытии обертки будет закрыт и лежащий в основе ResultSet.
 *
 * @param set Объект ResultSet.
 */
public record ResultSetWrapper(ResultSet set) implements AutoCloseable {

    /**
     * Преобразует первую строку в наборе результатов (если она существует) с помощью указанного маппера
     * и возвращает Optional со значением.
     * Базовый ResultSet будет закрыт после возврата из этого метода.
     * Optional будет пустым, если набор не содержит строк или если маппер вернул null.
     *
     * @param mapper Маппер.
     * @param <T>    Тип преобразованного элемента.
     * @return Optional, содержащий преобразованную строку.
     * @throws SQLException При ошибке SQL.
     */
    public <T> Optional<T> map(@NonNull ResultSetMapper<T> mapper) throws SQLException {
        try (final ResultSet set = this.set) {
            return set.next() ? Optional.ofNullable(mapper.map(set)) : Optional.empty();
        }
    }

    /**
     * Преобразует все строки в наборе результатов с помощью указанного маппера и собирает результаты в список.
     * Базовый ResultSet будет закрыт после возврата из этого метода.
     *
     * @param mapper Маппер.
     * @param <T>    Тип преобразованного элемента.
     * @return Список преобразованных строк.
     * @throws SQLException При ошибке SQL.
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
     * Закрывает лежащий в основе {@link ResultSet}.
     *
     * @throws SQLException При ошибке SQL.
     */
    @Override
    public void close() throws SQLException {
        this.set.close();
    }
}

/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.connection.hikari;

import me.denarydev.crystal.db.DatabaseType;
import me.denarydev.crystal.db.settings.HikariConnectionSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author DenaryDev
 * @since 0:09 24.11.2023
 */
@ApiStatus.Internal
@ApiStatus.AvailableSince("2.1.0")
public final class MariaDBConnectionFactory extends DriverBasedHikariConnectionFactory {
    public MariaDBConnectionFactory(HikariConnectionSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull DatabaseType implementationType() {
        return DatabaseType.MARIADB;
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }

    @Override
    protected String driverClassName() {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "mariadb";
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }
}

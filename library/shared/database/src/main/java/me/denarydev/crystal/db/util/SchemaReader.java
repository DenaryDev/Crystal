/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.util;

import org.jetbrains.annotations.ApiStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * Methods for getting schemas from .sql files
 *
 * @author DenaryDev
 * @since 13:33 23.12.2023
 */
@ApiStatus.AvailableSince("2.1.0")
public final class SchemaReader {
    private SchemaReader() {
    }

    public static List<String> getStatements(final InputStream is) throws IOException {
        final var queries = new LinkedList<String>();

        try (final var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            var builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }

                builder.append(line);

                // check for end of declaration
                if (line.endsWith(";")) {
                    builder.deleteCharAt(builder.length() - 1);

                    final var result = builder.toString().trim();
                    if (!result.isEmpty()) {
                        queries.add(result);
                    }

                    // reset
                    builder = new StringBuilder();
                }
            }
        }

        return queries;
    }
}

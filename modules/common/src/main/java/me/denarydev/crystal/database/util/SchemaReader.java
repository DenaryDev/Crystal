/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.database.util;

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
public final class SchemaReader {

    private SchemaReader() {
    }

    public static List<String> getStatements(final InputStream is) throws IOException {
        final List<String> queries = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }

                builder.append(line);

                // check for end of declaration
                if (line.endsWith(";")) {
                    builder.deleteCharAt(builder.length() - 1);

                    final String result = builder.toString().trim();
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

/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.command;

import com.google.common.base.CharMatcher;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * Utilities for simplifying tab-completion of commands built with Brigadier.
 */
public final class SharedSuggestionProvider {
    private static final CharMatcher MATCH_SPLITTER = CharMatcher.anyOf("._/");

    /**
     * Adds tab-completion suggestions from the given list to the given builder.
     *
     * @param variants the suggestion candidates.
     * @param builder  the suggestions builder.
     * @return a {@link CompletableFuture} containing the built {@link Suggestions}.
     */
    public static CompletableFuture<Suggestions> suggest(Iterable<String> variants, SuggestionsBuilder builder) {
        final String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (String variant : variants) {
            if (matchesSubStr(remaining, variant.toLowerCase(Locale.ROOT))) {
                builder.suggest(variant);
            }
        }

        return builder.buildFuture();
    }

    private static boolean matchesSubStr(String input, String substring) {
        int i = 0;

        while (!substring.startsWith(input, i)) {
            int j = MATCH_SPLITTER.indexIn(substring, i);
            if (j < 0) {
                return false;
            }

            i = j + 1;
        }

        return true;
    }
}

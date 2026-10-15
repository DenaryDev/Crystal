/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts legacy colour codes inside a string into MiniMessage tags, so that text written in
 * any of the common formats can be passed to {@code MiniMessage#deserialize} as is.
 *
 * <p>Supported legacy formats (both {@code §} and {@code &} prefixes, case-insensitive):</p>
 * <ul>
 *     <li>named colours {@code §0}–{@code §f} and decorations {@code §k}–{@code §o}, {@code §r};</li>
 *     <li>hex colours {@code §#rrggbb};</li>
 *     <li>Bungee hex colours {@code §x§r§r§g§g§b§b}.</li>
 * </ul>
 *
 * <p>Legacy semantics are preserved: a colour code cancels any active decoration, so when a
 * colour follows a decoration a {@code <reset>} tag is emitted before the colour tag.</p>
 *
 * <p>This class has no dependency on Adventure: the conversion is plain string processing, and
 * MiniMessage tags are detected by a list of the standard tag names.</p>
 */
@NullMarked
public final class ColorConverter {

    private static final Pattern SECTION = pattern('§');
    private static final Pattern AMPERSAND = pattern('&');

    private static final String[] COLORS = {
        "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray",
        "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white"
    };

    // Names of the tags registered by MiniMessage's StandardTags; unknown tags are left as text by MiniMessage
    private static final String STANDARD_TAGS = String.join("|",
        String.join("|", COLORS), "grey", "dark_grey", "color", "colour", "c",
        "bold", "b", "italic", "i", "em", "underlined", "u", "strikethrough", "st", "obfuscated", "obf", "decoration",
        "reset", "click", "hover", "key", "lang", "translate", "tr", "lang_or", "translate_or", "tr_or", "insert",
        "rainbow", "gradient", "transition", "font", "newline", "br", "selector", "sel", "score", "nbt", "data",
        "pride", "shadow", "sprite", "head"
    );

    // <name>, <name:args>, </name>, <!name> and <#hex> (MiniMessage accepts 1 to 6 hex digits);
    // a quoted argument containing '>' still yields a match
    private static final Pattern MINI_MESSAGE_TAG = Pattern.compile(
        "<[/!]?(?:" + STANDARD_TAGS + "|#[0-9a-f]{1,6})(?::[^<>]*)?>", Pattern.CASE_INSENSITIVE);

    private ColorConverter() {
    }

    /**
     * Converts legacy colour codes in the text into MiniMessage tags.
     *
     * <p>The text is expected to be in a single format: legacy section, MiniMessage or legacy
     * ampersand, checked in that order. Ampersand ({@code &}) codes are converted only when the text
     * contains no MiniMessage tags (see {@link #hasMiniMessageTags(String)}), so that a MiniMessage
     * string such as {@code "<green>R&B party"} is not mangled. Text without any codes is returned
     * unchanged.</p>
     *
     * @param text the text in MiniMessage, legacy section or legacy ampersand format.
     * @return the text with every legacy code replaced by the corresponding MiniMessage tag.
     */
    public static String toMiniMessage(final String text) {
        if (SECTION.matcher(text).find()) return convert(text, SECTION);

        // In MiniMessage text '&' is literal ("R&B")
        if (hasMiniMessageTags(text)) return text;

        return convert(text, AMPERSAND);
    }

    /**
     * Converts legacy colour codes in each line into MiniMessage tags.
     *
     * @param lines the lines to convert.
     * @return a new list with the converted lines.
     * @see #toMiniMessage(String)
     */
    public static List<String> toMiniMessage(final List<String> lines) {
        return lines.stream().map(ColorConverter::toMiniMessage).toList();
    }

    /**
     * Converts legacy colour codes that use the given prefix character into MiniMessage tags.
     * No heuristics are applied: every valid code with this prefix is converted.
     *
     * @param text   the text to convert.
     * @param prefix the legacy code prefix, usually {@code '§'} or {@code '&'}.
     * @return the text with every matching code replaced by the corresponding MiniMessage tag.
     */
    public static String toMiniMessage(final String text, final char prefix) {
        return switch (prefix) {
            case '§' -> convert(text, SECTION);
            case '&' -> convert(text, AMPERSAND);
            default -> convert(text, pattern(prefix));
        };
    }

    /**
     * Checks whether the text contains any legacy colour code with the given prefix.
     *
     * @param text   the text to check.
     * @param prefix the legacy code prefix, usually {@code '§'} or {@code '&'}.
     * @return {@code true} if at least one valid legacy code was found.
     */
    public static boolean hasLegacyCodes(final String text, final char prefix) {
        final Pattern pattern = switch (prefix) {
            case '§' -> SECTION;
            case '&' -> AMPERSAND;
            default -> pattern(prefix);
        };
        return pattern.matcher(text).find();
    }

    /**
     * Checks whether the text contains at least one standard MiniMessage tag, such as {@code <red>},
     * {@code </bold>}, {@code <#ff0000>} or {@code <click:run_command:'/help'>}. Tags that MiniMessage
     * itself does not know, such as {@code <world>} or custom placeholders, are not recognised.
     *
     * @param text the text to check.
     * @return {@code true} if a standard MiniMessage tag was found.
     */
    public static boolean hasMiniMessageTags(final String text) {
        return text.indexOf('<') != -1 && MINI_MESSAGE_TAG.matcher(text).find();
    }

    // Same codes LegacyComponentSerializer recognises; the &x form is listed first so that it wins over its parts
    private static Pattern pattern(final char prefix) {
        final String p = Pattern.quote(String.valueOf(prefix));
        return Pattern.compile(p + "(?:x(?:" + p + "[0-9a-f]){6}|#[0-9a-f]{6}|[0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
    }

    private static String convert(final String text, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) return text;

        final StringBuilder result = new StringBuilder(text.length() + 32);
        boolean decorated = false;
        int last = 0;

        do {
            result.append(text, last, matcher.start());
            final String code = matcher.group();
            final char type = Character.toLowerCase(code.charAt(1));

            switch (type) {
                case 'k' -> decorated = decoration(result, "obfuscated");
                case 'l' -> decorated = decoration(result, "bold");
                case 'm' -> decorated = decoration(result, "strikethrough");
                case 'n' -> decorated = decoration(result, "underlined");
                case 'o' -> decorated = decoration(result, "italic");
                case 'r' -> {
                    result.append("<reset>");
                    decorated = false;
                }
                case '#' -> decorated = color(result, decorated, code.substring(1));
                case 'x' -> {
                    // &x&r&r&g&g&b&b: hex digits sit at every odd offset after "&x"
                    final StringBuilder hex = new StringBuilder("#");
                    for (int i = 3; i < code.length(); i += 2) hex.append(code.charAt(i));
                    decorated = color(result, decorated, hex.toString());
                }
                default -> decorated = color(result, decorated, COLORS[Character.digit(type, 16)]);
            }

            last = matcher.end();
        } while (matcher.find());

        result.append(text, last, text.length());
        return result.toString();
    }

    private static boolean decoration(final StringBuilder result, final String tag) {
        result.append('<').append(tag).append('>');
        return true;
    }

    // Colour codes cancel decorations in legacy formatting, MiniMessage colour tags do not
    private static boolean color(final StringBuilder result, final boolean decorated, final String color) {
        if (decorated) result.append("<reset>");
        result.append('<').append(color.toLowerCase(Locale.ROOT)).append('>');
        return false;
    }
}

package charmony.rune_dictionary.common.features.rune_dictionary;

import charmony.core.base.Log;
import charmony.rune_dictionary.RuneDictionaryMod;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

@SuppressWarnings("unused")
public class Helpers {
    private static final Log LOGGER = new Log(RuneDictionaryMod.ID, "Helpers");

    public static final char FIRST_RUNE = 'a';
    public static final char LAST_RUNE = 'z';
    public static final char UNKNOWN_LETTER = '?';
    public static final int NUM_RUNES = 26;

    /**
     * Generate a random rune word for a given input string.
     *
     * @param input The input string to encode.
     * @param seed The seed to use for the random number generator.
     * @param minLength The minimum length of the generated word.
     * @param maxLength The maximum length of the generated word.
     */
    public static String generateRunes(String input, long seed, int minLength, int maxLength) {
        int alphaStart = FIRST_RUNE;
        int alphaEnd = LAST_RUNE;
        String hashed;

        try {
            var digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            var result = new StringBuilder();

            for (byte b : hash) {
                int value = b & 0xFF;
                result.append((char)(FIRST_RUNE + (value % NUM_RUNES)));
            }
            hashed = result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        StringBuilder in = new StringBuilder(hashed);
        StringBuilder out = new StringBuilder();

        var seedFromHash = 0;
        for (int i = 0; i < input.length(); i++) {
            seedFromHash = 31 * seedFromHash + input.charAt(i);
        }
        seedFromHash = Math.abs(seedFromHash);

        var wordSeed = RandomSource.create(seed);
        var lengthSeed = RandomSource.create(seedFromHash);
        var length = lengthSeed.nextIntBetweenInclusive(minLength, maxLength);

        for (int tries = 0; tries < 9; tries++) {
            if (in.length() >= length) {
                wordSeed.nextInt();
                char[] chars = in.toString().toLowerCase(Locale.ROOT).toCharArray();

                // Work over the string backwards by character.
                for (int i = Math.min(chars.length, length) - 1; i >= 0; --i) {
                    int chr = chars[i];

                    if (chr >= alphaStart && chr <= alphaEnd) {
                        // Shift the char with a random number of the total runes, wrapping around if it goes out of bounds.
                        int ri = chr + wordSeed.nextInt(NUM_RUNES);
                        if (ri > alphaEnd) {
                            chr = Mth.clamp(alphaStart + (ri - alphaEnd), alphaStart + 1, alphaEnd);
                        }

                        // Shift the char again with a random number of half the total runes, wrapping again as necessary.
                        ri += wordSeed.nextInt(NUM_RUNES / 2);
                        if (ri > alphaEnd) {
                            chr = Mth.clamp(alphaStart + (ri - alphaEnd), alphaStart + 1, alphaEnd);
                        }

                        out.append((char)chr);
                    }
                }

                // Write to debug log and output.
                var converted = out.reverse().toString();
                LOGGER.debug("(generateRunes) Word: '" + input + "', translation: '" + converted + "'");
                return converted;
            }

            // Keep adding the converted string to the end of the builder to bring the length up.
            in.append(hashed);
        }

        throw new RuntimeException("Maximum loops reached when checking string length");
    }
}

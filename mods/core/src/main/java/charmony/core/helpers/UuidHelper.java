package charmony.core.helpers;

import net.minecraft.util.RandomSource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class UuidHelper {
    public static UUID fromRandom(RandomSource random) {
        long mostSigBits = random.nextLong();
        long leastSigBits = random.nextLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public static UUID fromString(String str) throws NoSuchAlgorithmException {
        var sha1 = MessageDigest.getInstance("SHA-1");
        var hash = sha1.digest(str.getBytes(StandardCharsets.UTF_8));

        long mostSigBits = 0;
        long leastSigBits = 0;

        for (int i = 0; i < 8; i++) {
            mostSigBits = (mostSigBits << 8) | (hash[i] & 0xff);
        }

        for (int i = 8; i < 16; i++) {
            leastSigBits = (leastSigBits << 8) | (hash[i] & 0xff);
        }

        return new UUID(mostSigBits, leastSigBits);
    }
}

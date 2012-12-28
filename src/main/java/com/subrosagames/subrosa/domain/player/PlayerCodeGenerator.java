package com.subrosagames.subrosa.domain.player;

import java.util.Date;
import java.util.Random;

/**
*
*/
final class PlayerCodeGenerator {

    private static final Random RANDOM = new Random(new Date().getTime());

    private PlayerCodeGenerator() { }

    public static String generate() {
        StringBuilder builder = new StringBuilder(5);
        while (builder.length() < 5) {
            int index = RANDOM.nextInt(58);
            int ascii = index < 10 ? index + 48 // < 10 -> 48 (0) - 57 (9)
                    : index < 36 ? index + 55 // 10 - 36 -> 65 (A) - 90 (Z)
                    : index + 61; // > 36 -> 97 (a) - 122 (z)
            builder.append((char) ascii);
        }
        return builder.toString();
    }
}

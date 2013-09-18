package com.subrosagames.subrosa.util;

import java.util.Random;

public final class RandomString
{
    private static final Random RANDOM = new Random();

    private static final char[] SYMBOLS = new char[62];
    static {
        for (int idx = 0; idx < 10; ++idx) {
            SYMBOLS[idx] = (char) ('0' + idx);
        }
        for (int idx = 10; idx < 36; ++idx) {
            SYMBOLS[idx] = (char) ('a' + idx - 10);
        }
        for (int idx = 36; idx < 62; ++idx) {
            SYMBOLS[idx] = (char) ('a' + idx - 36);
        }
    }

    private RandomString() { }

    public static String generate(int length)
    {
        if (length < 1) {
            throw new IllegalArgumentException("Cannot generate string of length < 1, found " + length);
        }
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
        }
        return new String(buf);
    }

}


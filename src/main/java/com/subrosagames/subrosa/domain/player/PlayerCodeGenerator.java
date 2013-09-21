package com.subrosagames.subrosa.domain.player;

import org.apache.commons.lang.RandomStringUtils;

/**
 *
 */
final class PlayerCodeGenerator {

    private PlayerCodeGenerator() {
    }

    public static String generate() {
        return RandomStringUtils.randomAlphanumeric(5);
    }
}

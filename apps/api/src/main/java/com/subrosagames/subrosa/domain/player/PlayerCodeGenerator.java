package com.subrosagames.subrosa.domain.player;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Generator for player codes.
 */
final class PlayerCodeGenerator {

    private PlayerCodeGenerator() {
    }

    /**
     * Generate a player code.
     *
     * @return player code
     */
    public static String generate() {
        return RandomStringUtils.randomAlphanumeric(5);
    }
}

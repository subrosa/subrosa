package com.subrosagames.subrosa.domain.player;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link PlayerCodeGenerator}.
 */
public class PlayerCodeGeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        for (int i = 0; i < 100; ++i) {
            assertValidCode(PlayerCodeGenerator.generate());
        }
    }

    private void assertValidCode(String code) {
        Assert.assertEquals(5, code.length());
        for (char c : code.toCharArray()) {
            Assert.assertTrue(Character.isLetterOrDigit(c));
        }
    }
}

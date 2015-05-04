package com.subrosagames.subrosa.api.notification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Class for testing general notification codes.
 */
public class GeneralCodeTest {

    /**
     * Test to determine if all notification codes are unique.
     */
    @Test
    public void testUniqueCodes() {
        List<GeneralCode> codes = Arrays.asList(GeneralCode.values());
        Set<String> integerCodes = new HashSet<String>();
        for (GeneralCode code : codes) {
            String integerCode = code.getCode();
            assertFalse("Notification code " + integerCode + " is duplicated", integerCodes.contains(integerCode));
            integerCodes.add(integerCode);
        }

    }

}

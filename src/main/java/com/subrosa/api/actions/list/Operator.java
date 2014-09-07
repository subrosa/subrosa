package com.subrosa.api.actions.list;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Enumeration of supported filter operations.
 */
public enum Operator {

    // CHECKSTYLE-OFF: JavadocVariable
    EQUAL(""),
    NOT_EQUAL("Not"),
    LESS_THAN("LessThan", "Before"),
    GREATER_THAN("GreaterThan", "After"),
    CONTAINS("Contains");
    // CHECKSTYLE-ON: JavadocVariable

    private static final Map<String, Operator> SUFFIX_MAP;
    static {
        SUFFIX_MAP = new HashMap<String, Operator>();
        for (Operator operator : values()) {
            for (String indicator : operator.getSuffixes()) {
                SUFFIX_MAP.put(indicator, operator);
            }
        }
    }

    private String[] suffixes;

    Operator(String... suffixes) {
        this.suffixes = suffixes;
    }

    public String[] getSuffixes() {
        return Arrays.copyOf(suffixes, suffixes.length);
    }

    /**
     * Return the Operator that corresponds to the given suffix.
     *
     * @param suffix search key suffix
     * @return operator
     */
    public static Operator getOperatorForSuffix(String suffix) {
        return SUFFIX_MAP.get(suffix);
    }

    public static Set<String> getAllSuffixes() {
        return SUFFIX_MAP.keySet();
    }
}

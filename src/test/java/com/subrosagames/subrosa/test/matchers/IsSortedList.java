package com.subrosagames.subrosa.test.matchers;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matches a list of sorted comparable elements.
 * @param <T> list element type
 */
public final class IsSortedList<T> extends TypeSafeDiagnosingMatcher<List<T>> {

    private final boolean ascending;

    private IsSortedList(boolean ascending) {
        this.ascending = ascending;
    }

    /**
     * Factory for matching list sort in ascending order.
     * @param <T> list element type
     * @return list matcher
     */
    @Factory
    public static <T> IsSortedList isSortedAscending() {
        return new IsSortedList<T>(true);
    }

    /**
     * Factory for matching list sort in descending order.
     * @param <T> list element type
     * @return list matcher
     */
    @Factory
    public static <T> IsSortedList isSortedDescending() {
        return new IsSortedList<T>(false);
    }

    @Override
    protected boolean matchesSafely(List<T> objects, Description description) {
        boolean matches = true;
        T last = null;
        for (T object : objects) {
            if (last == null) {
                last = object;
                continue;
            }
            matches = ascending
                    ? ((Comparable<T>) object).compareTo(last) <= 0
                    : ((Comparable<T>) object).compareTo(last) >= 0;
            if (!matches) {
                description.appendText("| " + last + " and " + object + " are in wrong order ");
            }
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("list in " + (ascending ? "ascending" : "descending") + "order");
    }
}

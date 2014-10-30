package com.subrosagames.subrosa.test.matchers;

import java.util.Set;
import javax.validation.ConstraintViolation;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;

import static com.subrosagames.subrosa.test.matchers.HasConstraintViolation.IsConstraintViolation.isConstraintViolation;

/**
 * Matcher for a {@link DomainObjectValidationException} containing a specific {@link ConstraintViolation}.
 */
public final class HasConstraintViolation extends TypeSafeDiagnosingMatcher<DomainObjectValidationException> {

    private final String property;
    private final String message;

    private HasConstraintViolation(String property, String message) {
        this.property = property;
        this.message = message;
    }

    /**
     * Factory for matching a validation exception containing a specified constraint violation.
     *
     * @param property constraint violation property name
     * @param message  constraint violation message
     * @return validation exception matcher
     */
    @Factory
    public static Matcher<DomainObjectValidationException> hasConstraintViolation(String property, String message) {
        return new HasConstraintViolation(property, message);
    }

    @Override
    protected boolean matchesSafely(DomainObjectValidationException exception, Description description) {
        boolean matches = true;
        Set<? extends ConstraintViolation<?>> violations = exception.getViolations();
        if (!Matchers.hasItem(isConstraintViolation(property, message)).matches(violations)) {
            Matchers.hasItem(isConstraintViolation(property, message)).describeMismatch(violations, description);
            matches = false;
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(new StringBuilder()
                .append("has constraint violation (")
                .append(property)
                .append(" => ")
                .append(message)
                .append(")").toString());

    }

    /**
     * Matcher for a {@link ConstraintViolation}.
     */
    public static final class IsConstraintViolation extends TypeSafeDiagnosingMatcher<ConstraintViolation> {

        private final String property;
        private final String message;

        private IsConstraintViolation(String property, String message) {
            this.property = property;
            this.message = message;
        }

        /**
         * Factory for matching a constraint violation for a property and violation message.
         *
         * @param property constraint violation property name
         * @param message constraint violation message
         * @return constraint violation matcher
         */
        @Factory
        public static Matcher<ConstraintViolation> isConstraintViolation(String property, String message) {
            return new IsConstraintViolation(property, message);
        }

        @Override
        protected boolean matchesSafely(ConstraintViolation violation, Description description) {
            boolean matches = true;
            if (!property.equals(violation.getPropertyPath().toString())) {
                matches = false;
            }
            if (!message.equals(violation.getMessage())) {
                matches = false;
            }
            if (!matches) {
                description.appendText(new StringBuilder()
                        .append("| found violation (")
                        .append(violation.getPropertyPath().toString())
                        .append(" => ")
                        .append(violation.getMessage())
                        .append(") ").toString());
            }
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(new StringBuilder()
                    .append("constraint violation (")
                    .append(property)
                    .append(" => ")
                    .append(message)
                    .append(")").toString());
        }

    }

}

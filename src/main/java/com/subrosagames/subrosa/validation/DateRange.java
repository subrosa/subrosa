package com.subrosagames.subrosa.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Class-level constraint defining fields as making a valid date range.
 * <p/>
 * Both a {@link #start()} and {@link #end()} field must be specified. These fields must be {@code Date}
 * objects. If either is {@code null}, the range is considered valid. Otherwise, the start date must
 * precede the end date. Optionally, equality can be considered valid by setting {@link #allowEmptyRange()}
 * to {@code true}.
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface DateRange {

    /**
     * Validation message.
     */
    String message() default "{constraints.daterange}";

    /**
     * Validation groups.
     */
    Class<?>[] groups() default { };

    /**
     * Constraint payload.
     */
    Class<? extends Payload>[] payload() default { };

    /**
     * Start date.
     */
    String start();

    /**
     * End date.
     */
    String end();

    /**
     * Whether the start and end dates can be equal.
     */
    boolean allowEmptyRange() default false;

    /**
     * Defines several <code>@DateRange</code> annotations on the same element.
     * @see DateRange
     */
    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        DateRange[] value();
    }
}

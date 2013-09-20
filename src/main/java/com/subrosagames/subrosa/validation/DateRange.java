package com.subrosagames.subrosa.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface DateRange {

    String message() default "{constraints.daterange}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Start date.
     * @return The start date
     */
    String start();

    /**
     * End date.
     * @return The end date
     */
    String end();

    /**
     * Whether the start and end dates can be equal.
     * @return whether start and end can be equal
     */
    boolean allowEmptyRange() default false;

    /**
     * Defines several <code>@DateRange</code> annotations on the same element.
     *
     * @see DateRange
     */
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List
    {
        DateRange[] value();
    }
}

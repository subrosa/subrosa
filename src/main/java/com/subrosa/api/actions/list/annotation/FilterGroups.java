package com.subrosa.api.actions.list.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that applies query filter groupings to a model class.
 *
 * This allows for specifying a group of fields that should be queried against together using
 * an OR clause, supporting things such as searching for a term across multiple fields, or querying
 * for objects where at least one of n dates is later than a certain date.
 *
 * @see {@link FilterGroup}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface FilterGroups {

    /**
     * Configure an array of {@link FilterGroup}s to apply to the model class.
     */
    FilterGroup[] value() default { };
}

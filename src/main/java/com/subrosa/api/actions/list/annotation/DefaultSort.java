package com.subrosa.api.actions.list.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets which field in a type is sorted on by default.
 *
 * @see {@link com.subrosa.api.actions.list.Sort}.
 * @see {@link com.subrosa.api.actions.list.annotation.Sortable}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface DefaultSort {

    /**
     * Specifies the sort field that will be applied as the default sort.
     *
     * Follows the format of {@link com.subrosa.api.actions.list.Sort#setSortField(String)}.
     */
    String value() default "";
}

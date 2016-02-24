package com.subrosagames.subrosa.api.list.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets which field in a type is sorted on by default.
 *
 * @see com.subrosagames.subrosa.api.list.Sort Sort
 * @see com.subrosagames.subrosa.api.list.annotation.Sortable Sortable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface DefaultSort {

    /**
     * Specifies the sort field that will be applied as the default sort.
     * <p>
     * Follows the format of {@link com.subrosagames.subrosa.api.list.Sort#setSortField(String)}.
     */
    String value() default "";
}

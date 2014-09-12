package com.subrosa.api.actions.list.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.subrosa.api.actions.list.FilterValueTranslator;
import com.subrosa.api.actions.list.Operator;

/**
 * This annotation marks a field as usable as a filter in a query operation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Filterable {

    /**
     * Marker string that indicates the value has not been set explicitly.
     */
    String VALUE_UNSET = "##unset##";

    /**
     * Specifies how to refer to the element when building the query filter.
     * <p/>
     * Defaults to using the name of the annotated field.
     */
    String value() default VALUE_UNSET;

    /**
     * Specify a class to translate the value against which to filter into a form suitable for querying.
     * <p/>
     * Defaults to a NO-OP translation.
     */
    Class<? extends FilterValueTranslator> translator() default FilterValueTranslator.IdentityValueTranslator.class;

    /**
     * Specifies what comparison operators are supported for filtering on this field.
     * <p/>
     * Defaults to equal and not equal.
     *
     * @see com.subrosa.api.actions.list.Operator Operator
     */
    Operator[] operators() default { Operator.EQUAL, Operator.NOT_EQUAL };

    /**
     * Specifies that the operand is in fact a field on an entity rather than the field itself.
     * <p/>
     * Defaults to {@code null}, meaning to use the field itself.
     */
    String childOperand() default VALUE_UNSET;
}

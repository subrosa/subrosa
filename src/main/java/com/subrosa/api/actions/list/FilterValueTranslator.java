package com.subrosa.api.actions.list;

/**
 * Interface to implement if a filter value needs to be processed into a different form.
 *
 * For example, if a String is supplied as an query input but the persistence layer expects a different
 * type, the input can be translated to the appropriate type before the query is built.
 *
 * @param <T> input type
 * @param <U> output type
 */
public interface FilterValueTranslator<T, U> {

    /**
     * Translate the provided input object to produce one appropriate for querying against.
     *
     * @param input input object
     * @return object for use in query
     */
    U translate(T input);

    /**
     * Default NO-OP implementation of {@link FilterValueTranslator}.
     */
    class IdentityValueTranslator implements FilterValueTranslator<Object, Object> {
        @Override
        public Object translate(Object input) {
            return input;
        }
    }

    /**
     * Convenience translator that performs an Integer.valueOf(String) call.
     */
    class StringToIntegerTranslator implements FilterValueTranslator<String, Integer> {

        @Override
        public Integer translate(String input) {
            return Integer.valueOf(input);
        }
    }
}

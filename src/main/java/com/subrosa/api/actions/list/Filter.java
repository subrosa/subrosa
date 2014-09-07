package com.subrosa.api.actions.list;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang.StringUtils;

import java.util.Set;

/**
 * Represents a filter component of a query.
 */
public class Filter {

    private static final String OPERATOR_INDICATOR_REGEX = new StringBuilder()
            .append('(')
            .append(StringUtils.join(Operator.getAllSuffixes(), '|'))
            .append(")$").toString();

    private String field;
    private String queryField;
    private Operator operator;
    private Object value;
    private String filterKey;
    private FilterValueTranslator<Object, Object> translator;
    private String childOperand;

    /**
     * Construct with the given key, value and value translator with optional explicit query field.
     *
     * @param filterKey filter key
     * @param value filter value
     * @param translator filter value translator
     * @param queryField explicitly set field on which to query
     */
    public Filter(String filterKey, Object value, FilterValueTranslator<Object, Object> translator, String queryField, String childOperand) {
        field = parseFieldNameFromFilterKey(filterKey);
        this.queryField = field;
        operator = parseOperatorFromFilterKey(filterKey);
        this.value = value;
        this.filterKey = filterKey;
        this.translator = translator;
        if (queryField == null) {
            this.queryField = field;
        } else {
            this.queryField = queryField;
        }
        this.childOperand = childOperand;
    }

    /**
     * Construct with the given key and value.
     *
     * This constructs a filter that has no transformations on either the field or value used in the query.
     *
     * @param filterKey filter key
     * @param value filter value
     */
    public Filter(String filterKey, Object value) {
        this(filterKey, value, new FilterValueTranslator.IdentityValueTranslator(), null, null);
    }

    /**
     * Get the field to query on.
     *
     * @return query field
     */
    public String getQueryField() {
        return queryField;
    }

    /**
     * Get the value against which the query should be run.
     *
     * @return value after it runs through the translator
     */
    public Object getQueryValue() {
        return translator.translate(value);
    }

    private String parseFieldNameFromFilterKey(String key) {
        return key.replaceFirst(OPERATOR_INDICATOR_REGEX, "");
    }

    private Operator parseOperatorFromFilterKey(String key) {
        Set<String> suffixes = Operator.getAllSuffixes();
        // run through the possible suffixes until we find a match. note that we check for the empty suffix
        // because endsWith will always return true for an empty string. it represents the default case (equality).
        for (String suffix : suffixes) {
            if (StringUtils.isNotEmpty(suffix) && key.endsWith(suffix)) {
                return Operator.getOperatorForSuffix(suffix);
            }
        }
        return Operator.EQUAL;
    }

    @JsonValue
    public Object getValue() {
        return value;
    }

    @JsonIgnore
    public String getFilterKey() {
        return filterKey;
    }

    @JsonIgnore
    public String getField() {
        return field;
    }

    @JsonIgnore
    public Operator getOperator() {
        return operator;
    }

    @JsonIgnore
    public FilterValueTranslator<Object, Object> getTranslator() {
        return translator;
    }

    @JsonIgnore
    public String getChildOperand() {
        return childOperand;
    }

    @Override
    public String toString() {
        return String.format("[Field: %s, QueryField: %s, Operator: %s, Value: %s, FilterKey: %s]", field, queryField, operator, value, filterKey);
    }
}

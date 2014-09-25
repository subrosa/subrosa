package com.subrosa.api.actions.list;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import com.subrosa.api.actions.list.annotation.DefaultSort;
import com.subrosa.api.actions.list.annotation.FilterGroup;
import com.subrosa.api.actions.list.annotation.FilterGroups;
import com.subrosa.api.actions.list.annotation.Filterable;
import com.subrosa.api.actions.list.annotation.Sortable;

/**
 * Encapsulates the parameters that drive a query for a list of objects.
 *
 * @param <T> class annotated with filterable and sortable fields
 */
public class QueryCriteria<T> {

    private static final Logger LOG = LoggerFactory.getLogger(QueryCriteria.class);

    private List<Filter> filters = new ArrayList<Filter>();
    private Map<String, FilterGroup> filterGroups = new HashMap<String, FilterGroup>();
    private Sort sort;
    private Class<T> clazz;
    private int limit = DefaultSearchConstants.DEFAULT_LIMIT;
    private int offset = DefaultSearchConstants.DEFAULT_OFFSET;
    private boolean bypassFilterableChecks;

    /**
     * Construct for given type.
     *
     * @param clazz type
     */
    public QueryCriteria(Class<T> clazz) {
        this.clazz = clazz;

        DefaultSort defaultSort = clazz.getAnnotation(DefaultSort.class);
        if (defaultSort != null) {
            setSortForString(defaultSort.value());
        }

        FilterGroups groups = clazz.getAnnotation(FilterGroups.class);
        if (groups != null) {
            for (FilterGroup filterGroup : groups.value()) {
                filterGroups.put(filterGroup.value(), filterGroup);
            }
        }
    }

    /**
     * Return a list of the possible keys upon which filtering may occur.
     *
     * @return list of valid filter keys
     */
    public List<String> getValidFilterKeys() {
        final List<String> keys = new ArrayList<String>();
        ReflectionUtils.doWithFields(clazz,
                new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) {
                        Filterable filterable = field.getAnnotation(Filterable.class);
                        String fieldName = field.getName();
                        Operator[] operators = filterable.operators();
                        keys.addAll(getKeysForFieldAndOperators(fieldName, operators));
                    }
                },
                new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean matches(Field field) {
                        return field.getAnnotation(Filterable.class) != null;
                    }
                }
        );
        if (!CollectionUtils.isEmpty(filterGroups)) {
            for (Map.Entry<String, FilterGroup> group : filterGroups.entrySet()) {
                keys.addAll(getKeysForFieldAndOperators(group.getKey(), group.getValue().operators()));
            }
        }
        if (LOG.isDebugEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(").append(StringUtils.join(keys, "|")).append(")");
            LOG.debug("Inspecting class {} found valid filter keys: {}", clazz.getName(), stringBuilder.toString());
        }
        return keys;
    }

    private List<String> getKeysForFieldAndOperators(String fieldName, Operator[] operators) {
        List<String> keys = new ArrayList<String>();
        for (Operator operator : operators) {
            for (String indicator : operator.getSuffixes()) {
                keys.add(fieldName + indicator);
            }
        }
        return keys;
    }

    /**
     * Add a filter to the criteria.
     *
     * @param key   filter key
     * @param value filter value
     */
    public void addFilter(String key, Object value) {
        Filter filter = new Filter(key, value);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding filter to query criteria: {} {} " + value, filter.getField(), filter.getOperator());
        }

        if (filterGroups.containsKey(filter.getField())) {
            filters.add(filter);
        } else {
            filters.add(getFilterForField(filter));
        }
    }

    private Filter getFilterForField(Filter filter) {
        Field filterField = getFieldForFilter(filter);
        if (filterField == null) {
            throw new IllegalArgumentException("Field " + filter.getField() + " does not exist.");
        }

        if (bypassFilterableChecks) {
            return filter;
        }

        Filterable filterable = filterField.getAnnotation(Filterable.class);
        if (filterable == null) {
            throw new IllegalArgumentException("Filtering on field " + filter.getFilterKey() + " is not supported.");
        }

        if (!Arrays.asList(filterable.operators()).contains(filter.getOperator())) {
            throw new IllegalArgumentException("Operator " + filter.getOperator() + " on field " + filter.getField() + " is not supported.");
        }

        return getTransformedFilter(filter, filterable);
    }

    private Filter getTransformedFilter(Filter filter, Filterable filterable) {
        try {
            @SuppressWarnings("unchecked")
            FilterValueTranslator<Object, Object> translator = filterable.translator().newInstance();
            LOG.debug("Using translator of type {} for field {}", translator.getClass(), filter.getField());
            String queryField = filterable.value().equals(Filterable.VALUE_UNSET) ? null : filterable.value();
            String childOperator = filterable.childOperand().equals(Filterable.VALUE_UNSET) ? null : filterable.childOperand();
            return new Filter(filter.getFilterKey(), filter.getValue(), translator, queryField, childOperator);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Configured filter value translator " + filterable.translator() + " cannot be instantiated.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Configured filter value translator " + filterable.translator() + " cannot be instantiated.", e);
        }
    }

    private Field getFieldForFilter(Filter filter) {
        Field field;
        if (filter.getField().contains(".")) {
            field = findEmbeddedFieldForName(filter.getField());
        } else {
            field = ReflectionUtils.findField(clazz, filter.getField());
        }
        return field;
    }

    private Field findEmbeddedFieldForName(String fieldName) {
        Field field = null;
        String[] embeddedFields = fieldName.split("\\.");
        Class currentClass = clazz;
        for (String embeddedFieldName : embeddedFields) {
            field = ReflectionUtils.findField(currentClass, embeddedFieldName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Traversed into {} to find field {}", field.getName(), currentClass);
            }
            currentClass = field.getType();
        }
        return field;
    }

    /**
     * Set the sort criteria.
     *
     * @param sortString string indicating sort field and direction
     */
    public void setSort(String sortString) {
        setSortForString(sortString);
    }

    private void setSortForString(String sortString) {
        Sort sortField = new Sort(sortString);

        Field field;
        if (sortField.getField().contains(".")) {
            field = findEmbeddedFieldForName(sortField.getField());
        } else {
            field = ReflectionUtils.findField(clazz, sortField.getField());
        }
        if (field == null) {
            throw new IllegalArgumentException("Field " + sortField.getField() + " does not exist.");
        }

        Sortable sortable = field.getAnnotation(Sortable.class);
        if (sortable != null) {
            sort = sortField;
        } else {
            throw new IllegalArgumentException("Sorting on field " + sortField.getField() + " is not supported.");
        }
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public Sort getSort() {
        return sort;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Map<String, FilterGroup> getFilterGroups() {
        return filterGroups;
    }

    @Override
    public String toString() {

        List<String> filterStrings = new ArrayList<String>();
        for (Filter filter : filters) {
            filterStrings.add(filter.toString());
        }

        return String.format("Class: %s, Filters: (%s), Sort: %s, Limit: %s, Offset: %s", clazz, StringUtils.join(filterStrings, ","),
                sort, limit, offset);
    }

    public Class<T> getTargetClass() {
        return clazz;
    }

    public void setBypassFilterableChecks(boolean bypassFilterableChecks) {
        this.bypassFilterableChecks = bypassFilterableChecks;
    }
}

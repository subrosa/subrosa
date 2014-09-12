package com.subrosa.api.actions.list;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.subrosa.api.actions.list.serialization.json.FilterListJsonSerializer;
import com.subrosa.api.actions.list.serialization.xml.FilterListXmlAdapter;

/**
 * Represents query search request.
 */
@XmlRootElement(name = "query")
@XmlType(propOrder = { "filters", "sortFields", "pagination" })
@JsonPropertyOrder({ "filters", "sortFields", "pagination" })
public class Query {

    private List<Filter> filters;
    private List<Sort> sortFields;
    private Pagination pagination;

    /**
     * Default constructor.
     */
    public Query() {
    }

    /**
     * Creates a query with full arguments.
     *
     * @param filters    a filters.
     * @param sortFields list of sotrs
     * @param pagination a pagination object
     */
    public Query(List<Filter> filters, List<Sort> sortFields, Pagination pagination) {
        this.filters = filters;
        this.sortFields = sortFields;
        this.pagination = pagination;
    }

    /**
     * Gets a filters.
     *
     * @return filters the filters
     */
    @JsonSerialize(using = FilterListJsonSerializer.class)
    @XmlJavaTypeAdapter(FilterListXmlAdapter.class)
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Sets a filters.
     *
     * @param filters the filters
     */
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    /**
     * Gets a pagination.
     *
     * @return the pagination
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     * Sets a pagination.
     *
     * @param pagination the pagination
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    /**
     * Sets a sort fields.
     *
     * @return the sort fields
     */
    public List<Sort> getSortFields() {
        return sortFields;
    }

    /**
     * Set a sort fields.
     *
     * @param sortFields the sort fields
     */
    public void setSortFields(List<Sort> sortFields) {
        this.sortFields = sortFields;
    }
}

package com.subrosa.api.actions.list;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Handles the parsing of an expression of a sort for file lists.
 */
public class Sort {

    private String field;
    private boolean ascending;

    /**
     * Constructor by default.
     */
    public Sort() {}


    /**
     * Constructs the sorting with field.
     *
     * @param field the field for sorting
     */
    public Sort(String field) {
        parseSortField(field);
    }

    /**
     * Set the sort value. This parses a sort expression to determine the field and order.
     *
     * @param sort string representing a sort, such as "-createdDate"
     */
    public void setSortField(String sort) {
        parseSortField(sort);
    }

    private void parseSortField(String sort) {
        if (sort.charAt(0) == '-') {
            ascending = false;
            field = sort.substring(1);
        } else {
            ascending = true;
            field = sort;
        }
    }

    /**
     * Creates and returns a sort expression that represents the field and order.
     *
     * For example, a field of "createdDate" in descending order would return "-createdDate".
     *
     * @return sort expression
     */
    @JsonValue
    public String getSortField() {
        return (ascending ? "" : "-") + field;
    }

    @XmlTransient
    @JsonIgnore
    public boolean isAscending() {
        return ascending;
    }

    /**
     * Sets ascending.
     * @param ascending (true - up, false - down)
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    /**
     * Gets a sorting field.
     * @return the sorting field
     */
    @XmlTransient
    @JsonIgnore
    public String getField() {
        return field;
    }

    /**
     * Sets a sorting field.
     * @param field the a sorting field
     */
    public void setField(String field) {
        this.field = field;
    }
}

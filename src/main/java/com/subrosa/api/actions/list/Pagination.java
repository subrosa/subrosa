package com.subrosa.api.actions.list;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents the pagination parameters for a list response.
 */
@XmlType(propOrder = { "limit", "offset", "totalResults" })
@JsonPropertyOrder({ "limit", "offset", "totalResults" })
public class Pagination {

    private int limit;
    private int offset;
    private int totalResults;

    /**
     * Default constructor.
     */
    public Pagination() {
    }

    /**
     * Creates a pagination on the limit, offset and totalResults.
     *
     * @param limit        the limit rows on a page.
     * @param offset       the offset selection.
     * @param totalResults the total result of selected elements.
     */
    public Pagination(int limit, int offset, int totalResults) {
        this.limit = limit;
        this.offset = offset;
        this.totalResults = totalResults;
    }

    /**
     * Sets a limit.
     *
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets a limit.
     *
     * @param limit the limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Sets an offset.
     *
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets an offset.
     *
     * @param offset the offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Sets a total result of existing rows.
     *
     * @return the total result of existing rows
     */
    public int getTotalResults() {
        return totalResults;
    }

    /**
     * Gets a total result of existing rows.
     *
     * @param totalResults the total result of existing rows
     */
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}

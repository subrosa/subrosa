package com.subrosagames.subrosa.service;

import java.util.List;

/**
 * Wrapper for a paginated list of objects as a result of some query.
 * @param <T> object type
 */
public class PaginatedList<T> {

    private int limit;
    private int offset;
    private int resultCount;
    private List<? extends T> results;

    /**
     * Construct with provided results list and pagination parameters.
     * @param results results list
     * @param resultCount total results
     * @param limit number returned
     * @param offset offset into total
     */
    public PaginatedList(List<? extends T> results, int resultCount, int limit, int offset) {
        this.results = results;
        this.resultCount = resultCount;
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getResultCount() {
        return resultCount;
    }

    public List<? extends T> getResults() {
        return results;
    }

}

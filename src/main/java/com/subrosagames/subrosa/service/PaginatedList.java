package com.subrosagames.subrosa.service;

import java.util.List;

public class PaginatedList<T> {

    private int limit;
    private int offset;
    private int resultCount;
    private List<T> results;

    public PaginatedList(List<T> results, int resultCount, int limit, int offset) {
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

    public List<T> getResults() {
        return results;
    }

}

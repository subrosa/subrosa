package com.subrosa.api.actions.list;

/**
 * Represents results of query.
 */
public abstract class AbstractQueryResult {

    private Query query;

    /**
     * Gets a search query.
     * @return the search query
     */
    public Query getQuery() {
        return query;
    }

    /**
     * Sets a search query.
     * @param query the search query
     */
    public void setQuery(Query query) {
        this.query = query;
    }

}

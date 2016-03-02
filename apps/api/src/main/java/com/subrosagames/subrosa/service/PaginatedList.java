package com.subrosagames.subrosa.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Wrapper for a paginated list of objects as a result of some query.
 *
 * @param <T> object type
 */
@AllArgsConstructor
public class PaginatedList<T> {

    @Getter
    private final List<? extends T> results;
    @Getter
    private final long resultCount;
    @Getter
    private final int limit;
    @Getter
    private final int offset;

}

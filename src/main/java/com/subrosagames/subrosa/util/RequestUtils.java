package com.subrosagames.subrosa.util;

import javax.servlet.http.HttpServletRequest;

import com.subrosa.api.actions.list.QueryCriteria;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides common functionality for handling API requests.
 */
public final class RequestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUtils.class);

    private RequestUtils() { }

    /**
     * Creates and populates the criteria for a query based on the request parameters.
     *
     * Any of the valid filter keys supported by the provided type that are encountered in the request
     * parameters will be added to the criteria object.
     *
     * If any of limit, offset, or sort are supplied in the request, they too will be set on the
     * criteria object.
     *
     * @param request http servlet request
     * @param clazz type of object for which the query is being built
     * @param <T> type of object for which the query is being built
     * @return a query criteria object populated with filters
     */
    public static <T> QueryCriteria<T> createQueryCriteriaFromRequestParameters(HttpServletRequest request, Class<T> clazz) {
        QueryCriteria<T> queryCriteria = new QueryCriteria<T>(clazz);

        for (String key : queryCriteria.getValidFilterKeys()) {
            String[] parameterValues = request.getParameterValues(key);
            if (parameterValues != null && parameterValues.length > 0) {
                for (String parameter : parameterValues) {
                    LOG.debug("Adding query filter {} => {}", key, parameter);
                    if (parameter.equals(Boolean.TRUE.toString())) {
                        queryCriteria.addFilter(key, true);
                    } else if (parameter.equals(Boolean.FALSE.toString())) {
                        queryCriteria.addFilter(key, false);
                    } else {
                        queryCriteria.addFilter(key, parameter);
                    }
                }
            }
        }

        String limit = request.getParameter("limit");
        if (!StringUtils.isBlank(limit)) {
            LOG.debug("Setting a query limit of {}", limit);
            queryCriteria.setLimit(Integer.valueOf(limit));
        }
        String offset = request.getParameter("offset");
        if (!StringUtils.isBlank(offset)) {
            LOG.debug("Setting a query offset of {}", offset);
            queryCriteria.setOffset(Integer.valueOf(offset));
        }
        String sort = request.getParameter("sort");
        if (!StringUtils.isBlank(sort)) {
            LOG.debug("Setting a query sort of {}", sort);
            queryCriteria.setSort(sort);
        }

        return queryCriteria;
    }

}


package com.subrosagames.subrosa.infrastructure.persistence.hibernate.util;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for generating efficient queries.
 */
public final class QueryHelper {

    private QueryHelper() {
    }

    /**
     * Creates a query to find a model object by its id, fetching the specified expanded properties.
     * <p/>
     * Note that using this method requires that the class being queried for uses a field named {@code id}. If this is
     * not the case, use {@link #createQuery(javax.persistence.EntityManager, Class, java.util.Map, String...)} instead
     * to specify the field explicitly.
     * @param entityManager entity manager
     * @param type          model type
     * @param id            model id
     * @param expansions    fields to expand
     * @param <T>           model type
     * @return a typed query to retrieve the model
     */
    public static <T> TypedQuery<T> createQuery(EntityManager entityManager, Class<T> type, final Object id, String... expansions) {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("id", id);
        }};
        return createQuery(entityManager, type, map, expansions);
    }

    /**
     * Creates a query to find a model object by its id, fetching the specified expanded properties.
     * @param entityManager entity manager
     * @param type          model type
     * @param conditions    equality conditions to match the single result
     * @param expansions    fields to expand
     * @param <T>           model type
     * @return a typed query to retrieve the model
     */
    public static <T> TypedQuery<T> createQuery(EntityManager entityManager, Class<T> type, Map<String, Object> conditions, String... expansions) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);

        EntityGraph entityGraph = entityManager.createEntityGraph(type);
        entityGraph.addAttributeNodes(expansions);

        for (Map.Entry<String, Object> equality : conditions.entrySet()) {
            criteriaQuery.where(criteriaBuilder.equal(root.get(equality.getKey()), equality.getValue()));
        }
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(1);
        query.setHint("javax.persistence.loadgraph", entityGraph);
        return query;
    }
}

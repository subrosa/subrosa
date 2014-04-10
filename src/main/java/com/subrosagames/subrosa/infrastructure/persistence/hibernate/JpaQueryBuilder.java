package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import com.subrosa.api.actions.list.Filter;
import com.subrosa.api.actions.list.QueryBuilder;
import com.subrosa.api.actions.list.QueryCriteria;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Manages the creation of a JPA {@link TypedQuery} from a {@link QueryCriteria}.
 * <p/>
 * Requires the provision of an {@link EntityManager} instance, as it relies on on the {@link CriteriaBuilder} API
 * to create the query.
 */
public class JpaQueryBuilder<T> implements QueryBuilder<T, TypedQuery<T>, TypedQuery<Long>> {

    private EntityManager entityManager;

    /**
     * Construct the query builder with the given {@link EntityManager}.
     *
     * @param entityManager entity manager
     */
    public JpaQueryBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TypedQuery<T> getQuery(QueryCriteria<T> criteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(criteria.getTargetClass());
        Root<T> root = query.from(criteria.getTargetClass());
        query.select(root).where(getPredicate(criteria, builder, root));
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(criteria.getLimit());
        typedQuery.setFirstResult(criteria.getOffset());
        return typedQuery;
    }

    @Override
    public TypedQuery<Long> countQuery(QueryCriteria<T> criteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(criteria.getTargetClass());
        query.select(builder.count(root)).where(getPredicate(criteria, builder, root));
        return entityManager.createQuery(query);
    }

    private Predicate getPredicate(QueryCriteria<T> criteria, CriteriaBuilder builder, Root<T> root) {
        Predicate predicate = builder.conjunction();
        for (Filter filter : criteria.getFilters()) {
            Object value = filter.getTranslator().translate(filter.getValue());
            switch (filter.getOperator()) {
                case EQUAL:
                    predicate = builder.and(predicate, builder.equal(root.get(filter.getField()), value));
                    break;
                case NOT_EQUAL:
                    predicate = builder.and(predicate, builder.notEqual(root.get(filter.getField()), value));
                    break;
                case GREATER_THAN:
                    predicate = builder.and(predicate, builder.greaterThan(root.<Comparable>get(filter.getField()), (Comparable) value));
                    break;
                case LESS_THAN:
                    predicate = builder.and(predicate, builder.lessThan(root.<Comparable>get(filter.getField()), (Comparable) value));
                    break;
                default:
                    throw new IllegalStateException("Found unsupported operator " + filter.getOperator().name());
            }
        }
        return predicate;
    }
}

package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import com.subrosa.api.actions.list.Filter;
import com.subrosa.api.actions.list.QueryBuilder;
import com.subrosa.api.actions.list.QueryCriteria;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

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
        if (criteria.getLimit() > 0) {
            typedQuery.setMaxResults(criteria.getLimit());
        }
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
            Path<Comparable> path = root.get(filter.getField());
            if (filter.getChildOperand() != null) {
                path = root.join(filter.getField());
                path = path.get(filter.getChildOperand());
            }
            switch (filter.getOperator()) {
                case EQUAL:
                    predicate = builder.and(predicate, builder.equal(path, value));
                    break;
                case NOT_EQUAL:
                    predicate = builder.and(predicate, builder.notEqual(path, value));
                    break;
                case GREATER_THAN:
                    predicate = builder.and(predicate, builder.greaterThan(path, (Comparable) value));
                    break;
                case LESS_THAN:
                    predicate = builder.and(predicate, builder.lessThan(path, (Comparable) value));
                    break;
                case SET:
                    predicate = builder.and(predicate, builder.isNotNull(path));
                    break;
                case UNSET:
                    predicate = builder.and(predicate, builder.isNull(path));
                    break;
                default:
                    throw new IllegalStateException("Found unsupported operator " + filter.getOperator().name());
            }
        }
        return predicate;
    }
}

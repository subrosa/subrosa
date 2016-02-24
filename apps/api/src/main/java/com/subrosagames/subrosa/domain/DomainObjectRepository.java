package com.subrosagames.subrosa.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Extension of the paging and sorting repository interface to support the simultaneous use of
 * {@link Specification}-based querying and dynamically chosen {@link EntityGraph}s.
 *
 * @param <T> domain object type
 */
@NoRepositoryBean
public interface DomainObjectRepository<T, I extends Serializable> extends PagingAndSortingRepository<T, I> {

    /**
     * Find all entities matching the provided specs with the given entity graph enabled.
     *
     * @param spec  query specifications
     * @param hints entity graph hints
     * @return matching entities
     */
    List<T> findAll(Specification<T> spec, DomainObjectRepositoryImpl.EntityGraphHint... hints);

    List<T> findAll(Specification<T> spec, String... expansions);

    /**
     * Perform a paginated query for entities matching the provided specs with the given entity graph enabled.
     *
     * @param spec     query specifications
     * @param pageable pagination information
     * @param hints    entity graph hints
     * @return matching entities
     */
    Page<T> findAll(Specification<T> spec, Pageable pageable, DomainObjectRepositoryImpl.EntityGraphHint... hints);

    Page<T> findAll(Specification<T> spec, Pageable pageable, String... expansions);

    /**
     * Perform a sorted query for entities matching the provided specs with the given entity graph enabled.
     *
     * @param spec  query specifications
     * @param sort  sort
     * @param hints entity graph hints
     * @return matching entities
     */
    List<T> findAll(Specification<T> spec, Sort sort, DomainObjectRepositoryImpl.EntityGraphHint... hints);

    List<T> findAll(Specification<T> spec, Sort sort, String... expansions);

    /**
     * Retrieve a single entity matching the provided specs with the given entity graph enabled.
     *
     * @param spec  query specifications
     * @param hints entity graph hints
     * @return matching entity
     */
    Optional<T> findOne(Specification<T> spec, DomainObjectRepositoryImpl.EntityGraphHint... hints);

    Optional<T> findOne(Specification<T> spec, String... expansions);

    Optional<T> findOne(int id, DomainObjectRepositoryImpl.EntityGraphHint... hints);

    Optional<T> findOne(int id, String... expansions);

    EntityManager getEntityManager();

    Page<T> readPage(TypedQuery<T> query, Pageable pageable, Specification<T> spec);

    default void applyHints(TypedQuery<T> query, EntityGraphHint[] hints) {
        if (hints.length > 0) {
            EntityGraphHint hint = hints[0];
            try {
                query.setHint(hint.getType().getKey(), getEntityManager().getEntityGraph(hint.getName()));
            } catch (IllegalArgumentException e) {
                // swallow this - bad graph name given so it won't be used
            }
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class EntityGraphHint {

        @Getter
        private final EntityGraph.EntityGraphType type;
        @Getter
        private final String name;

        public static EntityGraphHint loadGraphName(String name) {
            return new EntityGraphHint(EntityGraph.EntityGraphType.LOAD, name);
        }

        public static EntityGraphHint fetchGraphName(String name) {
            return new EntityGraphHint(EntityGraph.EntityGraphType.FETCH, name);
        }
    }
}


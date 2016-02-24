package com.subrosagames.subrosa.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Provides implementation for {@link DomainObjectRepository} by building on top
 * of {@link SimpleJpaRepository} and adding query hints for entity graphs.
 */
@NoRepositoryBean
public class DomainObjectRepositoryImpl<T, I extends Serializable> extends SimpleJpaRepository<T, I> implements DomainObjectRepository<T, I> {

    private final EntityManager em;

    public DomainObjectRepositoryImpl(JpaEntityInformation<T, I> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /*
     * Here only to boost SimpleJpaRepository#readPage's access level to public.
     */
    @Override
    public Page<T> readPage(TypedQuery<T> query, Pageable pageable, Specification<T> spec) {
        return super.readPage(query, pageable, spec);
    }

    @Override
    public List<T> findAll(Specification<T> spec, EntityGraphHint... hints) {
        TypedQuery<T> query = getQuery(spec, (Sort) null);
        applyHints(query, hints);
        return query.getResultList();
    }

    @Override
    public List<T> findAll(Specification<T> spec, String... expansions) {
        return findAll(spec, Arrays.stream(expansions)
                .map(EntityGraphHint::loadGraphName)
                .toArray(EntityGraphHint[]::new));
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable, EntityGraphHint... hints) {
        TypedQuery<T> query = getQuery(spec, pageable.getSort());
        applyHints(query, hints);
        return readPage(query, pageable, spec);
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable, String... expansions) {
        return findAll(spec, pageable, Arrays.stream(expansions)
                .map(EntityGraphHint::loadGraphName)
                .toArray(EntityGraphHint[]::new));
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort, EntityGraphHint... hints) {
        TypedQuery<T> query = getQuery(spec, sort);
        applyHints(query, hints);
        return query.getResultList();
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort, String... expansions) {
        return findAll(spec, sort, Arrays.stream(expansions)
                .map(EntityGraphHint::loadGraphName)
                .toArray(EntityGraphHint[]::new));
    }

    @Override
    public Optional<T> findOne(Specification<T> spec, EntityGraphHint... hints) {
        TypedQuery<T> query = getQuery(spec, (Sort) null);
        applyHints(query, hints);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<T> findOne(Specification<T> spec, String... expansions) {
        return findOne(spec, Arrays.stream(expansions)
                .map(EntityGraphHint::loadGraphName)
                .toArray(EntityGraphHint[]::new));
    }

    @Override
    public Optional<T> findOne(int id, EntityGraphHint... hints) {
        return findOne((root, query, cb) -> cb.equal(root.get("id"), id), hints);
    }

    @Override
    public Optional<T> findOne(int id, String... expansions) {
        return findOne(id, Arrays.stream(expansions)
                .map(EntityGraphHint::loadGraphName)
                .toArray(EntityGraphHint[]::new));
    }
}

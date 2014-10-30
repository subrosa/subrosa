package com.subrosagames.subrosa.domain;

import java.util.List;

/**
 * Generic CRUD interface for domain repositories.
 *
 * @param <T> domain object type
 */
public interface DomainObjectRepository<T> {

    /**
     * Create domain object.
     *
     * @param object domain object
     * @return created object
     * @throws DomainObjectValidationException if domain object is not valid
     */
    T create(T object) throws DomainObjectValidationException;

    /**
     * List domain objects.
     *
     * @param limit      limit
     * @param offset     offset
     * @param expansions fields to expand
     * @return list of domain objects
     */
    List<T> list(int limit, int offset, String... expansions);

    /**
     * Total count of domain objects.
     *
     * @return object count
     */
    int count();

    /**
     * Get domain object.
     *
     * @param id         object id
     * @param expansions fields to expand
     * @return domain object
     * @throws DomainObjectNotFoundException if object is not found
     */
    T get(int id, String... expansions) throws DomainObjectNotFoundException;

    /**
     * Update domain object.
     *
     * @param object domain object
     * @return updated object
     * @throws DomainObjectNotFoundException   is object is not found
     * @throws DomainObjectValidationException if object is not valid
     */
    T update(T object) throws DomainObjectNotFoundException, DomainObjectValidationException;

}


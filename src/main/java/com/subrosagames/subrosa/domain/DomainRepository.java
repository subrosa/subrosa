package com.subrosagames.subrosa.domain;

import java.util.List;

/**
 * Generic CRUD interface for domain repositories.
 */
public interface DomainRepository<T> {

    T create(T object) throws DomainObjectValidationException;

    List<T> list(int limit, int offset, String... expansions);

    int count();

    T get(int id, String... expansions) throws DomainObjectNotFoundException;

    T update(T object) throws DomainObjectNotFoundException, DomainObjectValidationException;
}

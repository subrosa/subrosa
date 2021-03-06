package com.subrosagames.subrosa.domain;

/**
 * Interface for a domain object factory.
 *
 * @param <T> domain object type
 */
public interface DomainObjectFactory<T> {

    /**
     * Inject dependencies into the domain object.
     *
     * @param object domain object
     * @return domain object with dependencies injected
     */
    T injectDependencies(T object);

}

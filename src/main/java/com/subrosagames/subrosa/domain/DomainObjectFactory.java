package com.subrosagames.subrosa.domain;

/**
 * Interface for a domain object factory.
 */
public interface DomainObjectFactory<T> {

    /**
     * Inject dependencies into the domain object.
     * @param object domain object
     */
    void injectDependencies(T object);
}

package com.subrosagames.subrosa.domain;

/**
 */
public interface DomainObjectFactory<T> {

    void injectDependencies(T object);
}

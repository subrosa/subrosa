package com.subrosagames.subrosa.domain;

import java.lang.reflect.InvocationTargetException;
import com.subrosagames.subrosa.util.bean.OptionalAwareBeanUtilsBean;

/**
 * Base class for domain object factories.
 */
public abstract class BaseDomainObjectFactory {

    /**
     * Copies properties from a data transfer object into a domain object.
     *
     * @param dto    data transfer object
     * @param entity domain object
     */
    protected void copyProperties(Object dto, Object entity) {
        OptionalAwareBeanUtilsBean beanCopier = new OptionalAwareBeanUtilsBean();
        try {
            beanCopier.copyProperties(entity, dto);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}

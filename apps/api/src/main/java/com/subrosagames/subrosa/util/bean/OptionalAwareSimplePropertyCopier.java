package com.subrosagames.subrosa.util.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * Bean copier that skips null fields.
 */
public class OptionalAwareSimplePropertyCopier extends BeanUtilsBean {

    @Override
    public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        if (value != null && !(value instanceof Collection)) {
            if (value instanceof Optional) {
                super.copyProperty(dest, name, ((Optional) value).orElse(null));
            } else {
                super.copyProperty(dest, name, value);
            }
        }
    }
}

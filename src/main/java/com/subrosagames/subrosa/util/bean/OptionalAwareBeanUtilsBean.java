package com.subrosagames.subrosa.util.bean;

import java.lang.reflect.InvocationTargetException;

import com.google.common.base.Optional;
import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * Bean copier that skips null fields.
 */
public class OptionalAwareBeanUtilsBean extends BeanUtilsBean {

    @Override
    public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        if (value != null) {
            if (value instanceof Optional) {
                super.copyProperty(dest, name, ((Optional) value).orNull());
            } else {
                super.copyProperty(dest, name, value);
            }
        }
    }
}

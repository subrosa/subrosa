package com.subrosagames.subrosa.util.bean;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * Bean copier that skips null fields.
 */
public class NullAwareBeanUtilsBean extends BeanUtilsBean {

    @Override
    public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        if (value == null) {
            return;
        }
        super.copyProperty(dest, name, value);
    }
}

package com.subrosagames.subrosa.util.bean;

import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Overrides {@link ObjectMapper} to register the {@link Hibernate4Module} module.
 */
public class HibernateAwareObjectMapper extends ObjectMapper implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        registerModule(new Hibernate4Module());
    }
}

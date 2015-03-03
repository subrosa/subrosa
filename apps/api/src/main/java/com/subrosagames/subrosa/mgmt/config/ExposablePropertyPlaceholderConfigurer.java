package com.subrosagames.subrosa.mgmt.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Property placeholder configurer that keeps track of configured properties so they can be retrieved.
 * <p/>
 * TODO - uses deprecated configuration method - use PropertySourcesPlaceholderConfigurer instead
 */
public class ExposablePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private Map<String, String> resolvedProperties;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties properties) {
        super.processProperties(beanFactory, properties);

        resolvedProperties = new HashMap<String, String>();
        for (Object key : properties.keySet()) {
            String keyStr = key.toString();
            resolvedProperties.put(keyStr, resolvePlaceholder(keyStr, properties));
        }
    }

    /**
     * Gets the resolved properties as an unmodifiable map.
     *
     * @return resolved properties
     */
    public Map<String, String> getResolvedProperties() {
        return Collections.unmodifiableMap(resolvedProperties);
    }

}

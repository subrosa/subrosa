package com.subrosagames.subrosa.security;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of 3rd party providers for user connections.
 */
public enum ConnectionProvider {
    /**
     * Facebook.
     */
    FACEBOOK("facebook");

    private static final Map<String, ConnectionProvider> PROVIDER_NAME_MAP;

    static {
        PROVIDER_NAME_MAP = new HashMap<>(ConnectionProvider.values().length);
        for (ConnectionProvider provider : ConnectionProvider.values()) {
            PROVIDER_NAME_MAP.put(provider.getProviderName(), provider);
        }
    }

    private final String providerName;

    /**
     * Construct a provider name.
     *
     * @param providerName provider name
     */
    ConnectionProvider(String providerName) {
        this.providerName = providerName;
    }

    /**
     * Get the provider for the given provider name.
     *
     * @param name provider name
     * @return provider
     */
    public static ConnectionProvider forProviderName(String name) {
        return PROVIDER_NAME_MAP.get(name);
    }

    /**
     * Get the provider name for the provider.
     *
     * @return provider name
     */
    public String getProviderName() {
        return providerName;
    }
}


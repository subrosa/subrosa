package com.subrosagames.subrosa.domain.account;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Persisted user connection.
 */
@Entity
@Table(name = "user_connection")
public class UserConnection {

    @Id
    @SequenceGenerator(name = "userConnectionSeq", sequenceName = "user_connection_user_connection_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userConnectionSeq")
    @Column(name = "user_connection_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "provider_id")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_user_id")
    private String providerUserId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "secret")
    private String secret;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Enumeration of 3rd party providers for user connections.
     */
    public static enum Provider {
        /**
         * Facebook.
         */
        FACEBOOK("facebook");

        private static final Map<String, Provider> PROVIDER_NAME_MAP;

        static {
            PROVIDER_NAME_MAP = new HashMap<>(Provider.values().length);
            for (Provider provider : Provider.values()) {
                PROVIDER_NAME_MAP.put(provider.getProviderName(), provider);
            }
        }

        private final String providerName;

        /**
         * Construct a provider name.
         *
         * @param providerName provider name
         */
        Provider(String providerName) {
            this.providerName = providerName;
        }

        /**
         * Get the provider for the given provider name.
         *
         * @param name provider name
         * @return provider
         */
        public static Provider forProviderName(String name) {
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

}

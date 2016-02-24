package com.subrosagames.gateway.auth;

import java.util.List;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.MultiValueMap;

import com.subrosagames.subrosa.security.ConnectionProvider;

/**
 * JPA-based repository for social user connections.
 */
public class JpaConnectionRepository implements ConnectionRepository {

    private SocialUserRepository socialUserRepository;
    private Account account;

    /**
     * Construct with the given repositories and account.
     *
     * @param socialUserRepository user connection repository
     * @param account              account
     */
    public JpaConnectionRepository(SocialUserRepository socialUserRepository, Account account)
    {
        this.socialUserRepository = socialUserRepository;
        this.account = account;
    }

    @Override
    public void addConnection(Connection<?> connection) {
        ConnectionData data = connection.createData();
        UserConnection userConnection = new UserConnection();
        userConnection.setAccount(account);
        userConnection.setProviderUserId(data.getProviderUserId());
        userConnection.setProvider(ConnectionProvider.forProviderName(data.getProviderId()));
        userConnection.setAccessToken(data.getAccessToken());
        userConnection.setRefreshToken(data.getRefreshToken());
        userConnection.setSecret(data.getSecret());
        socialUserRepository.create(userConnection);
    }

    @Override
    public MultiValueMap<String, Connection<?>> findAllConnections() {
        return null;
    }

    @Override
    public List<Connection<?>> findConnections(String providerId) {
        return null;
    }

    @Override
    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
        return null;
    }

    @Override
    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
        return null;
    }

    @Override
    public Connection<?> getConnection(ConnectionKey connectionKey) {
        return null;
    }

    @Override
    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
        return null;
    }

    @Override
    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
        return null;
    }

    @Override
    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
        return null;
    }

    @Override
    public void updateConnection(Connection<?> connection) {

    }

    @Override
    public void removeConnections(String providerId) {

    }

    @Override
    public void removeConnection(ConnectionKey connectionKey) {

    }
}

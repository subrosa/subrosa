package com.subrosagames.subrosa.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Sets;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.repository.AccountRepository;
import com.subrosagames.subrosa.domain.account.UserConnection;

/**
 * Jpa implementation of a users connection repository.
 */
public class JpaUsersConnectionRepository implements UsersConnectionRepository {

    private final SocialUserRepository socialUserRepository;
    private final AccountRepository accountRepository;

    /**
     * Construct with the given repositories.
     *
     * @param socialUserRepository user connection repository
     * @param accountRepository    account repository
     */
    public JpaUsersConnectionRepository(SocialUserRepository socialUserRepository, AccountRepository accountRepository) {
        this.socialUserRepository = socialUserRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Find User with the Connection profile (providerId and providerUserId).
     * If this is the first connection attempt there will be nor User so create one and persist the Connection information.
     * In reality there will only be one User associated with the Connection.
     *
     * @param connection social connection
     * @return List of User Ids (see User.getUuid())
     */
    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        List<String> userIds = new ArrayList<>();
        ConnectionKey key = connection.getKey();
        List<UserConnection> users = socialUserRepository.findByProviderAndProviderUserId(key.getProviderId(), key.getProviderUserId());
        if (!users.isEmpty()) {
            userIds.addAll(getUserIdsForUsers(users));
        } else {
            userIds.add(createUserForConnection(connection));
        }
        return userIds;
    }

    private String createUserForConnection(Connection<?> connection) {
        String userId;
        UserProfile profile = connection.fetchUserProfile();
        Account account = findUserFromSocialProfile(profile);
        if (account == null) {
            Account newAccount = new Account();
            newAccount.setEmail(profile.getEmail());
            newAccount.setUsername(profile.getUsername());
            // accounts created via a 3rd party integration can be considered vetted
            newAccount.setActivated(true);
            // need an empty (but non-null) password - if the user wants to set a password later they can via "forgot password"
            newAccount.setPassword("");
            accountRepository.save(newAccount);
            userId = newAccount.getEmail();
        } else {
            userId = account.getEmail();
        }
        //persist the Connection
        createConnectionRepository(userId).addConnection(connection);
        return userId;
    }

    List<String> getUserIdsForUsers(List<UserConnection> users) {
        List<String> userIds = new ArrayList<>(users.size());
        for (UserConnection user : users) {
            userIds.add(user.getAccount().getEmail());
        }
        return userIds;
    }

    private Account findUserFromSocialProfile(UserProfile profile) {
        if (profile != null && StringUtils.hasText(profile.getEmail())) {
            return accountRepository.findOneByEmail(profile.getEmail()).orElse(null);
        }
        return null;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        return Sets.newHashSet(socialUserRepository.userIdsByProviderAndProviderUserId(providerId, providerUserIds));
    }

    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        Account account = accountRepository.findOneByEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not Found"));
        return new JpaConnectionRepository(socialUserRepository, account);
    }
}

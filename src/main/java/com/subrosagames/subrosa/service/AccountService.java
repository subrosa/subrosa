package com.subrosagames.subrosa.service;

import java.util.List;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.subrosagames.subrosa.api.dto.PlayerProfileDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;

/**
 * Service layer for account operations.
 */
@Service
public class AccountService {

    @Autowired
    private AccountFactory accountFactory;

    /**
     * Get specified account.
     *
     * @param accountId account id
     * @param expand    field expansions
     * @return account
     * @throws AccountNotFoundException if account does not exist
     */
    @PreAuthorize("hasPermission(#accountId, 'Account', 'READ_ACCOUNT')")
    public Account getAccount(Integer accountId, String... expand) throws AccountNotFoundException {
        return accountFactory.getAccount(accountId, expand);
    }

    /**
     * Get player profiles for account.
     *
     * @param accountId account id
     * @param limit     limit
     * @param offset    offset
     * @return list of players
     * @throws AccountNotFoundException if account is not found
     */
    @Transactional
    @PreAuthorize("hasPermission(#accountId, 'Account', 'READ_ACCOUNT')")
    public PaginatedList<PlayerProfile> listPlayerProfiles(Integer accountId, int limit, int offset) throws AccountNotFoundException {
        Account account = accountFactory.getAccount(accountId);
        List<PlayerProfile> playerProfiles = account.getPlayerProfiles();
        if (CollectionUtils.isEmpty(playerProfiles)) {
            return new PaginatedList<>(Lists.<PlayerProfile>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<>(
                    playerProfiles.subList(offset, Math.min(playerProfiles.size(), offset + limit)),
                    playerProfiles.size(), limit, offset);
        }
    }

    /**
     * Get player profile for account.
     *
     * @param accountId       account id
     * @param playerProfileId player id
     * @return player
     * @throws AccountNotFoundException       if account is not found
     * @throws PlayerProfileNotFoundException if player profile is not found
     */
    @Transactional
    @PreAuthorize("hasPermission(#accountId, 'Account', 'READ_ACCOUNT')")
    public PlayerProfile getPlayerProfile(int accountId, int playerProfileId) throws AccountNotFoundException, PlayerProfileNotFoundException {
        Account account = accountFactory.getAccount(accountId);
        return account.getPlayerProfile(playerProfileId);
    }

    /**
     * Create a player profile.
     *
     * @param accountId               account id
     * @param playerProfileDescriptor player profile information
     * @return created player profile
     * @throws AccountNotFoundException         if account is not found
     * @throws ImageNotFoundException           if image is not found
     * @throws PlayerProfileValidationException if player profile is not valid for creation
     */
    @Transactional
    @PreAuthorize("hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public PlayerProfile createPlayerProfile(int accountId, PlayerProfileDescriptor playerProfileDescriptor)
            throws AccountNotFoundException, ImageNotFoundException, PlayerProfileValidationException
    {
        Account account = accountFactory.getAccount(accountId);
        return account.createPlayerProfile(playerProfileDescriptor);
    }

    /**
     * Update a player profile.
     *
     * @param accountId               account id
     * @param playerId                player id
     * @param playerProfileDescriptor player profile information
     * @return updated player profile
     * @throws AccountNotFoundException         if account is not found
     * @throws ImageNotFoundException           if image is not found
     * @throws PlayerProfileNotFoundException   if player profile is not found
     * @throws PlayerProfileValidationException if player profile is not valid for saving
     */
    @Transactional
    @PreAuthorize("hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public PlayerProfile updatePlayerProfile(int accountId, int playerId, PlayerProfileDescriptor playerProfileDescriptor)
            throws AccountNotFoundException, PlayerProfileNotFoundException, ImageNotFoundException, PlayerProfileValidationException
    {
        Account account = accountFactory.getAccount(accountId);
        return account.updatePlayerProfile(playerId, playerProfileDescriptor);
    }

    /**
     * Delete a player profile.
     *
     * @param accountId account id
     * @param playerId  player id
     * @return deleted player profile
     * @throws AccountNotFoundException       if account is not found
     * @throws PlayerProfileNotFoundException if player profile is not found
     */
    @Transactional
    @PreAuthorize("hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public PlayerProfile deletePlayerProfile(int accountId, int playerId) throws AccountNotFoundException, PlayerProfileNotFoundException {
        Account account = accountFactory.getAccount(accountId);
        return account.deletePlayerProfile(playerId);
    }
}

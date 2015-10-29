package com.subrosagames.subrosa.service;

import java.util.List;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.api.dto.AddressDescriptor;
import com.subrosagames.subrosa.api.dto.PlayerProfileDescriptor;
import com.subrosagames.subrosa.bootstrap.AmqpConfiguration;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.AddressValidationException;
import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.account.PlayerProfileInUseException;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;

/**
 * Service layer for account operations.
 */
@Transactional
@Service
public class AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountFactory accountFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
     * List accounts.
     *
     * @param limit      limit
     * @param offset     offset
     * @param expansions field expansions
     * @return list of accounts
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Account> listAccounts(int limit, int offset, String... expansions) {
        return accountFactory.getAccounts(limit, offset, expansions);
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
     * @throws PlayerProfileInUseException    if player profile is in use
     */
    @PreAuthorize("hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public PlayerProfile deletePlayerProfile(int accountId, int playerId)
            throws AccountNotFoundException, PlayerProfileNotFoundException, PlayerProfileInUseException
    {
        Account account = accountFactory.getAccount(accountId);
        return account.deletePlayerProfile(playerId);
    }

    /**
     * Get paginated list of addresses.
     *
     * @param accountId account id
     * @param limit     number of results
     * @param offset    offset into results
     * @return list of addresses
     * @throws AccountNotFoundException if account is not found
     */
    @PreAuthorize("hasPermission(#accountId, 'Account', 'READ_ACCOUNT')")
    public PaginatedList<Address> listAddresses(int accountId, int limit, int offset) throws AccountNotFoundException {
        Account account = accountFactory.getAccount(accountId);
        List<Address> addresses = account.getAddresses();
        if (CollectionUtils.isEmpty(addresses)) {
            return new PaginatedList<>(Lists.<Address>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<>(
                    addresses.subList(offset, Math.min(addresses.size(), offset + limit)),
                    addresses.size(), limit, offset);
        }
    }

    /**
     * Get an account address.
     *
     * @param accountId account id
     * @param addressId address id
     * @return address
     * @throws AccountNotFoundException if account is not found
     * @throws AddressNotFoundException if address is not found
     */
    @PreAuthorize("hasPermission(#accountId, 'Account', 'READ_ACCOUNT')")
    public Address getAddress(int accountId, int addressId) throws AccountNotFoundException, AddressNotFoundException {
        Account account = accountFactory.getAccount(accountId);
        return account.getAddress(addressId);
    }

    /**
     * Create an address.
     *
     * @param accountId         account id
     * @param addressDescriptor account information
     * @return created account
     * @throws AccountNotFoundException   if account is not found
     * @throws AddressValidationException if address is invalid
     */
    @PreAuthorize("hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public Address createAddress(int accountId, AddressDescriptor addressDescriptor)
            throws AccountNotFoundException, AddressValidationException
    {
        Account account = accountFactory.getAccount(accountId);
        return account.createAddress(addressDescriptor);
    }

    /**
     * Update an address.
     *
     * @param accountId         account id
     * @param addressId         address id
     * @param addressDescriptor address information
     * @return updated address
     * @throws AccountNotFoundException   if account is not found
     * @throws AddressValidationException if address is invalid
     * @throws AddressNotFoundException   if address is not found
     */
    @PreAuthorize("hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public Address updateAddress(int accountId, int addressId, AddressDescriptor addressDescriptor)
            throws AccountNotFoundException, AddressValidationException, AddressNotFoundException
    {
        Account account = accountFactory.getAccount(accountId);
        return account.updateAddress(addressId, addressDescriptor);
    }

    /**
     * Delete an address.
     *
     * @param accountId account id
     * @param addressId address id
     * @return deleted address
     * @throws AccountNotFoundException if account is not found
     * @throws AddressNotFoundException if address is not found
     */
    @PreAuthorize("hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public Address deleteAddress(int accountId, int addressId) throws AccountNotFoundException, AddressNotFoundException {
        Account account = accountFactory.getAccount(accountId);
        return account.deleteAddress(addressId);
    }

    /**
     * Find the specified address and trigger a reprocessing for it.
     *
     * @param accountId account id
     * @param addressId address id
     * @return address
     * @throws AddressNotFoundException if address is not found
     * @throws AccountNotFoundException if account is not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Address reprocessAddress(int accountId, int addressId) throws AddressNotFoundException, AccountNotFoundException {
        Address address = getAddress(accountId, addressId);
        notifyOfNewUserAddress(address);
        return address;
    }

    /**
     * Queues a message for processing given address.
     *
     * @param address address
     */
    // TODO configure aspectj for aop so that this can be private/called from within this class
    public void notifyOfNewUserAddress(Address address) {
        if (address.getId() == null) {
            LOG.error("Attempted to queue user address message with no address id! - account {}: {}", address.getAccount().getId(), address.getFullAddress());
            return;
        }
        try {
            rabbitTemplate.convertAndSend(AmqpConfiguration.QueueName.USER_ADDRESS,
                    objectMapper.writeValueAsString(new UserAddressNotification(address.getId())));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to convert address into JSON. Should never happen.", e);
        }
    }

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}

package com.subrosagames.subrosa.domain.account;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.hibernate.Hibernate;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfiles;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.orm.jpa.JpaSystemException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.api.dto.AccountDescriptor;
import com.subrosagames.subrosa.api.dto.PlayerProfileDescriptor;
import com.subrosagames.subrosa.domain.PermissionTarget;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.token.Token;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.domain.token.TokenInvalidException;
import com.subrosagames.subrosa.domain.token.TokenType;
import com.subrosagames.subrosa.util.bean.OptionalAwareSimplePropertyCopier;

/**
 * Represents an account in the Subrosa application.
 */
@Entity
@Table(name = "account")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@FetchProfiles({
        @FetchProfile(name = "addresses", fetchOverrides = {
                @FetchProfile.FetchOverride(entity = Account.class, association = "addresses", mode = FetchMode.JOIN)
        }),
        @FetchProfile(name = "images", fetchOverrides = {
                @FetchProfile.FetchOverride(entity = Account.class, association = "images", mode = FetchMode.JOIN)
        })
})
public class Account implements PermissionTarget {

    @JsonIgnore
    @Transient
    private AccountRepository accountRepository;
    @JsonIgnore
    @Transient
    private AccountFactory accountFactory;
    @JsonIgnore
    @Transient
    private TokenFactory tokenFactory;

    @Id
    @SequenceGenerator(name = "accountSeq", sequenceName = "account_account_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSeq")
    @Column(name = "account_id")
    private Integer id;

    @Column
    private Boolean activated;

    @Column
    private String username;

    @Column
    private String name;

    @Column(length = 320, unique = true)
    @NotNull
    @NotBlank
    @Email
    private String email;

    @Column(name = "cell_phone")
    private String cellPhone;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @ElementCollection(targetClass = AccountRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;

    @JsonIgnore
    @Column(length = 256)
    private String password;

    @OneToMany
    @JoinTable(
            name = "account_address",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    @MapKey(name = "addressType")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<AddressType, Address> addresses;

    @JsonIgnore
    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderColumn(name = "index")
    private List<Image> images = Lists.newArrayList();

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = { CascadeType.PERSIST })
    private List<PlayerProfile> playerProfiles = Lists.newArrayList();

    @Column(name = "last_logged_in")
    private Date lastLoggedIn;

    /**
     * Get accolades for this account.
     *
     * @return list of accolades
     */
    public List<Accolade> getAccolades() {
        return new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public Date getDateOfBirth() {
        return dateOfBirth == null ? null : new Date(dateOfBirth.getTime());
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth == null ? null : new Date(dateOfBirth.getTime());
    }


    public Set<AccountRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AccountRole> accountRoles) {
        this.roles = accountRoles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastLoggedIn() {
        return lastLoggedIn == null ? null : new Date(lastLoggedIn.getTime());
    }

    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn == null ? null : new Date(lastLoggedIn.getTime());
    }

    /**
     * Get addresses if they have been loaded from the database.
     *
     * @return addresses if loaded or {@code null}
     */
    public Map<AddressType, Address> getAddresses() {
        if (!Hibernate.isInitialized(addresses)) {
            return null;
        }
        return addresses;
    }

    /**
     * Set addresses.
     *
     * @param addresses addresses
     */
    public void setAddresses(Map<AddressType, Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * Get images if they have been loaded from the database.
     *
     * @return images if loaded or {@code null}
     */
    public List<Image> getImages() {
        return images;
    }

    /**
     * Get address of the specified type.
     *
     * @param addressType address type
     * @return address
     */
    public Address getAddress(AddressType addressType) {
        return addresses.get(addressType);
    }

    /**
     * Create an account with the specified password.
     *
     * @param userPassword password
     * @return created account
     * @throws AccountValidationException if account is invalid for creation
     */
    public Account create(String userPassword) throws AccountValidationException {
        assertValid();
        try {
            accountRepository.create(this, userPassword);
        } catch (JpaSystemException e) {
            if (isEmailConflict(e)) {
                throw new EmailConflictException("Email " + getEmail() + " already in use.", e);
            }
            throw e;
        }
        accountFactory.injectDependencies(this);
        return this;
    }

    /**
     * Update this account with the given account information.
     *
     * @param accountDescriptor account information
     * @return updated account
     * @throws AccountValidationException if account is invalid for saving
     */
    public Account update(AccountDescriptor accountDescriptor) throws AccountValidationException {
        String originalEmail = getEmail();

        importDescriptorValues(accountDescriptor);

        if (!originalEmail.equals(getEmail())) {
            setActivated(false);
        }
        return performUpdate();
    }

    private void importDescriptorValues(AccountDescriptor accountDescriptor) {
        // read-only fields
        // TODO specify R/O fields via an annotation
        accountDescriptor.setId(getId());
        accountDescriptor.setActivated(Optional.of(isActivated()));
        OptionalAwareSimplePropertyCopier beanCopier = new OptionalAwareSimplePropertyCopier();
        try {
            beanCopier.copyProperties(this, accountDescriptor);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private Account performUpdate() throws AccountValidationException {
        assertValid();
        try {
            accountRepository.update(this);
        } catch (AccountNotFoundException e) {
            throw new IllegalStateException("This should never happen - was the id of this object modified?", e);
        } catch (JpaSystemException e) {
            if (isEmailConflict(e)) {
                throw new EmailConflictException("Email " + getEmail() + " already in use.", e);
            }
            throw e;
        }
        return this;
    }

    /**
     * Activates account using the given token.
     *
     * @param token activation token
     * @throws TokenInvalidException      if token is not valid for activation
     * @throws AccountValidationException if account is not valid for activation
     */
    public void activate(String token) throws TokenInvalidException, AccountValidationException {
        Token retrieved = tokenFactory.findToken(token, TokenType.EMAIL_VALIDATION);
        if (retrieved == null || !getId().equals(retrieved.getOwner())) {
            throw new TokenInvalidException("Token does not exist");
        }
        setActivated(true);
        performUpdate();
    }

    /**
     * Add image to account images.
     *
     * @param image image
     */
    public void addImage(Image image) {
        images.add(image);
    }

    /**
     * Get specified account image.
     *
     * @param imageId image id
     * @return account image
     * @throws ImageNotFoundException if image does not exist
     */
    public Image getImage(int imageId) throws ImageNotFoundException {
        return accountRepository.getImage(this, imageId);
    }

    private boolean isEmailConflict(JpaSystemException e) {
        String message = e.getMostSpecificCause().getMessage();
        return message.contains("unique constraint");
    }

    /**
     * Get player profiles.
     *
     * @return player profiles
     */
    public List<PlayerProfile> getPlayerProfiles() {
        return playerProfiles;
    }

    /**
     * Add player profile.
     *
     * @param playerProfile player profile
     */
    public void addPlayerProfile(PlayerProfile playerProfile) {
        playerProfiles.add(playerProfile);
    }

    /**
     * Get the specified player profile.
     *
     * @param playerId player profile id
     * @return player profile
     * @throws PlayerProfileNotFoundException if player profile does not exist
     */
    public PlayerProfile getPlayerProfile(int playerId) throws PlayerProfileNotFoundException {
        return accountRepository.getPlayerProfile(this, playerId);
    }

    /**
     * Create a new player profile for this account.
     *
     * @param playerProfileDescriptor player profile information
     * @return created player profile
     * @throws ImageNotFoundException           if specified image is not found
     * @throws PlayerProfileValidationException if player profile is not valid for creation
     */
    public PlayerProfile createPlayerProfile(PlayerProfileDescriptor playerProfileDescriptor)
            throws ImageNotFoundException, PlayerProfileValidationException
    {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setAccount(this);
        copyPlayerProfileProperties(playerProfileDescriptor, playerProfile);
        assertPlayerProfileValid(playerProfile);
        addPlayerProfile(playerProfile);
        return playerProfile;
    }

    void assertPlayerProfileValid(PlayerProfile playerProfile) throws PlayerProfileValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PlayerProfile>> violations = validator.validate(playerProfile);
        if (!violations.isEmpty()) {
            throw new PlayerProfileValidationException(violations);
        }
    }

    /**
     * Delete the specified player profile.
     *
     * @param playerId player profile id
     * @return deleted player profile
     * @throws PlayerProfileNotFoundException if player profile does not exist
     */
    public PlayerProfile deletePlayerProfile(int playerId) throws PlayerProfileNotFoundException {
        PlayerProfile playerProfile = getPlayerProfile(playerId);
        accountRepository.delete(playerProfile);
        return playerProfile;
    }

    /**
     * Update the specified player profile.
     *
     * @param playerId                player profile id
     * @param playerProfileDescriptor player profile information
     * @return updated player profile
     * @throws PlayerProfileNotFoundException   if player profile does not exist
     * @throws ImageNotFoundException           if specified image is not found
     * @throws PlayerProfileValidationException if player profile is not valid for saving
     */
    public PlayerProfile updatePlayerProfile(int playerId, PlayerProfileDescriptor playerProfileDescriptor)
            throws PlayerProfileNotFoundException, ImageNotFoundException, PlayerProfileValidationException
    {
        PlayerProfile playerProfile = getPlayerProfile(playerId);
        copyPlayerProfileProperties(playerProfileDescriptor, playerProfile);
        assertPlayerProfileValid(playerProfile);
        return playerProfile;
    }

    void copyPlayerProfileProperties(PlayerProfileDescriptor playerProfileDescriptor, PlayerProfile playerProfile) throws ImageNotFoundException {
        if (playerProfileDescriptor.getName() != null) {
            playerProfile.setName(playerProfileDescriptor.getName().orNull());
        }
        if (playerProfileDescriptor.getImageId() != null) {
            if (playerProfileDescriptor.getImageId().isPresent()) {
                playerProfile.setImage(getImage(playerProfileDescriptor.getImageId().get()));
            } else {
                playerProfile.setImage(null);
            }
        }
    }

    void assertValid(Class... validationGroups) throws AccountValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Account>> violations = validator.validate(this, validationGroups);
        if (!violations.isEmpty()) {
            throw new AccountValidationException(violations);
        }
    }

    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void setAccountFactory(AccountFactory accountFactory) {
        this.accountFactory = accountFactory;
    }

    public void setTokenFactory(TokenFactory tokenFactory) {
        this.tokenFactory = tokenFactory;
    }

}

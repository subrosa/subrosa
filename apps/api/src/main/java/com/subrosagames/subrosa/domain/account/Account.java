package com.subrosagames.subrosa.domain.account;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfiles;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.api.dto.AccountDescriptor;
import com.subrosagames.subrosa.api.dto.AddressDescriptor;
import com.subrosagames.subrosa.api.dto.PlayerProfileDescriptor;
import com.subrosagames.subrosa.domain.PermissionTarget;
import com.subrosagames.subrosa.domain.account.repository.AccountRepository;
import com.subrosagames.subrosa.domain.account.repository.PlayerProfileRepository;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.image.repository.ImageRepository;
import com.subrosagames.subrosa.domain.token.Token;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.domain.token.TokenInvalidException;
import com.subrosagames.subrosa.domain.token.TokenType;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;
import com.subrosagames.subrosa.security.PasswordUtility;
import com.subrosagames.subrosa.util.bean.OptionalAwareSimplePropertyCopier;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an account in the Subrosa application.
 */
@Entity
@Table(name = "account")
@FetchProfiles({
        @FetchProfile(name = "addresses", fetchOverrides = {
                @FetchProfile.FetchOverride(entity = Account.class, association = "addresses", mode = FetchMode.JOIN)
        }),
        @FetchProfile(name = "images", fetchOverrides = {
                @FetchProfile.FetchOverride(entity = Account.class, association = "images", mode = FetchMode.JOIN)
        })
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account extends BaseEntity implements PermissionTarget {

    private static final Logger LOG = LoggerFactory.getLogger(Account.class);

    @JsonIgnore
    @Transient
    @Setter
    private AccountRepository accountRepository;
    @JsonIgnore
    @Transient
    @Setter
    private AccountFactory accountFactory;
    @JsonIgnore
    @Transient
    @Setter
    private TokenFactory tokenFactory;
    @JsonIgnore
    @Transient
    @Setter
    private ImageRepository imageRepository;
    @JsonIgnore
    @Transient
    @Setter
    private PlayerProfileRepository playerProfileRepository;
    @JsonIgnore
    @Transient
    @Setter
    private PasswordUtility passwordUtility;

    @Id
    @SequenceGenerator(name = "accountSeq", sequenceName = "account_account_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSeq")
    @Column(name = "account_id")
    @Getter
    @Setter
    private Integer id;

    @Column
    @Getter
    @Setter
    private Boolean activated;

    @Column
    @Getter
    @Setter
    private String username;

    @Column
    @Getter
    @Setter
    private String name;

    @Column(length = 320, unique = true)
    @NotNull
    @NotBlank
    @Email
    @Getter
    @Setter
    private String email;

    @Column(name = "cell_phone")
    @Getter
    @Setter
    private String cellPhone;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    // TODO use immutable java 8 temporal types
    private Date dateOfBirth;

    @ElementCollection(targetClass = AccountRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Set<AccountRole> roles;

    @JsonIgnore
    @Column(length = 256)
    @Getter
    @Setter
    private String password;

    @JsonIgnore
    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Getter
    @Setter
    private List<Address> addresses = Lists.newArrayList();

    @JsonIgnore
    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderColumn(name = "index")
    @Getter
    @Setter
    private List<Image> images = Lists.newArrayList();

    @JsonIgnore
    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Getter
    @Setter
    private List<PlayerProfile> playerProfiles = Lists.newArrayList();

    @Column(name = "last_logged_in")
    @Getter
    @Setter
    private Date lastLoggedIn;

    /**
     * Get accolades for this account.
     *
     * @return list of accolades
     */
    public List<Accolade> getAccolades() {
        return new ArrayList<>();
    }

    public Date getDateOfBirth() {
        return dateOfBirth == null ? null : new Date(dateOfBirth.getTime());
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth == null ? null : new Date(dateOfBirth.getTime());
    }


    /**
     * Get address for id.
     *
     * @param addressId address id
     * @return address
     * @throws AddressNotFoundException if address is not found
     */
    public Address getAddress(int addressId) throws AddressNotFoundException {
        return addresses.stream().filter(a -> a.getId().equals(addressId)).findAny()
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id " + addressId + " for account " + id));
    }

    /**
     * Create an address.
     *
     * @param addressDescriptor address information
     * @return created address
     * @throws AddressValidationException if address is invalid
     */
    public Address createAddress(AddressDescriptor addressDescriptor) throws AddressValidationException {
        Address address = new Address();
        populateAddress(addressDescriptor, address);
        assertAddressValid(address);
        addAddress(address);
        return address;
    }

    private void populateAddress(AddressDescriptor addressDescriptor, Address address) {
        address.setAccount(this);
        OptionalAwareSimplePropertyCopier beanCopier = new OptionalAwareSimplePropertyCopier();
        try {
            beanCopier.copyProperties(address, addressDescriptor);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addAddress(Address address) {
        addresses.add(address);
    }

    /**
     * Update specified address.
     *
     * @param addressId         address id
     * @param addressDescriptor account information
     * @return updated address
     * @throws AddressValidationException if address is invalid
     * @throws AddressNotFoundException   if address is not found
     */
    public Address updateAddress(int addressId, AddressDescriptor addressDescriptor) throws AddressValidationException, AddressNotFoundException {
        Address address = getAddress(addressId);
        populateAddress(addressDescriptor, address);
        assertAddressValid(address);
        return address;
    }

    private void assertAddressValid(Address address, Class... validationGroups) throws AddressValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Address>> violations = validator.validate(address, validationGroups);
        if (!violations.isEmpty()) {
            throw new AddressValidationException(violations);
        }
    }

    /**
     * Delete specified address.
     *
     * @param addressId address id
     * @return deleted address
     * @throws AddressNotFoundException if address is not found
     */
    public Address deleteAddress(int addressId) throws AddressNotFoundException {
        Address address = getAddress(addressId);
        addresses.remove(address);
        return address;
    }

    /**
     * Create an account with the specified password.
     *
     * @param userPassword password
     * @return created account
     * @throws AccountValidationException if account is invalid for creation
     */
    public Account create(String userPassword) throws AccountValidationException {
        setPassword(passwordUtility.encryptPassword(userPassword));
        assertValid();
        try {
            accountRepository.save(this);
        } catch (JpaSystemException | DataIntegrityViolationException e) {
            if (isUniqueConstraintViolation(e)) {
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
        accountDescriptor.setActivated(Optional.of(getActivated()));
        OptionalAwareSimplePropertyCopier beanCopier = new OptionalAwareSimplePropertyCopier();
        try {
            beanCopier.copyProperties(this, accountDescriptor);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private Account performUpdate() throws AccountValidationException {
        assertValid();
        try {
            accountRepository.save(this);
        } catch (JpaSystemException | DataIntegrityViolationException e) {
            if (isUniqueConstraintViolation(e)) {
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
        return imageRepository.findOneByAccountAndId(this, imageId)
                .orElseThrow(() -> new ImageNotFoundException("No image " + imageId + " for account " + id));
    }

    /**
     * Delete the specified account image.
     *
     * @param imageId image id
     * @return deleted image
     * @throws ImageNotFoundException if image does not exist
     * @throws ImageInUseException    if image is in use
     */
    public Image deleteImage(int imageId) throws ImageNotFoundException, ImageInUseException {
        Image image = getImage(imageId);
        if (CollectionUtils.isEmpty(image.getPlayerProfiles())) {
            images.remove(image);
        } else {
            throw new ImageInUseException("Image " + imageId + " is in use and cannot be deleted");
        }
        return image;
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
        return playerProfileRepository.findOneByAccountAndId(this, playerId)
                .orElseThrow(() -> new PlayerProfileNotFoundException("No player profile " + playerId + " for account " + id));
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
     * @throws PlayerProfileInUseException    if player profile is in use
     */
    public PlayerProfile deletePlayerProfile(int playerId) throws PlayerProfileNotFoundException, PlayerProfileInUseException {
        PlayerProfile playerProfile = getPlayerProfile(playerId);
        if (CollectionUtils.isEmpty(playerProfile.getPlayers())) {
            playerProfileRepository.delete(playerProfile);
        } else {
            throw new PlayerProfileInUseException("Player profile " + playerId + " is in use and cannot be deleted");
        }
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
            playerProfile.setName(playerProfileDescriptor.getName().orElse(null));
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

}

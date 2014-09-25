package com.subrosagames.subrosa.domain.account;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.subrosagames.subrosa.api.dto.AccountDescriptor;
import com.subrosagames.subrosa.domain.PermissionTarget;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageType;
import com.subrosagames.subrosa.util.bean.OptionalAwareBeanUtilsBean;

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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_image",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @MapKey(name = "imageType")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<ImageType, Image> images;

    /**
     * Get accolades for this account.
     *
     * @return list of accolades
     */
    public List<Accolade> getAccolades() {
        return new ArrayList<Accolade>();
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
    public Map<ImageType, Image> getImages() {
        if (!Hibernate.isInitialized(images)) {
            return null;
        }
        return images;
    }

    /**
     * Set images.
     *
     * @param images images
     */
    public void setImages(Map<ImageType, Image> images) {
        this.images = images;
    }

    /**
     * Get image of the specified type.
     *
     * @param imageType image type
     * @return image
     */
    public Image getImage(ImageType imageType) {
        return images.get(imageType);
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
     * @param password password
     * @return created account
     * @throws AccountValidationException if account is invalid for creation
     * @throws EmailConflictException     if email address is already in use
     */
    public Account create(String password) throws AccountValidationException, EmailConflictException {
        assertValid();
        try {
            accountRepository.create(this, password);
        } catch (JpaSystemException e) {
            String message = e.getMostSpecificCause().getMessage();
            if (message.contains("unique constraint")) {
                throw new EmailConflictException("Email " + getEmail() + " already in use.", e);
            }
            throw e;
        }
        accountFactory.injectDependencies(this);
        return this;
    }

    public Account update(AccountDescriptor accountDescriptor) throws AccountValidationException {
        OptionalAwareBeanUtilsBean beanCopier = new OptionalAwareBeanUtilsBean();
        try {
            beanCopier.copyProperties(this, accountDescriptor);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        assertValid();
        return this;
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
}

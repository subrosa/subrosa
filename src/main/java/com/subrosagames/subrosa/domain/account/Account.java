package com.subrosagames.subrosa.domain.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import org.codehaus.jackson.annotate.JsonIgnore;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageType;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.Hibernate;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfiles;

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
public class Account {

    @Id
    @SequenceGenerator(name = "accountSeq", sequenceName = "account_account_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSeq")
    @Column(name = "account_id")
    private int id;

    @Column
    private boolean activated;

    @Column
    private String username;

    @Column
    private String name;

    @Column
    private String email;

    @Column(name = "cell_phone")
    private String cellPhone;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @ElementCollection(targetClass = AccountRole.class, fetch = FetchType.EAGER)
    @JoinTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> accountRoles;

    @JsonIgnore
    @Column
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

    @OneToMany
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
     * @return list of accolades
     */
    public List<Accolade> getAccolades() {
        return new ArrayList<Accolade>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
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


    public Set<AccountRole> getAccountRoles() {
        return accountRoles;
    }

    public void setAccountRoles(Set<AccountRole> accountRoles) {
        this.accountRoles = accountRoles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<AddressType, Address> getAddresses() {
        if (!Hibernate.isInitialized(addresses)) {
            return null;
        }
        return addresses;
    }

    public void setAddresses(Map<AddressType, Address> addresses) {
        this.addresses = addresses;
    }

    public Map<ImageType, Image> getImages() {
        if (!Hibernate.isInitialized(images)) {
            return null;
        }
        return images;
    }

    public void setImages(Map<ImageType, Image> images) {
        this.images = images;
    }

    public Image getImage(ImageType imageType) {
        return images.get(imageType);
    }

    public Address getAddress(AddressType addressType) {
        return addresses.get(addressType);
    }
}

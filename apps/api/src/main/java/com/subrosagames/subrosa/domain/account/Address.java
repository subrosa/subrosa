package com.subrosagames.subrosa.domain.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Used to represent an accounts addresses.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Address extends BaseEntity {

    @Id
    @SequenceGenerator(name = "addressSeq", sequenceName = "address_address_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressSeq")
    @Column(name = "address_id")
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @JsonIgnore
    @Column
    private Integer index;

    @Column
    @NotBlank
    @Length(max = 128)
    private String label;

    @OneToOne(targetEntity = LocationEntity.class)
    @JoinColumn(name = "location_id")
    private Location location;

    @NotBlank
    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "user_provided")
    private String userProvided;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "street_address_2")
    private String streetContinued;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @PrePersist
    @PreUpdate
    private void prepareIndex() {
        if (account != null) {
            index = account.getAddresses().indexOf(this);
        }
    }

}

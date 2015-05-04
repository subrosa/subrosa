package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonValue;
import com.subrosagames.subrosa.domain.account.Address;

/**
 * Persists a reference to an account image as a player attribute.
 */
@Entity
@DiscriminatorValue(PlayerAttributeAddress.ATTRIBUTE_TYPE_ADDRESS)
public class PlayerAttributeAddress extends PlayerAttribute {

    /**
     * Indicates address attribute type.
     */
    public static final String ATTRIBUTE_TYPE_ADDRESS = "ADDRESS";

    @ManyToOne
    @JoinColumn(name = "value_ref_id")
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @JsonValue
    public Object getJsonValue() {
        return address;
    }

    public String getValueRef() {
        return address.getId().toString();
    }
}

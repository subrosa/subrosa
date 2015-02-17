package com.subrosagames.subrosa.domain.game;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.player.persistence.PlayerAttribute;
import com.subrosagames.subrosa.domain.player.persistence.PlayerAttributeAddress;
import com.subrosagames.subrosa.domain.player.persistence.PlayerAttributeImage;

/**
 * Enumeration of type that enrollment fields can take.
 */
public enum EnrollmentFieldType {
    /**
     * Simple text field.
     */
    TEXT("text"),
    /**
     * Numeric field.
     */
    NUMBER("number"),
    /**
     * Phone number.
     */
    TEL("tel"),
    /**
     * Image from image gallery.
     */
    IMAGE("image") {
        @Override
        public PlayerAttribute newForAccount(Account account, Object attribute) throws ImageNotFoundException {
            Image image = account.getImage((Integer) ((Map) attribute).get("id"));
            PlayerAttributeImage imageAttribute = new PlayerAttributeImage();
            imageAttribute.setImage(image);
            return imageAttribute;
        }

        @Override
        public PlayerAttribute updateForAccount(Account account, Object attribute, PlayerAttribute playerAttribute) throws ImageNotFoundException {
            if (!(attribute instanceof Map)) {
                throw new IllegalArgumentException("Expected a Map but received a " + attribute.getClass() + " updating enrollment field");
            }
            Image image = account.getImage((Integer) ((Map) attribute).get("id"));
            if (playerAttribute instanceof PlayerAttributeImage) {
                ((PlayerAttributeImage) playerAttribute).setImage(image);
            }
            return playerAttribute;
        }
    },
    /**
     * Address from address book.
     */
    ADDRESS("address") {
        @Override
        public PlayerAttribute newForAccount(Account account, Object attribute) throws AddressNotFoundException {
            Address address = account.getAddress((Integer) ((Map) attribute).get("id"));
            PlayerAttributeAddress addressAttribute = new PlayerAttributeAddress();
            addressAttribute.setAddress(address);
            return addressAttribute;
        }

        @Override
        public PlayerAttribute updateForAccount(Account account, Object attribute, PlayerAttribute playerAttribute) throws AddressNotFoundException {
            if (!(attribute instanceof Map)) {
                throw new IllegalArgumentException("Expected a Map but received a " + attribute.getClass() + " updating enrollment field");
            }
            Address address = account.getAddress((Integer) ((Map) attribute).get("id"));
            if (playerAttribute instanceof PlayerAttributeAddress) {
                ((PlayerAttributeAddress) playerAttribute).setAddress(address);
            }
            return playerAttribute;
        }
    };

    private final String name;

    /**
     * Construct with string name.
     *
     * @param name field type name
     */
    EnrollmentFieldType(String name) {
        this.name = name;
    }

    /**
     * Get the user-facing representation of the type.
     *
     * @return type name
     */
    @JsonValue
    public String getName() {
        return name;
    }

    /**
     * Creates a player attribute object for the given inputs.
     *
     * @param account   account
     * @param attribute player attribute input
     * @return created player attribute
     * @throws ImageNotFoundException   if an image is not found
     * @throws AddressNotFoundException if an address is not found
     */
    public PlayerAttribute newForAccount(Account account, Object attribute) throws ImageNotFoundException, AddressNotFoundException {
        PlayerAttribute pa = new PlayerAttribute();
        pa.setValue((String) attribute);
        return pa;
    }

    /**
     * Updates a player attribute object for the given inputs.
     *
     * @param account         account
     * @param attribute       player attribute input
     * @param playerAttribute player attribute to update
     * @return updated player attribute
     * @throws ImageNotFoundException   if an image is not found
     * @throws AddressNotFoundException if an address is not found
     */
    public PlayerAttribute updateForAccount(Account account, Object attribute, PlayerAttribute playerAttribute) throws ImageNotFoundException,
            AddressNotFoundException
    {
        playerAttribute.setValue((String) attribute);
        return playerAttribute;
    }
}


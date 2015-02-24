package com.subrosagames.subrosa.service;

/**
 * Message container for notifications of new user addresses.
 */
public class UserAddressNotification {

    private int addressId;

    /**
     * Default constructor.
     */
    public UserAddressNotification() {
    }

    /**
     * Construct with address id.
     *
     * @param addressId address id
     */
    public UserAddressNotification(int addressId) {
        this.addressId = addressId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
}

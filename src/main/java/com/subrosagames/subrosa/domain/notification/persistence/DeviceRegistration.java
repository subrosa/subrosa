package com.subrosagames.subrosa.domain.notification.persistence;

import com.subrosagames.subrosa.domain.notification.DeviceType;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name = "device_registration")
public class DeviceRegistration {

    @Id
    @Column(name = "registration_id")
    private String registrationId;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "device_type")
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}

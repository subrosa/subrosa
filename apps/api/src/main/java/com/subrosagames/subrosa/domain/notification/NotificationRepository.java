package com.subrosagames.subrosa.domain.notification;

import java.util.List;

/**
 * A repository for devices available for notification.
 */
public interface NotificationRepository {

    /**
     * Get devices that should be notified for a given game.
     *
     * @param gameId game id
     * @return list of device registration ids
     */
    List<String> getDevicesForGame(int gameId);

    /**
     * Register a device for notifications.
     *
     * @param registrationId registration id
     * @param deviceType     device type
     */
    void registerDevice(String registrationId, DeviceType deviceType);

    /**
     * Unregister a device for notifications.
     *
     * @param registrationId registration id
     * @param deviceType     device type
     */
    void unregisterDevice(String registrationId, DeviceType deviceType);
}

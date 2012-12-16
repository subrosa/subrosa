package com.subrosagames.subrosa.domain.notification;

import java.util.List;

/**
 *
 */
public interface NotificationRepository {

    List<String> getDevicesForGame(int gameId);

    void registerDevice(String registrationId, DeviceType deviceType);

    void unregisterDevice(String registrationId, DeviceType deviceType);
}

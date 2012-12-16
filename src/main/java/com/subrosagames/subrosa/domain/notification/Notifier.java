package com.subrosagames.subrosa.domain.notification;

import java.io.IOException;

/**
 *
 */
public interface Notifier {

    void sendNotification(NotificationDetails notificationDetails) throws IOException;
}

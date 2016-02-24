package com.subrosagames.subrosa.domain.notification;

import java.io.IOException;

/**
 * Notifies a user of a game event, for example via email, SMS, or mobile app notification.
 *
 * @see NotificationDetails
 */
public interface Notifier {

    /**
     * Send a notification to the appropriate users for the given notification details.
     *
     * @param notificationDetails notification details
     * @throws IOException if a notification could not be sent
     */
    void sendNotification(NotificationDetails notificationDetails) throws IOException;
}

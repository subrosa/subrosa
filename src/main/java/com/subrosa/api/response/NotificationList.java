package com.subrosa.api.response;

import java.util.Collection;
import java.util.HashSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.subrosa.api.notification.Notification;

/**
 * Class for sending error notifications to the client. This should only be used with non 2XX HTTP status codes
 */
@XmlRootElement(name = "notifications")
public class NotificationList {

    private Collection<Notification> notifications;

    /**
     * Creates an empty error response.
     */
    public NotificationList() {
    }

    /**
     * Creates an error response containing a single Notification.
     *
     * @param notification the notification to send to the client
     */
    public NotificationList(Notification notification) {
        this.addNotification(notification);
    }

    /**
     * Creates an error response containing the supplied collection of notifications.
     *
     * @param notifications the collection of notifications to send to the client
     */
    public NotificationList(Collection<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * Adds a new Notification to the error response.
     *
     * @param notification the notification to add to the response
     */
    public void addNotification(Notification notification) {
        if (notifications == null) {
            notifications = new HashSet<Notification>();
        }
        notifications.add(notification);
    }

    /**
     * Gets the collection of notifications in the response.
     *
     * @return a collection of notifications
     */
    @XmlElement(name = "notification")
    public Collection<Notification> getNotifications() {
        return notifications;
    }

    /**
     * Sets the collection of notifications in the response.
     *
     * @param notifications the notifications to send to the client
     */
    public void setNotifications(Collection<Notification> notifications) {
        this.notifications = notifications;
    }

}

package com.subrosagames.subrosa.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.subrosagames.subrosa.domain.notification.DeviceType;
import com.subrosagames.subrosa.domain.notification.NotificationCode;
import com.subrosagames.subrosa.domain.notification.NotificationDetails;
import com.subrosagames.subrosa.domain.notification.NotificationRepository;
import com.subrosagames.subrosa.domain.notification.Notifier;

/**
 * Controller for android device specific functionality.
 */
@Controller
public class AndroidController {

    private static final Logger LOG = LoggerFactory.getLogger(AndroidController.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private Notifier notifier;

    /**
     * Register a device for notifications.
     * @param registrationId registration id
     * @return success
     */
    @RequestMapping(value = "/android/register/{registrationId}", method = RequestMethod.POST)
    @ResponseBody
    public String handleDeviceRegistration(@PathVariable("registrationId") String registrationId) {
        LOG.debug("Registering device {} ({})", registrationId, DeviceType.ANDROID);
        notificationRepository.registerDevice(registrationId, DeviceType.ANDROID);
        return "success";
    }

    /**
     * Unregister a device for notifications.
     * @param registrationId registration id
     * @return success
     */
    @RequestMapping(value = "/android/unregister/{registrationId}", method = RequestMethod.POST)
    @ResponseBody
    public String handleDeviceUnregistration(@PathVariable("registrationId") String registrationId) {
        LOG.debug("Unregistering device {} ({})", registrationId, DeviceType.ANDROID);
        notificationRepository.unregisterDevice(registrationId, DeviceType.ANDROID);
        return "success";
    }

    /**
     * Trigger notifications be sent for a game. Test endpoint.
     * @param gameId game id
     * @return success
     * @throws IOException if notification send fails
     */
    @RequestMapping(value = "/android/notify/{gameId}", method = RequestMethod.POST)
    @ResponseBody
    public String notifyDevices(@PathVariable("gameId") Integer gameId) throws IOException {
        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.setGameId(gameId);
        notificationDetails.setCode(NotificationCode.TEST);
        notifier.sendNotification(notificationDetails);
        return "success";
    }
}

package com.subrosagames.subrosa.infrastructure.android.gcm;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.subrosagames.subrosa.domain.notification.NotificationDetails;
import com.subrosagames.subrosa.domain.notification.NotificationRepository;
import com.subrosagames.subrosa.domain.notification.Notifier;

/**
 * Notification mechanism using Google Cloud Messaging.
 */
@Component
public class GCMNotifier implements Notifier {

    private static final Logger LOG = LoggerFactory.getLogger(GCMNotifier.class);

    @Autowired
    private String gcmApiKey;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void sendNotification(NotificationDetails notificationDetails) throws IOException {
        int gameId = notificationDetails.getGameId();
        LOG.debug("Sending notifications for game {}", gameId);
        List<String> devices = notificationRepository.getDevicesForGame(gameId);
        Sender sender = new Sender(gcmApiKey);
        Message message = new Message.Builder()
                .collapseKey("1")
                .timeToLive(3)
                .delayWhileIdle(true)
                .addData("code", notificationDetails.getCode())
                .addData("title", "The Game Has Begun!")
                .addData("text", "Find out who your target is.")
                .build();
        MulticastResult multicastResult = sender.send(message, devices, 5);
        List<Result> results = multicastResult.getResults();

        for (Result result : results) {
            if (result.getMessageId() != null) {
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    LOG.debug("Received null canonical registration id - updating database with new id");
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    LOG.debug("Application has been removed from device - unregistering device");
                }
            }
        }
    }
}

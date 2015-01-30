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
import com.subrosagames.subrosa.bootstrap.GoogleIntegration;
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
    private GoogleIntegration googleIntegration;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void sendNotification(NotificationDetails notificationDetails) throws IOException {
        int gameId = notificationDetails.getGameId();
        LOG.debug("Sending notifications for game {}", gameId);
        List<String> devices = notificationRepository.getDevicesForGame(gameId);
        Sender sender = new Sender(googleIntegration.getGcmApiKey());
        Message.Builder builder = new Message.Builder()
                .collapseKey("1")
                .timeToLive(3)
                .delayWhileIdle(true)
                .addData("code", notificationDetails.getCode().name());
        switch (notificationDetails.getCode()) {
            case GAME_START:
                builder.addData("title", "The Game Has Begun!")
                        .addData("text", "Find out who your target is.");
                break;
            case GAME_END:
                builder.addData("title", "The Game Is OVER!")
                        .addData("text", "You lost. You're a loser.");
                break;
            default:
                // TODO should probably be an exception
                builder.addData("title", "I don't know what happened")
                        .addData("text", "I was told to send you a notification so I did.");
        }
        Message message = builder.build();
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

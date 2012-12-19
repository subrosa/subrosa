package com.subrosagames.subrosa.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.subrosagames.subrosa.domain.notification.NotificationCode;
import com.subrosagames.subrosa.domain.notification.NotificationDetails;
import com.subrosagames.subrosa.domain.notification.Notifier;
import com.subrosagames.subrosa.event.message.StartGameMessage;

/**
 * Handler for {@link StartGameMessage}s.
 */
public class StartGameMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StartGameMessageHandler.class);

    @Autowired
    private Notifier notifier;

    /**
     * Handle the given start game message.
     * @param startGameMessage start game message
     * @throws Exception if message cannot be handled
     */
    public void process(StartGameMessage startGameMessage) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling game start message for game {}", startGameMessage.getGameId());
        }
        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.setGameId(startGameMessage.getGameId());
        notificationDetails.setCode(NotificationCode.GAME_START);
        notifier.sendNotification(notificationDetails);
    }

}

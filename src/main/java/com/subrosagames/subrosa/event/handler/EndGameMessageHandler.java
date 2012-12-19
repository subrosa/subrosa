package com.subrosagames.subrosa.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.subrosagames.subrosa.domain.notification.NotificationCode;
import com.subrosagames.subrosa.domain.notification.NotificationDetails;
import com.subrosagames.subrosa.domain.notification.Notifier;
import com.subrosagames.subrosa.event.message.EndGameMessage;

/**
 * Handler for {@link EndGameMessage}s.
 */
public class EndGameMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EndGameMessageHandler.class);

    @Autowired
    private Notifier notifier;

    /**
     * Handle the given end game message.
     * @param endGameMessage end game message
     * @throws Exception if message cannot be handled
     */
    public void process(EndGameMessage endGameMessage) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling game end message for game {}", endGameMessage.getGameId());
        }
        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.setGameId(endGameMessage.getGameId());
        notificationDetails.setCode(NotificationCode.GAME_END);
        notifier.sendNotification(notificationDetails);
    }
}

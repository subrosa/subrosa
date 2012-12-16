package com.subrosagames.subrosa.event.handler;

import com.subrosagames.subrosa.domain.notification.NotificationCode;
import com.subrosagames.subrosa.domain.notification.NotificationDetails;
import com.subrosagames.subrosa.domain.notification.Notifier;
import com.subrosagames.subrosa.event.message.EndGameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class EndGameMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EndGameMessageHandler.class);

    @Autowired
    private Notifier notifier;

    public void process(EndGameMessage endGameMessage) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling game end message for game {}", endGameMessage.getGameId());
        }
        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.setGameId(endGameMessage.getGameId());
        notificationDetails.setCode(NotificationCode.GAME_END.name());
        notifier.sendNotification(notificationDetails);
    }
}

package com.subrosagames.subrosa.event.handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.notification.NotificationCode;
import com.subrosagames.subrosa.domain.notification.NotificationDetails;
import com.subrosagames.subrosa.domain.notification.Notifier;

/**
 * Handler for ending games.
 */
@Component
public class GameEndMessageHandler extends AbstractMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GameEndMessageHandler.class);

    @Autowired
    private Notifier notifier;

    @Override
    public void process(Game game, Map<String, Serializable> properties) throws MessageHandlingException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling game end message for game {}", game.getId());
        }
        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.setGameId(game.getId());
        notificationDetails.setCode(NotificationCode.GAME_END);
        try {
            notifier.sendNotification(notificationDetails);
        } catch (IOException e) {
            throw new MessageHandlingException(e);
        }
    }
}

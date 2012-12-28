package com.subrosagames.subrosa.event.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
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

    @Autowired
    private GameFactory gameFactory;

    /**
     * Handle the given start game message.
     * @param startGameMessage start game message
     * @throws Exception if message cannot be handled
     */
    public void process(StartGameMessage startGameMessage) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling game start message for game {}", startGameMessage.getGameId());
        }

        Game game = gameFactory.getGameForId(startGameMessage.getGameId());
        game.startGame();

        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.setGameId(startGameMessage.getGameId());
        notificationDetails.setCode(NotificationCode.GAME_START);
        notifier.sendNotification(notificationDetails);
    }

}

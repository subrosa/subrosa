package com.subrosagames.subrosa.event.handler;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.notification.NotificationCode;
import com.subrosagames.subrosa.domain.notification.NotificationDetails;
import com.subrosagames.subrosa.domain.notification.Notifier;
import com.subrosagames.subrosa.event.EventExecutor;
import com.subrosagames.subrosa.event.TriggeredEvent;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 * Handler for starting a game.
 */
public class GameStartMessageHandler extends AbstractMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GameStartMessageHandler.class);

    @Autowired
    private Notifier notifier;

    @Autowired
    private EventExecutor eventExecutor;

    @Override
    public void process(Game game, Map<String, Serializable> properties) throws Exception {
        game.startGame();

        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.setGameId(game.getId());
        notificationDetails.setCode(NotificationCode.GAME_START);
        notifier.sendNotification(notificationDetails);
    }

}

package com.subrosagames.subrosa.domain.game;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.event.EventRepository;
import com.subrosagames.subrosa.domain.game.persistence.TriggeredEventEntity;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.EventExecutor;
import com.subrosagames.subrosa.event.TriggeredEvent;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 * Interface describing the base level of functionality a game implementation must provide.
 */
public class GameHelper {

    private static final Logger LOG = LoggerFactory.getLogger(GameHelper.class);

    private GameRepository gameRepository;
    private PlayerFactory playerFactory;
    private EventRepository eventRepository;
    private EventExecutor eventExecutor;

    private GameEntity game;

    public GameHelper(GameEntity game) {
        this.game = game;
    }

    /**
     * Validate that this is a valid game.
     * @throws com.subrosagames.subrosa.domain.game.validation.GameValidationException if the game is invalid
     */
    public void validate() throws GameValidationException {
    }

    public Game publish() {
        throw new NotImplementedException("game publishing");
    }

    public Player getPlayer(int accountId) {
        return gameRepository.getPlayerForUserAndGame(accountId, game.getId());
    }

    public List<PlayerEntity> getPlayers() {
        List<PlayerEntity> playerEntities = gameRepository.getPlayersForGame(game.getId());
        return playerEntities;
    }

    public boolean achieveTarget(Player player, int targetId, String code) throws TargetNotFoundException {
        Target target = player.getTarget(targetId);

        Map<String, Serializable> properties = Maps.newHashMap();
        properties.put("playerId", player.getId());
        properties.put("targetId", targetId);
        eventExecutor.execute(EventMessage.TARGET_ACHIEVED.name(), game.getId(), properties);
        return true;
    }

    public Player addUserAsPlayer(Account account, PlayerDescriptor playerDescriptor) throws PlayerValidationException {
        return playerFactory.createPlayerForGame(game, account, playerDescriptor);
    }

    public void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
        LOG.debug("Setting attribute {} to {} for game {}", new Object[] { attributeType, attributeValue, game.getId() });
        gameRepository.setGameAttribute(game, attributeType, attributeValue);
    }

    public void addTriggeredEvent(EventMessage eventType, Event trigger) {
        TriggeredEventEntity entity = eventRepository.createTriggeredEvent(eventType, trigger);
        Lifecycle lifecycle = game.getLifecycle();
        lifecycle.addTriggeredEvent(entity);
        gameRepository.save(lifecycle);
    }

    public List<TriggeredEvent> getEventsTriggeredBy(EventMessage eventMessage) {
        List<TriggeredEvent> eventsTriggered = Lists.newArrayList();
        List<TriggeredEventEntity> persistedEvents = game.getLifecycle().getTriggeredEvents();
        for (TriggeredEventEntity event : persistedEvents) {
            String eventClass = event.getTriggerEvent().getEventClass();
            if (eventClass.equals(eventMessage.name())) {
                eventsTriggered.add(event);
            }
        }
        return eventsTriggered;
    }

    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void setPlayerFactory(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
    }

    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
}

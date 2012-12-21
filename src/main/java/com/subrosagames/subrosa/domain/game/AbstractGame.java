package com.subrosagames.subrosa.domain.game;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import com.google.common.collect.Maps;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.event.EventExecutor;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 * Interface describing the base level of functionality a game implementation must provide.
 */
public abstract class AbstractGame implements Game {

    private GameRepository gameRepository;

    private EventExecutor eventExecutor;


    @JsonIgnore
    private GameEntity gameEntity;
    @JsonIgnore
    private Lifecycle gameLifecycle;

    /**
     * Construct with given game information and lifecycle.
     * @param gameEntity game entity
     * @param gameLifecycle game lifecycle
     */
    public AbstractGame(GameEntity gameEntity, Lifecycle gameLifecycle) {
        this.gameEntity = gameEntity;
        this.gameLifecycle = gameLifecycle;
    }

    /**
     * Get the events that must exist for this to be a valid game.
     * @return array of events
     */
    protected abstract String[] getRequiredEvents();

    /**
     * Get game entity.
     * @return game entity
     */
    public GameEntity getGameEntity() {
        return gameEntity;
    }

    /**
     * Get game lifecycle.
     * @return game lifecycle
     */
    public Lifecycle getGameLifecycle() {
        return gameLifecycle;
    }

    /**
     * Validate that this is a valid game.
     * @throws GameValidationException if the game is invalid
     */
    public void validate() throws GameValidationException {
    }

    /**
     * Persist this game data as a new game.
     * @throws GameValidationException if the game is invalid
     */
    public void create() throws GameValidationException {
        gameRepository.createGame(this);
    }

    @Override
    public Player getPlayer(int accountId) {
        return new Player(gameRepository.getPlayerForUserAndGame(accountId, this.getId()));
    }

    @Override
    public boolean achieveTarget(Player player, int targetId, String code) throws TargetNotFoundException {
        Target target = player.getTarget(targetId);

        Map<String, Serializable> properties = Maps.newHashMap();
        properties.put("playerId", player.getId());
        properties.put("targetId", targetId);
        eventExecutor.execute(EventMessage.TARGET_ACHIEVED.name(), getId(), properties);
        return true;
    }

    @JsonIgnore
    public List<Post> getPosts() {
        return getGameEntity().getPosts();
    }

    public int getId() {
        return getGameEntity().getId();
    }

    public String getName() {
        return getGameEntity().getName();
    }

    public String getDescription() {
        return getGameEntity().getDescription();
    }

    public GameType getGameType() {
        return getGameEntity().getGameType();
    }

    public BigDecimal getPrice() {
        return getGameEntity().getPrice();
    }

    public Date getStartTime() {
        return getGameLifecycle().getGameStart();
    }

    public Date getEndTime() {
        return getGameLifecycle().getGameEnd();
    }

    public Date getRegistrationStartTime() {
        return getGameLifecycle().getRegistrationStart();
    }

    public Date getRegistrationEndTime() {
        return getGameLifecycle().getRegistrationEnd();
    }

    public String getTimezone() {
        return getGameEntity().getTimezone();
    }

    public String getPassword() {
        return getGameEntity().getPassword();
    }

    public Image getImage() {
        return getGameEntity().getImage();
    }

    public Integer getMinimumAge() {
        return getGameEntity().getMinimumAge();
    }

    public Integer getMaximumTeamSize() {
        return getGameEntity().getMaximumTeamSize();
    }

    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void setPlayerFactory(PlayerFactory playerFactory) {
    }
}

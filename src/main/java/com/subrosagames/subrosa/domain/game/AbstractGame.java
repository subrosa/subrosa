package com.subrosagames.subrosa.domain.game;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;

/**
 * Interface describing the base level of functionality a game implementation must provide.
 */
public abstract class AbstractGame implements Game {

    private GameRepository gameRepository;

    private int id;
    @JsonIgnore
    private GameEntity gameEntity;
    @JsonIgnore
    private Lifecycle gameLifecycle;

    /**
     * Construct with given game id.
     * @param id game id
     */
    public AbstractGame(int id) {
        this.id = id;
    }

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
        if (gameEntity == null) {
            gameEntity = gameRepository.getGameEntity(id);
        }
        return gameEntity;
    }

    /**
     * Get game lifecycle.
     * @return game lifecycle
     */
    public Lifecycle getGameLifecycle() {
        if (gameLifecycle == null) {
            gameLifecycle = gameRepository.getGameLifecycle(id);
        }
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

}

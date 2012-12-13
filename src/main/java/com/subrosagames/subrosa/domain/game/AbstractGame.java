package com.subrosagames.subrosa.domain.game;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import com.subrosagames.subrosa.domain.message.Post;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.annotate.JsonIgnore;
import com.subrosagames.subrosa.domain.image.Image;

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


    public AbstractGame(int id) {
        this.id = id;
    }

    public AbstractGame(GameEntity gameEntity) {
        this(gameEntity.getId());
        this.gameEntity = gameEntity;
    }

    public AbstractGame(GameEntity gameEntity, Lifecycle gameLifecycle) {
        this(gameEntity);
        this.gameLifecycle = gameLifecycle;
    }

    public GameEntity getGameEntity() {
        if (gameEntity == null) {
            gameEntity = gameRepository.getGameEntity(id);
        }
        return gameEntity;
    }

    public Lifecycle getGameLifecycle() {
        if (gameLifecycle == null) {
            gameLifecycle = gameRepository.getGameLifecycle(id);
        }
        return gameLifecycle;
    }

    public void startGame() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public void makeAssignments() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public void endGame() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public List<? extends GameRule> getRules() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public List<? extends Participant> getPlayers() {
        throw new NotImplementedException("Must be implemented by a child class");
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

    abstract protected String[] getRequiredEvents();

    public void validate() throws GameValidationException {
    }

    public void create() throws GameValidationException {
        gameRepository.createGame(this);
    }
}

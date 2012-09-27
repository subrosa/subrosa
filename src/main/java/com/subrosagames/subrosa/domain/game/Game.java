package com.subrosagames.subrosa.domain.game;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.image.Image;

/**
 * Interface describing the base level of functionality a game implementation must provide.
 */
public class Game {

    private int id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    private GameType gameType;
    @NotNull
    private BigDecimal price;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date registrationEndTime;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date endTime;
    @NotNull
    private String timezone;
    private Integer maximumTeamSize;
    private String password;
    private Image image;
    private Integer minimumAge;

    public void startGame() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public void makeAssignments() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public void endGame() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public List<? extends GameEvent> getEvents() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public List<? extends GameRule> getRules() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public List<? extends Participant> getPlayers() {
        throw new NotImplementedException("Must be implemented by a child class");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getRegistrationEndTime() {
        return registrationEndTime;
    }

    public void setRegistrationEndTime(Date registrationEndTime) {
        this.registrationEndTime = registrationEndTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getMaximumTeamSize() {
        return maximumTeamSize;
    }

    public void setMaximumTeamSize(Integer maximumTeamSize) {
        this.maximumTeamSize = maximumTeamSize;
    }
}

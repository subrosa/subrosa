package com.subrosagames.subrosa.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.subrosagames.subrosa.domain.game.GameData;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.image.Image;
import com.google.common.base.Objects;

/**
 * Encapsulates the necessary information to create a game.
 */
public class GameDescriptor implements GameData {

    private Integer id;
    private String name;
    private String url;
    private String description;
    private GameType gameType;
    private BigDecimal price;
    private String timezone;
    private Integer maximumTeamSize;
    private String password;
    private Image image;
    private Integer minimumAge;
    private Date gameStart;
    private Date gameEnd;
    private Date registrationStart;
    private Date registrationEnd;

    public Integer getId() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getMaximumTeamSize() {
        return maximumTeamSize;
    }

    public void setMaximumTeamSize(Integer maximumTeamSize) {
        this.maximumTeamSize = maximumTeamSize;
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

    public Date getGameStart() {
        return gameStart;
    }

    public void setGameStart(Date gameStart) {
        this.gameStart = gameStart;
    }

    public Date getGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(Date gameEnd) {
        this.gameEnd = gameEnd;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("url", url)
                .add("description", description)
                .add("gameType", gameType)
                .add("price", price)
                .add("timezone", timezone)
                .add("maximumTeamSize", maximumTeamSize)
                .add("password", password)
                .add("image", image)
                .add("minimumAge", minimumAge)
                .add("gameStart", gameStart)
                .add("gameEnd", gameEnd)
                .add("registrationStart", registrationStart)
                .add("registrationEnd", registrationEnd)
                .toString();
    }
}

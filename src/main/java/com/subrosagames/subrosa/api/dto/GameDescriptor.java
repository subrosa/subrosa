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
    private Date startTime;
    private Date endTime;
    private Date registrationStartTime;
    private Date registrationEndTime;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getRegistrationStartTime() {
        return registrationStartTime;
    }

    public void setRegistrationStartTime(Date registrationStartTime) {
        this.registrationStartTime = registrationStartTime;
    }

    public Date getRegistrationEndTime() {
        return registrationEndTime;
    }

    public void setRegistrationEndTime(Date registrationEndTime) {
        this.registrationEndTime = registrationEndTime;
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
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("registrationStartTime", registrationStartTime)
                .add("registrationEndTime", registrationEndTime)
                .toString();
    }
}

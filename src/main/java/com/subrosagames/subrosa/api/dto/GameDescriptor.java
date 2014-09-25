package com.subrosagames.subrosa.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.subrosagames.subrosa.domain.game.GameStatus;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.image.Image;

/**
 * Encapsulates the necessary information to create or update a game.
 */
public class GameDescriptor { //implements GameData {

    private Integer id;
    private Optional<String> name;
    private Optional<String> url;
    private Optional<String> description;
    private Optional<GameType> gameType;
    private Optional<BigDecimal> price;
    private Optional<String> timezone;
    private Optional<Integer> maximumTeamSize;
    private Optional<String> password;
    private Optional<Image> image;
    private Optional<Integer> minimumAge;
    private Optional<Date> gameStart;
    private Optional<Date> gameEnd;
    private Optional<Date> registrationStart;
    private Optional<Date> registrationEnd;
    private Optional<GameStatus> status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
    }

    public Optional<String> getUrl() {
        return url;
    }

    public void setUrl(Optional<String> url) {
        this.url = url;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public void setDescription(Optional<String> description) {
        this.description = description;
    }

    public Optional<GameType> getGameType() {
        return gameType;
    }

    public void setGameType(Optional<GameType> gameType) {
        this.gameType = gameType;
    }

    public Optional<BigDecimal> getPrice() {
        return price;
    }

    public void setPrice(Optional<BigDecimal> price) {
        this.price = price;
    }

    public Optional<String> getTimezone() {
        return timezone;
    }

    public void setTimezone(Optional<String> timezone) {
        this.timezone = timezone;
    }

    public Optional<Integer> getMaximumTeamSize() {
        return maximumTeamSize;
    }

    public void setMaximumTeamSize(Optional<Integer> maximumTeamSize) {
        this.maximumTeamSize = maximumTeamSize;
    }

    public Optional<String> getPassword() {
        return password;
    }

    public void setPassword(Optional<String> password) {
        this.password = password;
    }

    public Optional<Image> getImage() {
        return image;
    }

    public void setImage(Optional<Image> image) {
        this.image = image;
    }

    public Optional<Integer> getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Optional<Integer> minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Optional<Date> getGameStart() {
        return gameStart;
    }

    public void setGameStart(Optional<Date> gameStart) {
        this.gameStart = gameStart;
    }

    public Optional<Date> getGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(Optional<Date> gameEnd) {
        this.gameEnd = gameEnd;
    }

    public Optional<Date> getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Optional<Date> registrationStart) {
        this.registrationStart = registrationStart;
    }

    public Optional<Date> getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Optional<Date> registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public Optional<GameStatus> getStatus() {
        return status;
    }

    public void setStatus(Optional<GameStatus> status) {
        this.status = status;
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

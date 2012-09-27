package com.subrosa.game.model;

import com.subrosa.event.GameEvent;
import com.subrosa.game.Game;
import com.subrosa.game.GameRule;
import com.subrosa.game.GameType;
import com.subrosagames.subrosa.domain.game.Participant;
import com.subrosagames.subrosa.domain.image.Image;
import org.apache.commons.lang.NotImplementedException;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Represents a generic game.
 */
@Entity
@Table(name = "game")
public class GameModel implements Game {

    @Id
    @SequenceGenerator(name = "gameSeq", sequenceName = "game_game_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gameSeq")
    @Column(name = "game_id")
    private int id;

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "game_type")
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Column
    private BigDecimal price;

    @Column(name = "registration_end_time")
    private Date registrationEndTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column
    private String timezone;

    @Column
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Image image;

    // TODO move this out of the base model class
    @Column(name = "min_age")
    private Integer minimumAge;

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

    @Override
    public List<? extends GameEvent> getEvents() {
        throw new NotImplementedException("Attempted to get events for a non-specific game");
    }

    @Override
    public List<? extends GameRule> getRules() {
        throw new NotImplementedException("Attempted to get rules for a non-specific game");
    }

    @Override
    public List<? extends Participant> getPlayers() {
        throw new NotImplementedException("Attempted to get players for a non-specific game");
    }
}

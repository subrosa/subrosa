package com.subrosagames.subrosa.domain.game.persistence;

import com.subrosagames.subrosa.domain.game.event.GameEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "history")
public class GameEventEntity implements GameEvent {

    @Id
    @Column(name = "history_id")
    private Integer historyId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "assassin_id")
    private Integer assassinId;

    @Column(name = "victim_id")
    private Integer victimId;

    @Column(name = "obituary")
    private String obituary;

    @Column(name = "history_type")
    private String type;

    @Column(name = "created")
    private Date created;

    @Column(name = "modified")
    private Date modified;

    @Override
    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    @Override
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    @Override
    public Integer getAssassinId() {
        return assassinId;
    }

    public void setAssassinId(Integer assassinId) {
        this.assassinId = assassinId;
    }

    @Override
    public Integer getVictimId() {
        return victimId;
    }

    public void setVictimId(Integer victimId) {
        this.victimId = victimId;
    }

    @Override
    public String getObituary() {
        return obituary;
    }

    public void setObituary(String obituary) {
        this.obituary = obituary;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}

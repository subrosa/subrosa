package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name = "player")
public class PlayerEntity {

    @Id
    @Column(name = "player_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @Column(name = "kill_code")
    private String killCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public String getKillCode() {
        return killCode;
    }

    public void setKillCode(String killCode) {
        this.killCode = killCode;
    }
}

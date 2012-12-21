package com.subrosagames.subrosa.domain.player.persistence;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Target;
import com.subrosagames.subrosa.domain.game.persistence.TargetEntity;

/**
 * Persists a player.
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "player")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TargetEntity> targets;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<TargetEntity> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetEntity> targets) {
        this.targets = targets;
    }

}

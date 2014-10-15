package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Restriction;
import com.subrosagames.subrosa.domain.game.RestrictionType;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;

/**
 * Persisted restriction.
 */
@Entity
@Table(name = "restriction")
public class RestrictionEntity extends BaseEntity implements Restriction {

    @Id
    @SequenceGenerator(name = "restrictionSeq", sequenceName = "restriction_restriction_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restrictionSeq")
    @Column(name = "restriction_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity game;

    @Column
    @Enumerated(EnumType.STRING)
    private RestrictionType type;

    @Column
    private String value;

    @Override
    public boolean satisfied(Account account) {
        return type.satisfied(account, value);
    }

    @Override
    public String message() {
        return type.message(value);
    }

    @Override
    public String field() {
        return type.field();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }

    @Override
    public RestrictionType getType() {
        return type;
    }

    public void setType(RestrictionType type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

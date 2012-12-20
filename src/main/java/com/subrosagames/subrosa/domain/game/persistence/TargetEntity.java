package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.game.Target;
import com.subrosagames.subrosa.domain.game.TargetType;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Parent entity for game targets.
 */
@Entity
@Table(name = "target")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "target_type")
public class TargetEntity implements Target {

    @Id
    @Column(name = "target_id")
    @SequenceGenerator(name = "targetSeq", sequenceName = "target_target_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "targetSeq")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type")
    private TargetType targetType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity playerId) {
        this.player = playerId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }
}

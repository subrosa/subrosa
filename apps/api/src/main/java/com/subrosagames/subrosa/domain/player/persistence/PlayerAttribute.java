package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Persisted player attribute.
 */
@Entity
@Table(name = "player_attribute")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "attribute_type", discriminatorType = DiscriminatorType.STRING)
public class PlayerAttribute {

    @EmbeddedId
    private PlayerAttributePk primaryKey;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @MapsId("playerId")
    private PlayerEntity player;

    @Column
    private String value;

    public PlayerAttributePk getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PlayerAttributePk primaryKey) {
        this.primaryKey = primaryKey;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonValue
    public Object getJsonValue() {
        return value;
    }

    public String getValueRef() {
        return value;
    }
}

package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonValue;
import com.subrosagames.subrosa.domain.game.EnrollmentFieldType;
import com.subrosagames.subrosa.domain.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * Persisted player attribute.
 */
@Entity
@Table(name = "player_attribute")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "attribute_type", discriminatorType = DiscriminatorType.STRING)
public class PlayerAttribute {

    @EmbeddedId
    @Getter
    @Setter
    private PlayerAttributePk primaryKey;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @MapsId("playerId")
    @Getter
    @Setter
    private Player player;

    @Column
    @Getter
    @Setter
    private String value;

    @Column(name = "attribute_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private EnrollmentFieldType type;

    @JsonValue
    public Object getJsonValue() {
        return value;
    }

    public String getValueRef() {
        return value;
    }
}

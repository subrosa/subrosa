package com.subrosagames.subrosa.domain.game.persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Represents compound (game_id, id) primary key for an enrollment field.
 *
 * @see EnrollmentFieldEntity
 */
@Embeddable
public class EnrollmentFieldPk implements Serializable {

    private static final long serialVersionUID = 198376005161097782L;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "field_id")
    private String fieldId;

    /**
     * Default constructor.
     */
    public EnrollmentFieldPk() {
    }

    /**
     * Construct with game id and field id.
     *
     * @param gameId game id
     * @param fieldId field id
     */
    public EnrollmentFieldPk(Integer gameId, String fieldId) {
        this.gameId = gameId;
        this.fieldId = fieldId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
}


package com.subrosagames.subrosa.domain.game.persistence;

import com.subrosagames.subrosa.domain.image.Image;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name = "team")
public class TeamEntity {

    @Id
    @Column(name = "team_id")
    private Integer id;

    @Column(name = "game_id")
    private Integer gameId;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Column
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

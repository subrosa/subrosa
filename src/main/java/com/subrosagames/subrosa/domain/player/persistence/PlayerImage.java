package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageType;
import com.subrosagames.subrosa.domain.player.Player;

/**
 * Model class for images.
 */
@Entity
@Table(name = "player_image")
public class PlayerImage {

    @EmbeddedId
    private PlayerImagePk primaryKey;

    @Column(name = "image_type")
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @ManyToOne(targetEntity = PlayerEntity.class)
    @JoinColumn(name = "player_id")
    @MapsId("playerId")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "image_id")
    @MapsId("imageId")
    private Image image;

    public void setPrimaryKey(PlayerImagePk primaryKey) {
        this.primaryKey = primaryKey;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}

package com.subrosa.image;

import org.hibernate.type.ImageType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.net.URI;

/**
 * Model class for images.
 */
@Entity
public class Image {

    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Integer id;

    @Column
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Column
    private URI uri;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}

package com.subrosa.image;

import com.subrosa.db.UriUserType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.net.URI;

/**
 * Model class for images.
 */
@Entity
public class Image {

    @Id
    @SequenceGenerator(name = "imageSeq", sequenceName="image_image_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "imageSeq")
    @Column(name = "image_id")
    private Integer id;

    @Column(name = "image_type")
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Column
    @Type(type = UriUserType.HIBERNATE_TYPE_NAME)
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

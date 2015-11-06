package com.subrosagames.subrosa.domain.image;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.file.FileAsset;
import lombok.Getter;
import lombok.Setter;

/**
 * Model class for images.
 */
@Entity
@Table(name = "image")
public class Image {

    @Id
    @SequenceGenerator(name = "imageSeq", sequenceName = "image_image_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "imageSeq")
    @Column(name = "image_id")
    @Getter
    @Setter
    private Integer id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "file_asset_id")
    @Getter
    @Setter
    private FileAsset fileAsset;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id")
    @Getter
    @Setter
    private Account account;

    @JsonIgnore
    @Column
    @Getter
    @Setter
    private Integer index;

    @JsonIgnore
    @OneToMany(mappedBy = "image")
    @Getter
    @Setter
    private Set<PlayerProfile> playerProfiles = Sets.newHashSet();

    @PrePersist
    @PreUpdate
    private void prepareIndex() {
        if (account != null) {
            index = account.getImages().indexOf(this);
        }
    }

    @JsonProperty
    public String getName() {
        return fileAsset.getName();
    }

    @JsonProperty
    public Long getSize() {
        return fileAsset.getSize();
    }

    @JsonProperty
    public String getMimeType() {
        return fileAsset.getMimeType();
    }

    @JsonProperty
    public String getLink() {
        return "/images/" + fileAsset.getUuid();
    }

}

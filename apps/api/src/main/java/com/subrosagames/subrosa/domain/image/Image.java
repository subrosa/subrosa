package com.subrosagames.subrosa.domain.image;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.file.FileAsset;

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
    private Integer id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "file_asset_id")
    private FileAsset fileAsset;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @JsonIgnore
    @Column
    private Integer index;

    @JsonIgnore
    @OneToMany(mappedBy = "image")
    private Set<PlayerProfile> playerProfiles;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public FileAsset getFileAsset() {
        return fileAsset;
    }

    public void setFileAsset(FileAsset fileAsset) {
        this.fileAsset = fileAsset;
    }

    public Set<PlayerProfile> getPlayerProfiles() {
        return playerProfiles;
    }

    public void setPlayerProfiles(Set<PlayerProfile> playerProfiles) {
        this.playerProfiles = playerProfiles;
    }
}

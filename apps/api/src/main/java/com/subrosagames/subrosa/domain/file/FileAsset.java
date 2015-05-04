package com.subrosagames.subrosa.domain.file;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

/**
 * Holds metadata related to a file asset associated with an application user.
 */
@Entity
@Table(name = "file_asset")
public class FileAsset {

    @Id
    @SequenceGenerator(name = "fileAssetSeq", sequenceName = "file_asset_file_asset_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileAssetSeq")
    @Column(name = "file_asset_id")
    private Integer id;

    @Length(max = 255)
    @Column
    private String name;

    @Column
    private Long size;

    @Length(max = 255)
    @Column(name = "mime_type")
    private String mimeType;

    @Column
    private String uuid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getSize() {
        return size;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

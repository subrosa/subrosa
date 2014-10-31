package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Parent class for persisted entities.
 * <p/>
 * Provides common elements such as create and update timestamps.
 */
@MappedSuperclass
public class BaseEntity {

    @Column
    private Date created;

    @Column
    private Date modified;

    /**
     * Set created and modified dates on creation.
     */
    @PrePersist
    protected void prePersist() {
        created = new Date();
        modified = new Date();
    }

    /**
     * Update the modified date on update.
     */
    @PreUpdate
    protected void preUpdate() {
        modified = new Date();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}

package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * @todo
 */
@MappedSuperclass
public class BaseEntity {

    @Column
    private Date created;

    @Column
    private Date modified;

    @PrePersist
    protected void prePersist() {
        created = new Date();
        modified = new Date();
    }

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

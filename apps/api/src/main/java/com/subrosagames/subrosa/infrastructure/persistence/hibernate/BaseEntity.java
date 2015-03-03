package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.dao.NonTransientDataAccessException;

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

    /**
     * Helper for determining whether an exception was caused by a unique constraint violation.
     *
     * @param e exception
     * @return whether was unique constraint violation
     */
    protected boolean isUniqueConstraintViolation(NonTransientDataAccessException e) {
        String message = e.getMostSpecificCause().getMessage();
        return message.contains("unique constraint");
    }

    /**
     * Get created date.
     *
     * @return created date
     */
    public Date getCreated() {
        return created == null ? null : new Date(created.getTime());
    }

    /**
     * Set created date.
     *
     * @param created created date
     */
    public void setCreated(Date created) {
        this.created = created == null ? null : new Date(created.getTime());
    }

    /**
     * Get last modified date.
     *
     * @return modified date
     */
    public Date getModified() {
        return modified == null ? null : new Date(modified.getTime());
    }

    /**
     * Set last modified date.
     *
     * @param modified modified date
     */
    public void setModified(Date modified) {
        this.modified = modified == null ? null : new Date(modified.getTime());
    }

}


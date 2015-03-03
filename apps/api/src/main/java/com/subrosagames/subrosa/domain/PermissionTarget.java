package com.subrosagames.subrosa.domain;

import java.io.Serializable;

/**
 * Target of an ACL permission.
 */
public interface PermissionTarget {

    /**
     * Object id.
     * @return id
     */
    Serializable getId();
}

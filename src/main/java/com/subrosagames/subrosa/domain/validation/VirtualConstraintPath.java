package com.subrosagames.subrosa.domain.validation;

import java.util.Iterator;
import javax.validation.Path;

/**
 * Sparse implementation of @{link Path} to allow for virtual constraint violations.
 *
 * @see VirtualConstraintViolation
 */
public class VirtualConstraintPath implements Path {

    private String path;

    /**
     * Construct with the given field name.
     *
     * @param path field name
     */
    public VirtualConstraintPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public Iterator<Node> iterator() {
        return null;
    }
}

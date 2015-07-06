package com.subrosagames.subrosa.api.web;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.subrosagames.subrosa.api.BadRequestException;
import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.domain.DomainObject;
import com.subrosagames.subrosa.domain.DomainObjectDescriptor;
import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.security.SecurityHelper;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Created by josiah on 3/3/15.
 */
@RestController
public abstract class AbstractCrudController<T extends DomainObject, U extends DomainObjectDescriptor> {

    private static final String PARENT_ID = "parentId";
    private static final String CHILD_ID = "childId";

    protected abstract List<T> listObjects(String parentId) throws DomainObjectNotFoundException;

    protected abstract T getObject(String parentId, String childId) throws DomainObjectNotFoundException;

    protected abstract T createObject(String parentId, U objectDescriptor);

    protected abstract String createdObjectLocation(String parentId, String childId);

    protected abstract T updateObject(String parentId, String childId, U objectDescriptor) throws DomainObjectNotFoundException;

    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<T> list(@PathVariable(PARENT_ID) String parentId,
                                 @RequestParam(value = "limitParam", required = false) Integer limitParam,
                                 @RequestParam(value = "offsetParam", required = false) Integer offsetParam)
            throws DomainObjectNotFoundException
    {
        int limit = ObjectUtils.defaultIfNull(limitParam, 0);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        List<T> objects = listObjects(parentId);
        if (CollectionUtils.isEmpty(objects)) {
            return new PaginatedList<>(Lists.<T>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<>(
                    objects.subList(offset, Math.min(objects.size() - 1, offset + limit)),
                    objects.size(),
                    limit, offset);
        }
    }

    @RequestMapping(value = { "/{childId}", "/{childId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public T get(@PathVariable(PARENT_ID) String parentId,
                 @PathVariable(CHILD_ID) String childId) throws DomainObjectNotFoundException
    {
        return getObject(parentId, childId);
    }

    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public T create(@PathVariable(PARENT_ID) String parentId,
                    @RequestBody(required = false) U objectDescriptor,
                    HttpServletResponse response)
            throws BadRequestException
    {
        if (objectDescriptor == null) {
            throw new BadRequestException("No POST body supplied");
        }
        T object = createObject(parentId, objectDescriptor);
        response.setHeader("Location", createdObjectLocation(parentId, object.identifier()));
        return object;
    }

    @RequestMapping(value = { "/{childId}", "/{childId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    public T update(@PathVariable(PARENT_ID) String parentId,
                    @PathVariable(CHILD_ID) String childId,
                    @RequestBody(required = false) U objectDescriptor) throws BadRequestException, NotAuthenticatedException, DomainObjectNotFoundException
    {
        if (objectDescriptor == null) {
            throw new BadRequestException("No POST body supplied");
        }
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to update a game.");
        }
        return updateObject(parentId, childId, objectDescriptor);
    }

}

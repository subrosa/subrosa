package com.subrosagames.subrosa.api;

import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.message.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/17/12
 * Time: 8:45 午後
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private GameRepository gameRepository;

    @RequestMapping(value = "/game/{gameId}/post", method = RequestMethod.GET)
    public PaginatedList<Post> retrievePosts(@PathVariable("gameId") Integer gameId,
                                             @RequestParam("limit") Integer limit,
                                             @RequestParam("offset") Integer offset) {
        LOG.debug("Getting posts for gameId {} with limit {} and offset {}", new Object[] { gameId, limit, offset });
        return new PaginatedList<Post>(
                gameRepository.getPosts(gameId, limit, offset),
                gameRepository.getPostCount(gameId),
                limit, offset);
    }

}

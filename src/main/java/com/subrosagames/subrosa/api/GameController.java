package com.subrosagames.subrosa.api;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GameController {

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameRepository gameRepository;

    @RequestMapping(value = "/game", method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Game> listGames(@RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "offset", required = false) Integer offset)
    {
        LOG.debug("Getting game list with limit {} and offset {}.", limit, offset);
        return new PaginatedList<Game>(
                gameRepository.getGames(limit, offset),
                gameRepository.getGameCount(),
                limit, offset);
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
    @ResponseBody
    public Game getGame(@PathVariable("gameId") String gameId) {
        LOG.debug("Getting game {} info", gameId);
        return gameRepository.getGame(Integer.valueOf(gameId));
    }

    @RequestMapping(value = "/game", method = RequestMethod.POST)
    @ResponseBody
    public Game createGame(@PathVariable("gameId") String gameId) {
        return gameRepository.getGame(Integer.valueOf(gameId));
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.PUT)
    @ResponseBody
    public Game updateGame(@PathVariable("gameId") String gameId) {
        return gameRepository.getGame(Integer.valueOf(gameId));
    }

}

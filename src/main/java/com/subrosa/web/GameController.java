package com.subrosa.web;

import com.subrosa.api.PaginatedList;
import com.subrosa.game.Game;
import com.subrosa.game.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class GameController {

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @RequestMapping("/index")
    @ResponseBody
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/game", method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Game> listGames(@RequestParam("limit") int limit,
                                         @RequestParam("offset") int offset)
    {
        LOG.debug("Handling game request.");
        PaginatedList<Game> gameList = new PaginatedList<Game>();
        gameList.setLimit(limit);
        gameList.setOffset(offset);
        gameList.setResults(gameService.getGames(limit, offset));
        return gameList;
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
    @ResponseBody
    public Game getGame(@PathVariable("gameId") String gameId) {
        LOG.debug("Getting game {} info", gameId);
        return gameService.getGame(Integer.valueOf(gameId));
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.POST)
    @ResponseBody
    public Game saveGame(@PathVariable("gameId") String gameId) {
        return gameService.getGame(Integer.valueOf(gameId));
    }

}

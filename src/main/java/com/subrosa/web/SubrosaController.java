package com.subrosa.web;

import com.subrosa.game.Game;
import com.subrosa.game.GameService;
import com.subrosa.vegas.VegasGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class SubrosaController {

    private static final Logger LOG = LoggerFactory.getLogger(SubrosaController.class);

    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/game", method = RequestMethod.GET)
    public Map<String, Object> listGames() {
        LOG.debug("Handling game request.");

        VegasGame game = new VegasGame();
        game.setStartTime(new Date());
        game.setEndTime(new Date());
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("games", Arrays.asList(game));
        return model;
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
    public Game getGame(@PathVariable("gameId") String gameId) {
        GameService gameService = new GameService();
        return gameService.getGame(Integer.valueOf(gameId));
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.POST)
    public Game saveGame(@PathVariable("gameId") String gameId) {
        GameService gameService = new GameService();
        return gameService.getGame(Integer.valueOf(gameId));
    }

    @InitBinder
    public void initDateBinder(final WebDataBinder dataBinder, final Locale locale) {
        final String dateformat = messageSource.getMessage("date.format", null, locale);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateformat);
        simpleDateFormat.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(simpleDateFormat, false));
    }

}

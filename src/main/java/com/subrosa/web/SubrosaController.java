package com.subrosa.web;

import com.subrosa.game.Game;
import com.subrosa.vegas.VegasGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SubrosaController {

    private static final Logger LOG = LoggerFactory.getLogger(SubrosaController.class);

    @RequestMapping("/index")
    public void index() {
        LOG.debug("Handling index request.");
    }

    @RequestMapping(value = "/game", method = RequestMethod.GET)
    public Map<String, Game> handleGame() {
        LOG.debug("Handling game request.");

        VegasGame game = new VegasGame();
        game.setName("Test Game");
        game.setStartTime(new Date());
        game.setEndTime(new Date());
        Map<String, Game> model = new HashMap<String, Game>();
        model.put("game", game);
        return model;
    }
}

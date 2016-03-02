package com.subrosagames.subrosa.api.admin;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;

/**
 * TODO
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private GameFactory gameFactory;

    @RequestMapping(value = { "/game/{url}/start", "/game/{url}/start/" }, method = RequestMethod.POST)
    public String startGame(@PathVariable String url) throws GameNotFoundException {
        StartGameCommand startGameCommand = new StartGameCommand();
        commandGateway.send(startGameCommand);
        Game game = gameFactory.getGame(url);
        game.startGame();
        return "OK";
    }

    public class StartGameCommand {
    }
}

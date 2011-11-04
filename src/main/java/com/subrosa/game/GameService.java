package com.subrosa.game;

import com.subrosa.game.model.GameDao;
import com.subrosa.game.model.GameModel;
import com.subrosa.vegas.VegasGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class); // NOPMD

    @Autowired
    private GameDao gameDao;

    public Game getGame(int gameId) {
        GameModel gameModel = gameDao.getGame(gameId);
        return new VegasGame(gameId);
    }
}

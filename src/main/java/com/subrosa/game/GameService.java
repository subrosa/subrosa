package com.subrosa.game;

import com.subrosa.game.model.GameDao;
import com.subrosa.game.model.GameModel;
import com.subrosa.vegas.VegasGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private GameDao gameDao;

    public List<Game> getGames(int limit, int offset) {
        return gameDao.getGames(limit, offset);
    }

    public Game getGame(int gameId) {
        return new VegasGame(gameDao.getGame(gameId));
    }
}

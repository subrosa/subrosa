package com.subrosa.vegas;

import com.subrosa.event.GameEvent;
import com.subrosa.game.AbstractGame;
import com.subrosa.game.GameRule;
import com.subrosa.game.Participant;
import com.subrosa.game.Player;
import com.subrosa.game.model.GameDao;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;


public class VegasGame extends AbstractGame {

    private GameDao gameDao;

    public VegasGame() {
    }

    public VegasGame(int id) {
        BeanUtils.copyProperties(gameDao.getGame(id), this);
    }

    @Override
    public String getName() {
        return "Vegas Game";
    }

    @Override
    public String getDescription() {
        return "Lucky man wins!";
    }

    @Override
    public List<? extends GameRule> getRules() {
        return Arrays.asList(
                new GameRule("Wait", "You do nothing."),
                new GameRule("Finish", "You do not pitch a fit when you do not win.")
        );
    }

    @Override
    public List<? extends GameEvent> getEvents() {
        return Arrays.asList(new RandomWinnerGameEndEvent());
    }

    @Override
    public List<? extends Participant> getPlayers() {
        Player player1 = new Player();
        player1.setName("player1");
        Participant player2 = new Player();
        player1.setName("player2");
        return Arrays.asList(player1, player2);
    }
}

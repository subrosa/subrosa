package com.subrosa.vegas;

import com.subrosa.event.GameEvent;
import com.subrosa.game.*;
import com.subrosagames.subrosa.domain.game.Participant;
import com.subrosagames.subrosa.domain.game.Player;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;

public class VegasGame extends AbstractGame {

    public VegasGame() {
    }

    public VegasGame(Game gameModel) {
        BeanUtils.copyProperties(gameModel, this);
    }

    @Override
    public String getName() {
        return "Vegas AssassinsGame";
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
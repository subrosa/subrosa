package com.subrosa.vegas;

import com.subrosa.game.Game;

import java.util.Random;

/**
 * TODO description.
 */
public class GameWinnerPicker {

    private int gameId;

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void pickWinner() {
        Game game = new VegasGame(gameId);
        System.out.println("winner: " + game.getPlayers().get(new Random().nextInt(2)).getName());
    }
}

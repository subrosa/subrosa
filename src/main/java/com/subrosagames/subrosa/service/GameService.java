package com.subrosagames.subrosa.service;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.player.InsufficientInformationException;
import com.subrosagames.subrosa.domain.player.PlayRestrictedException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.Team;

/**
 * Handles interactions between players and games.
 */
public interface GameService {

    /**
     * Update game with the given information.
     *
     * @param gameUrl        game url
     * @param gameDescriptor game information
     * @return updated game
     * @throws GameNotFoundException   if game is not found
     * @throws GameValidationException if game information is invalid
     */
    Game updateGame(String gameUrl, GameDescriptor gameDescriptor) throws GameValidationException, GameNotFoundException;

    /**
     * Publish the indicated game.
     *
     * @param gameUrl game url
     * @return published game
     * @throws GameValidationException if game is not valid for publishing
     * @throws GameNotFoundException   if specified game url does not map to a game
     */
    Game publishGame(String gameUrl) throws GameValidationException, GameNotFoundException;

    /**
     * Enroll the given account into a team with a team password.
     *
     * @param account      account to enroll
     * @param team         team into which to enroll
     * @param teamPassword team password
     * @return the resulting game player
     */
    Player enrollInTeam(Account account, Team team, String teamPassword);

    /**
     * Enroll the given account as a player of a game.
     *
     * @param account account to enroll
     * @param game    game into which to enroll
     * @return the resulting game player
     */
    Player enrollInGame(Account account, Game game);

    /**
     * Creates a game player for an account.
     *
     * @param gameUrl         game url
     * @param account         account to enroll
     * @param joinGameRequest join game request
     * @return created player
     * @throws GameNotFoundException            if game is not found
     * @throws InsufficientInformationException if required player information is missing
     * @throws PlayRestrictedException          if account does not satisfy game requirements
     * @throws PlayerValidationException        if player information is not valid
     */
    Player joinGame(String gameUrl, Account account, JoinGameRequest joinGameRequest) throws GameNotFoundException, PlayerValidationException;
}

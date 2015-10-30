package com.subrosagames.subrosa.domain.player;

import java.util.List;

import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.api.dto.TeamDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Factory for managing game players.
 */
public interface PlayerFactory {

    /**
     * Create a new player in a game.
     *
     * @param game             game
     * @param account          account
     * @param playerDescriptor player information
     * @return created player
     * @throws PlayerValidationException if player information is invalid
     * @throws AddressNotFoundException  if address is not found
     * @throws ImageNotFoundException    if image is not found
     */
    Player createPlayerForGame(Game game, Account account, PlayerDescriptor playerDescriptor) throws PlayerValidationException, AddressNotFoundException,
            ImageNotFoundException;

    /**
     * Get player in game.
     *
     * @param game     game
     * @param playerId player id
     * @return player in game
     * @throws PlayerNotFoundException if specified player is not in game
     */
    Player getPlayer(Game game, Integer playerId) throws PlayerNotFoundException;

    List<? extends Player> getPlayers(Game game, Integer limit, Integer offset);

    /**
     * Create a new team in a game.
     *
     * @param game game
     * @param teamDescriptor team information
     * @return created team
     */
    Team createTeamForGame(Game game, TeamDescriptor teamDescriptor);

    /**
     * Get team in game.
     *
     * @param game game
     * @param teamId team id
     * @return team in game
     * @throws TeamNotFoundException if specified team is not in game
     */
    Team getTeam(Game game, Integer teamId) throws TeamNotFoundException;

    List<? extends Team> getTeams(Game game);

    /**
     * Handles transformation of player attributes into persistable entities on the player.
     *
     * @param playerEntity     player entity
     * @param playerDescriptor player information
     * @throws ImageNotFoundException   if image is not found
     * @throws AddressNotFoundException if address is not found
     */
    void processPlayerAttributes(PlayerEntity playerEntity, PlayerDescriptor playerDescriptor) throws ImageNotFoundException, AddressNotFoundException;

}

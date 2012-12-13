package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.game.assassins.AssassinsGame;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameLifecycle;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.service.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory class for generating game domain objects.
 */
@Component
public class GameFactoryImpl implements GameFactory {

    @Autowired
    private GameRepository gameRepository;

    @Override
    public Game getGameForEntity(GameEntity gameEntity) {
        return new AssassinsGame(gameEntity);
    }

    @Override
    public Game getGameForId(int gameId) {
        AbstractGame game = new AssassinsGame(gameId);
        game.setGameRepository(gameRepository);
        return game;
    }

    @Override
    public Game createGame(GameEntity gameEntity, Lifecycle lifecycle) throws GameValidationException {
        AbstractGame game = new AssassinsGame(gameEntity, lifecycle);
        game.setGameRepository(gameRepository);
        game.validate();
        game.create();
        return game;
    }

    @Override
    public PaginatedList<Game> getGames(Integer limit, Integer offset) {
        return new PaginatedList<Game>(
                gameRepository.getGames(limit, offset),
                gameRepository.getGameCount(),
                limit, offset);
    }

    @Override
    public PaginatedList<Post> getPostsForGame(int gameId, int limit, int offset) {
        List<Post> posts = getGameForId(gameId).getPosts();
        return new PaginatedList<Post>(
                posts,
                posts.size(),
                limit, offset);

    }
}

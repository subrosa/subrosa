package com.subrosagames.subrosa.security.permission;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Evaluates read game permissions.
 */
@Component
public class ReadGamePermission extends AbstractPermission {

    private static final Logger LOG = LoggerFactory.getLogger(ReadGamePermission.class);

    @Autowired
    private GameRepository gameRepository;
    private GameFactory gameFactory;

    @Override
    public boolean isAllowed(Authentication authentication, Object target) {
        return isAdmin(authentication) || target instanceof Game && (
                ((Game) target).isPublished()
                        || ((Game) target).getOwner().getId().equals(((SubrosaUser) authentication.getPrincipal()).getAccount().getId())
        );
    }

    @Override
    public boolean isAllowed(Authentication authentication, Serializable id, String type) {
        try {
            return isAdmin(authentication) || type.equals("Game") && isAllowed(authentication, getGame(id));
        } catch (GameNotFoundException e) {
            LOG.warn("Failed to find game in permissions evaluation", e);
            return false;
        }
    }

    private Game getGame(Serializable id) throws GameNotFoundException {
        return gameRepository.get((String) id);
    }

    public void setGameFactory(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
    }
}

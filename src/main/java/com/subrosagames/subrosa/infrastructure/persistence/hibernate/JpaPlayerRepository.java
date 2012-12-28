package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TargetEntity;
import com.subrosagames.subrosa.domain.player.PlayerRepository;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 *
 */
@Repository
@Transactional
public class JpaPlayerRepository implements PlayerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createTarget(TargetEntity targetEntity) {
        entityManager.persist(targetEntity);
    }

    @Override
    public PlayerEntity getPlayer(Integer id) {
        return entityManager.find(PlayerEntity.class, id);
    }

    @Override
    public TeamEntity getTeam(Integer id) {
        return entityManager.find(TeamEntity.class, id);
    }

    @Override
    public void createTeam(TeamEntity teamEntity) {
        entityManager.persist(teamEntity);
    }

    @Override
    public void createPlayer(PlayerEntity playerEntity) {
        entityManager.persist(playerEntity);
    }
}

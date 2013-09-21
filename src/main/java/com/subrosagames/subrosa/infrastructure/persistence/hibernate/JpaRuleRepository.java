package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.RuleRepository;
import com.subrosagames.subrosa.domain.game.RuleType;
import com.subrosagames.subrosa.domain.game.persistence.RuleEntity;

/**
 * Manages storage and retrieval of game rules.
 */
@Repository
@Cacheable
public class JpaRuleRepository implements RuleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<? extends Rule> getRulesForType(RuleType ruleType) {
        return entityManager.createQuery("SELECT r FROM RuleEntity r WHERE r.ruleType = :ruleType", RuleEntity.class)
                .setParameter("ruleType", ruleType)
                .getResultList();
    }
}

package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.game.persistence.RuleEntity;

/**
 * TODO
 */
public interface RuleRepository extends DomainObjectRepository<RuleEntity, Integer> {

    /**
     * Retrieves rules for the given type.
     *
     * @param ruleType rule type
     * @return rules for type
     */
    List<Rule> findAllByRuleType(RuleType ruleType);
}

package com.subrosagames.subrosa.domain.game;

import java.util.List;

/**
 *
 */
public interface RuleRepository {

    List<? extends Rule> getRulesForType(RuleType ruleType);
}

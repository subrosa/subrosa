//package com.subrosagames.subrosa.domain.game.persistence;
//
//import com.subrosagames.subrosa.domain.game.Rule;
//import com.subrosagames.subrosa.domain.game.RuleType;
//
//import javax.persistence.Embeddable;
//import javax.persistence.EmbeddedId;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import java.io.Serializable;
//import java.util.List;
//
///**
// */
//@Entity
//public class RuleList {
//
//    @EmbeddedId
//    private RuleListId ruleListId;
//
//
//    private List<Rule> rules;
//    private RuleType ruleType;
//
//    public List<Rule> getRules() {
//        return rules;
//    }
//
//    public void setRules(List<Rule> rules) {
//        this.rules = rules;
//    }
//
//
//    @Embeddable
//    public static class RuleListId implements Serializable {
//
//        private Integer ruleId;
//        private RuleType ruleType;
//    }
//}

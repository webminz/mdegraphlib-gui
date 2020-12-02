package no.hvl.past.webui.transfer.quickRuleEngine.service;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.*;

import java.util.Collection;
import java.util.Set;

public interface RuleEngineConfigurationService {

    // Domain

    Collection<UserInteraction> possibleUserEvents(String ruleEngineID);

    Collection<WorldObject> existingObjects(String ruleEngineID);

    WorldRenderer getRenderer(String ruleEngineID);

    // Rule Tree interactions

    void createRuleEngineIfNotExists(String ruleEngineID, String domainID);

    boolean renameRule(String ruleEngineID, String oldRuleName, String newRuleName);

    RuleHierarchy getRuleHierarchy(String ruleEngineID);

    void createRuleGroup(String ruleEngineID, String parentGroupName, String groupName);

    void moveElementIntoGroup(String ruleEngineID, String elementName, String parentName);

    void setGroupModeSequential(String ruleEngineID, String groupName);

    void setGroupModeAlternative(String ruleEngineID, String groupName);

    void setGroupModeParallel(String ruleEngineID, String groupName);

    // Updaters for a single rule

    void createOrUpdateRule(String ruleEngineID, String ruleGroup, String ruleName, World pattern, Set<Thing> insertions, Set<Thing> deletions);

    void addEventTriggerForRule(String ruleEngineID, String currentRule, Event displays);


    // Getters for a single rule

    Collection<Event> eventTriggers(String ruleEngineID, String ruleName);

    World getRuleContextPattern(String ruleEngineID, String ruleName);

    Collection<Thing> getRuleDeletes(String ruleEngineID, String ruleName);

    Collection<Thing> getRuleInserts(String ruleEngineID, String ruleName);
}


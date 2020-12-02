package no.hvl.past.webui.transfer.quickRuleEngine.service;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.UserInteraction;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public interface RuleEngineInteractionService {

    World getCurrentState(String ruleEngineID);

    void userInteractionHappened(String ruleEngineID, UserInteraction interaction);

    void registerForServerSideEvents(String ruleEngineID, Consumer<World> eventHandler); // TODO offer a diff-based variant

    Collection<UserInteraction> possibleUserEvents(String ruleEngineID);

    WorldRenderer getRenderer(String ruleEngineID);

    void createRuleEngineIfNotExists(String ruleEngineID, String domainID);

}

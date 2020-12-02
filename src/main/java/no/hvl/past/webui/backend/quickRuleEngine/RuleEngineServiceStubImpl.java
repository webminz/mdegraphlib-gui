package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.backend.quickRuleEngine.pacman.PacmanDomain;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.*;
import no.hvl.past.webui.transfer.quickRuleEngine.service.RuleEngineConfigurationService;
import no.hvl.past.webui.transfer.quickRuleEngine.service.RuleEngineInteractionService;
import no.hvl.past.webui.transfer.quickRuleEngine.service.WorldRenderer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Service
@Scope("singleton")
public class RuleEngineServiceStubImpl implements RuleEngineInteractionService, RuleEngineConfigurationService {

    public static final String PACMAN_DOMAIN = "PACMAN";

    private final Map<String, RuleEngine> engines;

    public RuleEngineServiceStubImpl() {
        this.engines = new HashMap<>();
    }

    @Override
    public World getCurrentState(String ruleEngineID) {
        return this.engines.get(ruleEngineID).getCurrentState();
    }

    @Override
    public void userInteractionHappened(String ruleEngineID, UserInteraction interaction) {
        this.engines.get(ruleEngineID).getEventQueue().add(interaction);
    }

    @Override
    public void registerForServerSideEvents(String ruleEngineID, Consumer<World> eventHandler) {
        this.engines.get(ruleEngineID).registerForUpdates(eventHandler);
    }

    @Override
    public Collection<UserInteraction> possibleUserEvents(String ruleEngineID) {
        return this.engines.get(ruleEngineID).getObservableEvents().stream().filter(event -> event instanceof UserInteraction).map(event -> (UserInteraction)event).collect(Collectors.toSet());
    }

    @Override
    public Collection<WorldObject> existingObjects(String ruleEngineID) {
        return this.engines.get(ruleEngineID).getInstantiableThings().stream().filter(thing -> thing instanceof WorldObject).map(thing -> (WorldObject) thing).collect(Collectors.toList());
    }

    @Override
    public WorldRenderer getRenderer(String ruleEngineID) {
        return this.engines.get(ruleEngineID).getRenderer();
    }

    @Override
    public void createOrUpdateRule(String ruleEngineID, String ruleGroupName, String ruleName, World pattern, Set<Thing> insertions, Set<Thing> deletions) {
        Rule rule = this.engines.get(ruleEngineID).getOrCreateRule(ruleGroupName, ruleName);
        rule.setContext(new Pattern(pattern));
        rule.setInsertions(insertions);
        rule.setDeletions(deletions);
    }

    @Override
    public void addEventTriggerForRule(String ruleEngineID, String currentRule, Event displays) {
        Rule rule = this.engines.get(ruleEngineID).getOrCreateRule(null, currentRule);
        if (rule.getTrigger() instanceof EventTrigger) {
            ((EventTrigger) rule.getTrigger()).addEvent(displays);
        } else {
            rule.setTrigger((new EventTrigger<>(this.engines.get(ruleEngineID), displays)));
        }
    }

    @Override
    public boolean renameRule(String ruleEngineID, String oldRuleName, String newRuleName) {
        return this.engines.get(ruleEngineID).renameRuleOrGroup(oldRuleName, newRuleName);
    }

    @Override
    public void createRuleEngineIfNotExists(String ruleEngineID, String domainID) {
        if (!this.engines.containsKey(ruleEngineID)) {
            this.engines.put(ruleEngineID,createRuleEngine(ruleEngineID, domainID));
        }
    }


    @Override
    public RuleHierarchy getRuleHierarchy(String ruleEngineID) {
        return this.engines.get(ruleEngineID).getRules().getHierarchy(null);
    }

    @Override
    public void createRuleGroup(String ruleEngineID, String parentGroupName, String groupName) {
        this.engines.get(ruleEngineID).getOrCreateRule(parentGroupName, groupName);
    }

    @Override
    public void moveElementIntoGroup(String ruleEngineID, String elementName, String parentName) {
        this.engines.get(ruleEngineID).moveElementIntoGroup(elementName, parentName);
    }

    @Override
    public void setGroupModeSequential(String ruleEngineID, String groupName) {
        this.engines.get(ruleEngineID).setRuleGroupMode(groupName, RuleGroup.ExecutionType.SEQUENCE);
    }

    @Override
    public void setGroupModeAlternative(String ruleEngineID, String groupName) {
        this.engines.get(ruleEngineID).setRuleGroupMode(groupName, RuleGroup.ExecutionType.ALTERNATIVE);
    }

    @Override
    public void setGroupModeParallel(String ruleEngineID, String groupName) {
        this.engines.get(ruleEngineID).setRuleGroupMode(groupName, RuleGroup.ExecutionType.PARALLEL);
    }

    @Override
    public Collection<Event> eventTriggers(String ruleEngineID, String ruleName) {
        Trigger trigger = this.engines.get(ruleEngineID).getOrCreateRule(null, ruleName).getTrigger();
        if (trigger != null && trigger instanceof EventTrigger<?>) {
            return ((EventTrigger) trigger).getReactsOn();
        }
        return Collections.emptySet();
    }

    @Override
    public World getRuleContextPattern(String ruleEngineID, String ruleName) {
        return this.engines.get(ruleEngineID).getOrCreateRule(null, ruleName).getContext().getWorldExcerpt();
    }


    @Override
    public Collection<Thing> getRuleDeletes(String ruleEngineID, String ruleName) {
        return this.engines.get(ruleEngineID).getOrCreateRule(null,ruleName).getDeletions();
    }

    @Override
    public Collection<Thing> getRuleInserts(String ruleEngineID, String ruleName) {
        return this.engines.get(ruleEngineID).getOrCreateRule(null, ruleName).getInsertions();
    }


    private RuleEngine createRuleEngine(String ruleEngineID, String domainID) {
        if (domainID.equals(PACMAN_DOMAIN)) {
            // TODO dependency cycle, fix with Dependency injection
            Domain domain = new PacmanDomain();

            RuleEngine ruleEngine = new RuleEngine(ruleEngineID, domain.startWorld());
            ruleEngine.setObservableEvents(domain.interactions());
            ruleEngine.setInstantiableThings(domain.objects());
            ruleEngine.setRenderer(domain.renderer());
          //  domain.domainSpecificRuleSetUp(ruleEngine);

            ruleEngine.start();
            return ruleEngine;
        } else {
            RuleEngine engine = new RuleEngine(ruleEngineID, new World(Collections.emptySet()));
            engine.start();
            return engine;
        }
    }






}

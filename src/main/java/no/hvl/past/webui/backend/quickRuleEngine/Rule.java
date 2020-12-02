package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Event;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.RuleHierarchy;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.Thing;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class Rule extends AbstractRule {

    public static class RuleFired extends Event {
        private final String ruleName;

        public RuleFired(String ruleName) {
            super("Rule \"" + ruleName + "\" fired event");
            this.ruleName = ruleName;
        }
    }

    private final RuleEngine engine;
    private String name;
    private Trigger trigger;
    private Pattern context;
    private Set<Thing> insertions;
    private Set<Thing> deletions;


    public Rule(RuleEngine engine, String name, Trigger trigger, Pattern context, Set<Thing> insertions, Set<Thing> deletions) {
        this.engine = engine;
        this.name = name;
        this.trigger = trigger;
        this.context = context;
        this.insertions = insertions;
        this.deletions = deletions;
    }

    @Override
    public World apply(World current) {
        this.trigger.triggerProcessed();
        Optional<Binding> match = this.context.match(current);
        if (match.isPresent()) {
            World result = current.copy();
            match.get().insert(insertions, result);
            match.get().delete(deletions, result);
            System.out.println(this.engine.getRuleEngineID() + ": Triggered rule " + this.name + " executed");
            engine.getEventQueue().add(new RuleFired(name));
            return result;
        } else {
            System.out.println(this.engine.getRuleEngineID() +": Triggered rule " + this.name + " could not be applied, since there was no match");
        }
        return current;
    }

    @Override
    public boolean isTriggered() {
        return this.trigger.isTriggered();
    }

    @Override
    public void deny() {
        System.out.println(this.engine.getRuleEngineID()+ ": Execution of triggered rule " + this.name + " denied");
        this.trigger.triggerProcessed();
    }

    @Override
    public RuleHierarchy getHierarchy(RuleHierarchy parent) {
        return new RuleHierarchy(getName(), parent, RuleHierarchy.RuleHierarchyItemType.RULE, Collections.emptyList());
    }

    @Override
    public Optional<Rule> findRule(String ruleName) {
        if (ruleName.equals(this.name)) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public Optional<RuleGroup> findGroup(String groupName) {
        return Optional.empty();
    }

    @Override
    public boolean hasMatch(World inWorld) {
        return this.context.match(inWorld).isPresent();
    }

    public RuleEngine getEngine() {
        return engine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public Pattern getContext() {
        return context;
    }

    public Set<Thing> getInsertions() {
        return insertions;
    }

    public Set<Thing> getDeletions() {
        return deletions;
    }

    public void setContext(Pattern context) {
        this.context = context;
    }

    public void setInsertions(Set<Thing> insertions) {
        this.insertions = insertions;
    }

    public void setDeletions(Set<Thing> deletions) {
        this.deletions = deletions;
    }
}

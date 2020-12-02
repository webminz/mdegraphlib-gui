package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Event;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.Thing;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;
import no.hvl.past.webui.transfer.quickRuleEngine.service.WorldRenderer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;

public class RuleEngine {

    private final String ruleEngineID;
    private RuleGroup rules;
    private World currentState;
    private final EventQueue eventQueue;
    private boolean running;
    private List<Consumer<World>> worldUpdateListeners;
    private List<Consumer<Event>> eventListeners;
    private LocalDateTime lastTriggerEvent = LocalDateTime.MIN;

    private Collection<Event> observableEvents;
    private Collection<? extends Thing> instantiableThings;
    private WorldRenderer renderer;

    private static final long EPSILON = 10; // ms

    public RuleEngine(String ruleEngineID, World currentState) {
        this.ruleEngineID = ruleEngineID;
        this.currentState = currentState;
        this.rules = new RuleGroup("");
        this.eventQueue = new EventQueue();
        this.running = false;
        this.worldUpdateListeners = new ArrayList<>();
        this.eventListeners = new ArrayList<>();
        this.observableEvents = Collections.emptySet();
        this.instantiableThings = Collections.emptySet();
        this.renderer = WorldRenderer.EMPTY_RENDERER;
    }

    public void start() {
        this.running = true;
        new Thread(() -> {
            while (running) {
                try {
                    Event event = getEventQueue().remove();
                    handle(event);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handle(Event event) {
        this.eventListeners.forEach(l -> l.accept(event));
        if (event instanceof Trigger.TriggerEvent) {
            LocalDateTime triggerTime = ((Trigger.TriggerEvent) event).getTimestamp();
            if (triggerTime.isAfter(lastTriggerEvent.plus(EPSILON, ChronoUnit.MILLIS))) {
                try {
                    executeRules();
                } catch (Throwable throwable) {
                    System.out.println(this.getRuleEngineID() + ": Rule execution resulted in an exception: " + throwable.getMessage());
                    throwable.printStackTrace();
                }
                this.lastTriggerEvent = triggerTime;
            }
        }
    }

    private Rule createRule(String inGroup, String ruleName) {
        Rule rule = new Rule(this, ruleName, Trigger.always(this), new Pattern(), new HashSet<>(), new HashSet<>());
        if (inGroup == null || inGroup.isEmpty()) {
            this.rules.addChild(rule);
            return rule;
        } else {
            RuleGroup group = this.getOrCreateGroup("", inGroup);
            group.addChild(rule);
            return rule;
        }
    }

    private RuleGroup createGroup(String inParentGroup, String groupName) {
        RuleGroup group = new RuleGroup(groupName);
        if (inParentGroup == null || inParentGroup.isEmpty()) {
            this.rules.addChild(group);
            return group;
        } else {
            RuleGroup parent = this.getOrCreateGroup("", inParentGroup);
            parent.addChild(group);
            return group;
        }
    }

    public void registerForEvents(Consumer<Event> listener) {
        this.eventListeners.add(listener);
    }

    public void registerForUpdates(Consumer<World> listener) {
        this.worldUpdateListeners.add(listener);
    }

    public void executeRules() {
        this.currentState = this.rules.apply(currentState);
        this.worldUpdateListeners.forEach(c -> c.accept(currentState));
    }

    public boolean renameRuleOrGroup(String oldRuleName, String newRuleName) {
        if (this.rules.findRule(newRuleName).isPresent() || this.rules.findGroup(newRuleName).isPresent()) {
            return false;
        } else {
            this.rules.findRule(oldRuleName).ifPresent(r -> r.setName(newRuleName));
            this.rules.findGroup(oldRuleName).ifPresent(g -> g.setName(newRuleName));
            return true;
        }
    }

    public Rule getOrCreateRule(String ruleGroupName, String ruleName) {
        return this.rules.findRule(ruleName).orElseGet(() -> this.createRule(ruleGroupName, ruleName));
    }

    public RuleGroup getOrCreateGroup(String inGroup, String groupName) {
        return this.rules.findGroup(groupName).orElseGet(() -> this.createGroup(inGroup, groupName));
    }

    public void setRuleGroupMode(String ruleGroup, RuleGroup.ExecutionType type) {
        this.rules.findGroup(ruleGroup).ifPresent(g -> g.setExecution(type));
    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // Setters and getters

    public String getRuleEngineID() {
        return ruleEngineID;
    }

    public RuleGroup getRules() {
        return rules;
    }

    public World getCurrentState() {
        return currentState;
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    public Collection<Event> getObservableEvents() {
        return observableEvents;
    }

    public void setObservableEvents(Collection<Event> observableEvents) {
        this.observableEvents = observableEvents;
    }

    public Collection<? extends Thing> getInstantiableThings() {
        return instantiableThings;
    }

    public void setInstantiableThings(Collection<? extends Thing> instantiableThings) {
        this.instantiableThings = instantiableThings;
    }

    public WorldRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(WorldRenderer renderer) {
        this.renderer = renderer;
    }


    public void moveElementIntoGroup(String elementName, String parentName) {
        Rule rule = this.getOrCreateRule("", elementName);
        RuleGroup oldParent = this.rules.findParent(rule);
        oldParent.removeChild(rule);
        RuleGroup newParent = this.getOrCreateGroup("", parentName);
        newParent.addChild(rule);
    }
}

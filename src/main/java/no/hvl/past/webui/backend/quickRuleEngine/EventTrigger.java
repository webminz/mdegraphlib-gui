package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EventTrigger<E extends Event> extends AbstractEventTrigger<E> {

    private Set<E> reactsOn;

    public EventTrigger(RuleEngine engine, E... reactsOn) {
        super(engine);
        this.reactsOn = new HashSet<>(Arrays.asList(reactsOn));
    }

    public void addEvent(E event) {
        this.reactsOn.add(event);
    }

    @Override
    protected boolean reactsOn(E event) {
        return reactsOn.contains(event);
    }

    public Set<E> getReactsOn() {
        return reactsOn;
    }
}

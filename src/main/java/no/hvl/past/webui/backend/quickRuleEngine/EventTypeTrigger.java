package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EventTypeTrigger<E extends Event> extends AbstractEventTrigger<E> {

    private Set<Class<? extends E>> reactsOnTypes;

    public EventTypeTrigger(RuleEngine engine, Class<? extends E>... types) {
        super(engine);
        this.reactsOnTypes = new HashSet<>(Arrays.asList(types));
    }

    @Override
    protected boolean reactsOn(E event) {
        return reactsOnTypes.stream().anyMatch(c -> c.isAssignableFrom(event.getClass()));
    }
}

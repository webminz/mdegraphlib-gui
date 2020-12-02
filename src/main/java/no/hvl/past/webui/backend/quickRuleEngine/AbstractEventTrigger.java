package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Event;

import java.util.function.Consumer;

public abstract class AbstractEventTrigger<E extends Event> extends Trigger implements Consumer<Event> {

    public AbstractEventTrigger(RuleEngine engine) {
        super(engine);
        engine.registerForEvents(this);
    }

    @Override
    public void accept(Event e) {
        try {
            if (reactsOn((E) e)) {
                trigger();
            }
        } catch (ClassCastException ex) {
            // Nothing to do here
        }
    }

    protected abstract boolean reactsOn(E event);
}

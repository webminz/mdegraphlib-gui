package no.hvl.past.webui.backend.quickRuleEngine;


import no.hvl.past.webui.transfer.quickRuleEngine.domain.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public abstract class Trigger {

    public static class TriggerEvent extends Event {
        private final LocalDateTime timestamp;
        private TriggerEvent(LocalDateTime timestamp) {
            super("Trigger event at " + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            this.timestamp = timestamp;
        }
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    public static class AlwaysTrigger extends Trigger {

        private AlwaysTrigger(RuleEngine engine) {
            super(engine);
        }

        @Override
        public boolean isTriggered() {
            return true;
        }
    }

    public static class NeverTrigger extends Trigger {

        private NeverTrigger(RuleEngine engine) {
            super(engine);
        }

        @Override
        public boolean isTriggered() {
            return false;
        }
    }

    public static class ManualTrigger extends Trigger {

        private boolean value;

        private ManualTrigger(boolean value, RuleEngine engine) {
            super(engine);
            this.value = value;
        }

        public void setValue(boolean isTriggered) {
            this.value = isTriggered;
        }

        @Override
        public boolean isTriggered() {
            return value;
        }
    }

    private final RuleEngine engine;
    private boolean isTriggered;


    public Trigger(RuleEngine engine) {
        this.engine = engine;
        this.isTriggered = false;
    }

    public boolean isTriggered() {
        return isTriggered;
    }

    public void trigger() {
        this.isTriggered = true;
        if (!engine.getEventQueue().peekLast().map(e -> e instanceof TriggerEvent).orElse(false)) {
            engine.getEventQueue().add(new TriggerEvent(LocalDateTime.now()));
        }
    }

    void triggerProcessed() {
        this.isTriggered = false;
    }

    public RuleEngine getEngine() {
        return engine;
    }


    public static AlwaysTrigger always(RuleEngine engine) {
        return new AlwaysTrigger(engine);
    }

    public static NeverTrigger never(RuleEngine engine) {
        return new NeverTrigger(engine);
    }

    public static ManualTrigger manual(RuleEngine engine, boolean triggerValue) {
        return new ManualTrigger(triggerValue, engine);
    }

}

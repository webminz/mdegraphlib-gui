package no.hvl.past.webui.transfer.quickRuleEngine.domain;


public abstract class Event extends Thing {

    public Event(String name) {
        super(name);
    }

    @Override
    public boolean isMatchableWith(Thing other) {
        if (other instanceof Event) {
            return this.equals(other);
        }
        return false;
    }
}

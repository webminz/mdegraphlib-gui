package no.hvl.past.webui.backend.quickRuleEngine;

public abstract class Timer extends Trigger {

    private boolean isActive;

    public Timer(RuleEngine engine) {
        super(engine);
    }

    public void activate() {
        if (isActive) {
            return;
        }
        isActive = true;
        this.performStartup();
    }

    public void deactivate() {
        if (!isActive) {
            return;
        }
        isActive = false;
        this.terminate();
    }

    protected abstract void terminate();

    protected abstract void performStartup();

    public boolean isActive() {
        return isActive;
    }
}

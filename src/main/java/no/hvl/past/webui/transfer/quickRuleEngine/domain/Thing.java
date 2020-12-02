package no.hvl.past.webui.transfer.quickRuleEngine.domain;

public abstract class Thing {

    private String name;

    public Thing(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public abstract boolean isMatchableWith(Thing other);
}

package no.hvl.past.webui.backend.quickRuleEngine.pacman;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldObject;

public abstract class LabyrinthElement extends WorldObject {

    public static class Corridor extends LabyrinthElement {
        public Corridor() {
            super("Corridor");
        }
    }

    public static class Wall extends LabyrinthElement {
        public Wall() {
            super("Wall");
        }
    }

    public LabyrinthElement(String name) {
        super(name, false);
    }
}

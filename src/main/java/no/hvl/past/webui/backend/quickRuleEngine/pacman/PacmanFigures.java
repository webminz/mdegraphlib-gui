package no.hvl.past.webui.backend.quickRuleEngine.pacman;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldObject;

public abstract class PacmanFigures extends WorldObject {

    public enum GhostColor {
        ORANGE,
        TEAL,
        RED,
        ROSE
    }

    public static class Marble extends PacmanFigures {

        public Marble() {
            super("Marble", false);
        }
    }

    public static class Pacman extends PacmanFigures {

        public Pacman() {
            super("Pacman", true);
        }
    }

    public static abstract class Ghost extends PacmanFigures {

        private GhostColor color;

        public Ghost(GhostColor color) {
            super(color.name() + " Ghost", true);
            this.color = color;
        }

        public GhostColor getColor() {
            return color;
        }
    }

    public static class RedGhost extends Ghost {

        public RedGhost() {
            super(GhostColor.RED);
        }
    }

    public static class OrangeGhost extends Ghost {

        public OrangeGhost() {
            super(GhostColor.ORANGE);
        }
    }

    public static class TealGhost extends  Ghost {

        public TealGhost() {
            super(GhostColor.TEAL);
        }
    }

    public static class RoseGhost extends Ghost {


        public RoseGhost() {
            super(GhostColor.ROSE);
        }
    }

    public PacmanFigures(String name, boolean isSingleton) {
        super(name, isSingleton);
    }
}

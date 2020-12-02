package no.hvl.past.webui.transfer.quickRuleEngine.domain;


import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public abstract class UserInteraction extends Event {

    public enum MouseInteractionType {
        CLICK,
        RIGHT_CLICK,
        DOUBLE_CLICK,
        HOVER
    }

    public enum Buttons {
        ENTER,
        SPACE,
        BACKSPACE,
        DELETE,
        INSERT,
        TAB,
        ESCAPE,
        SHIFT,
        CAPSLOCK,
        LEFT,
        UP,
        RIGHT,
        DOWN,
        META_KEY,
        CONTROL,
        ALT,
        CHARACTER
    }

    public UserInteraction(String name) {
        super(name);
    }

    public static class ButtonPress extends UserInteraction {

        private final Set<Buttons> buttonsPressed;
        private final String characters;

        public ButtonPress(Set<Buttons> buttonsPressed, String characters) {
            super(print(buttonsPressed, characters));
            this.buttonsPressed = buttonsPressed;
            this.characters = characters;
        }

        private static String print(Set<Buttons> buttonsPressed, String characters) {
            if (buttonsPressed.contains(Buttons.CHARACTER)) {
                return "\"" + characters + "\" pressed event";
            }
            if (buttonsPressed.size() == 1) {
                Buttons pressed = buttonsPressed.iterator().next();
                return pressed.name() + " pressed event";
            }
            StringJoiner stringJoiner = new StringJoiner(", ", "\"", "\"");
            stringJoiner.setEmptyValue("");
            buttonsPressed.stream().map(Object::toString).forEach(stringJoiner::add);
            return "Multiple buttons: " + stringJoiner.toString() + " pressed event";
        }

        public Set<Buttons> getButtonsPressed() {
            return buttonsPressed;
        }

        public String getCharacters() {
            return characters;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ButtonPress) {
                ButtonPress bp = (ButtonPress) obj;
                return this.buttonsPressed.equals(bp.buttonsPressed) && this.characters.equals(bp.characters);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.buttonsPressed.hashCode() ^ this.characters.hashCode();
        }
    }

    public static class MouseInteraction extends UserInteraction {

        private final MouseInteractionType type;
        private final Set<Thing> interactedWith;

        public MouseInteraction(MouseInteractionType type, Set<Thing> interactedWith) {
            super(print(type, interactedWith));
            this.type = type;
            this.interactedWith = interactedWith;
        }

        private static String print(MouseInteractionType type, Set<Thing> interactedWith) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            stringJoiner.setEmptyValue("");
            interactedWith.stream().map(Thing::getName).forEach(stringJoiner::add);
            return type.name() + " on " + stringJoiner.toString();
        }
    }

}

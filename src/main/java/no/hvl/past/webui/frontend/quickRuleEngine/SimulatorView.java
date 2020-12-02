package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import no.hvl.past.webui.backend.quickRuleEngine.pacman.PacmanFigures;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.*;
import no.hvl.past.webui.transfer.quickRuleEngine.service.RuleEngineInteractionService;
import no.hvl.past.webui.transfer.quickRuleEngine.service.WorldRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Consumer;

@Route("forskergrandprix/pacman") // TODO make more generic path names later
@CssImport("animate.css/animate.css")
@Push
public class SimulatorView extends Div implements HasUrlParameter<String>, Consumer<World> {

    private static final String TO_RULE_CONFIG_NO = "Konfigurer Regler";

    private RuleEngineInteractionService service;
    private WorldRenderer renderer;
    private List<SimulatorViewGridRow> rows;
    private List<Image> buttons;
    private VerticalLayout rowContainer;
    private HorizontalLayout contextContainer;
    private Map<Integer, SimulatorViewGridCell> cells;
    private int width;
    private int height;
    private String ruleEngineID;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String ruleEngineID) {
        this.ruleEngineID = ruleEngineID;
        if (ruleEngineID != null && !ruleEngineID.isEmpty()) {
            this.service.createRuleEngineIfNotExists(ruleEngineID, "PACMAN");
            this.wireUp();
        }
    }

    private static class SimulatorViewGridRow extends HorizontalLayout  {
        private int y;
        private List<SimulatorViewGridCell> cells;

        public SimulatorViewGridRow(int y) {
            this.y = y;
            this.cells = new ArrayList<>();
            this.setSpacing(false);
            this.setPadding(false);
            this.addClassName("row");
        }

        public void addCell(Map<Integer, SimulatorViewGridCell> registry, int maxWidth) {
            SimulatorViewGridCell cell = new SimulatorViewGridCell(cells.size(), y);
            registry.put(maxWidth * y + cells.size(), cell);
            this.cells.add(cell);
            add(cell);
        }

        public void clear() {
            this.cells.clear();
            removeAll();
        }


    }
    private static class SimulatorViewGridCell extends Div {

        private int x;
        private int y;

        private String backgroundImage;
        private List<String> foregroundImages;

        public SimulatorViewGridCell(int x, int y) {
            this.x = x;
            this.y = y;
            addClassName("cell");
            this.foregroundImages = new ArrayList<>();
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setBackgroundImage(String imageUrl) {
            this.backgroundImage = imageUrl;
                getStyle().set("background-image", "url(" + backgroundImage + ")");
                getStyle().set("background-size", "contain");

        }

        public void addForegroundImage(String imageUrl) {
            if (backgroundImage != null) {
                if (foregroundImages.isEmpty()) {
                    getStyle().set("background-image", "url(" + imageUrl + "), url(" + backgroundImage + ")");
                    getStyle().set("background-size", "auto, contain");
                    this.foregroundImages.add(imageUrl);
                } else if (foregroundImages.size() == 1) {
                    getStyle().set("background-image", "url(" + foregroundImages.get(0) + "), url(" + imageUrl + "), url(" + backgroundImage + ")");
                    getStyle().set("background-size", "auto, auto, contain");
                    getStyle().set("background-position", "center top, center bottom");
                    this.foregroundImages.add(imageUrl);
                }
            } else {
                getStyle().set("background-image", "url(" + imageUrl + ")");
                getStyle().set("background-size", "auto");
            }
        }
    }


    public SimulatorView(@Autowired RuleEngineInteractionService service) {
        this.service = service;
        setSizeFull();
        addClassName("world-simulator");
        this.rows = new ArrayList<>();
        this.buttons = new ArrayList<>();

        this.rowContainer = new VerticalLayout();
        this.rowContainer.addClassName("grid");
        this.rowContainer.setSpacing(false);
        this.rowContainer.setPadding(false);
        this.rowContainer.setHeight("90%");
        add(rowContainer);

        this.contextContainer = new HorizontalLayout();
        this.contextContainer.addClassName("context-menu");
        this.contextContainer.setHeight("10%");

        add(contextContainer);
    }

    private void wireUp() {
        this.renderer = service.getRenderer(this.ruleEngineID);

        service.possibleUserEvents(ruleEngineID)
                .stream().filter(ui -> ui instanceof UserInteraction.ButtonPress)
                .map(ui -> (UserInteraction.ButtonPress) ui)
                .forEach(this::addUIButton);

        service.registerForServerSideEvents(this.ruleEngineID, this);

        this.show(this.renderer.render(service.getCurrentState(this.ruleEngineID)));

        add(new SwitcherButton(TO_RULE_CONFIG_NO, click -> {
            getUI().ifPresent(ui -> ui.navigate(RuleEditorView.class, this.ruleEngineID));

        }));
    }


    private void buttonPressed(Image trigger, UserInteraction.ButtonPress btn) {
        this.buttons.forEach(component -> {
            component.removeClassName("animate__animated");
            component.removeClassName("animate__heartBeat");
        });
        trigger.addClassName("animate__animated");
        trigger.addClassName("animate__heartBeat");
        service.userInteractionHappened(ruleEngineID, btn);
    }

    private void addUIButton(UserInteraction.ButtonPress buttonPress) {
        WorldRepresentation.VisualElement rendered = this.renderer.render(buttonPress);
        Image img = new Image(rendered.getImageUrl(), "");
        createKeyBinding(buttonPress, img);
        this.buttons.add(img);
        this.contextContainer.add(img);
    }

    private void createKeyBinding(UserInteraction.ButtonPress buttonPress, Image img) {
        img.addClickListener(click -> {
            buttonPressed(img, buttonPress);
        });
        Set<UserInteraction.Buttons> pressed = new HashSet<>(buttonPress.getButtonsPressed());
        KeyModifier[] modifiers = checkForModifiers(pressed);
        Key key = translateKey(pressed, buttonPress.getCharacters());
        if (key != null) {
            Shortcuts.addShortcutListener(img, () -> buttonPressed(img, buttonPress), key, modifiers);
        }
    }

    private KeyModifier[] checkForModifiers(Set<UserInteraction.Buttons> pressed) {
        Set<KeyModifier> mod = new HashSet<>();
        if (pressed.contains(UserInteraction.Buttons.CONTROL)) {
            mod.add(KeyModifier.CONTROL);
            pressed.remove(UserInteraction.Buttons.CONTROL);
        }
        if (pressed.contains(UserInteraction.Buttons.META_KEY)) {
            mod.add(KeyModifier.META);
            pressed.remove(UserInteraction.Buttons.META_KEY);
        }
        if (pressed.contains(UserInteraction.Buttons.ALT)) {
            mod.add(KeyModifier.ALT);
            pressed.remove(UserInteraction.Buttons.ALT);
        }
        // TODO alt graph
        if (pressed.contains(UserInteraction.Buttons.SHIFT)) {
            mod.add(KeyModifier.SHIFT);
            pressed.remove(UserInteraction.Buttons.SHIFT);
        }
        KeyModifier[] modifiers = new KeyModifier[mod.size()];
        return mod.toArray(modifiers);
    }

    private Key translateKey(Set<UserInteraction.Buttons> buttonPress, String chars) {
        if (chars != null && chars.length() == 1) {
            switch (chars.charAt(0)) {
                case 'a':
                case 'A':
                    return Key.KEY_A;
                case 'b':
                case 'B':
                    return Key.KEY_B;
                    // TODO to be continued
                default:
                    return null;

            }
        } else if (buttonPress.size() == 1) {
            switch (buttonPress.iterator().next()) {
                case DOWN:
                    return Key.ARROW_DOWN;
                case LEFT:
                    return Key.ARROW_LEFT;
                case RIGHT:
                    return Key.ARROW_RIGHT;
                case UP:
                    return Key.ARROW_UP;
                case ENTER:
                    return Key.ENTER;
                case ESCAPE:
                    return Key.ESCAPE;
                case CAPSLOCK:
                    return Key.CAPS_LOCK;
                case DELETE:
                    return Key.DELETE;
                case INSERT:
                    return Key.INSERT;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }


    private void show(WorldRepresentation render) {
        if (render instanceof GridWorld) {
            this.show((GridWorld)render);
        }
    }


    private void show(GridWorld world) {
        clear();
        if (world.getWidth() <= 8 && world.getHeight() <= 8) {
            this.rowContainer.addClassName("small");
        } else if (world.getWidth() <= 32 && world.getHeight() <= 32) {
            this.rowContainer.addClassName("medium");
        } else {
            this.rowContainer.addClassName("big");
        }

        this.width = world.getWidth();
        this.height = world.getHeight();
        this.cells = new HashMap<>();

        for (int i = 0; i < world.getHeight(); i++) {
            this.addRow(world.getWidth());
        }

        for (GridWorld.Cell cell : world.getCellGrid()) {
            this.setCell(cell.getX(), cell.getY(), cell);
        }

        for (GridWorld.Figure figure : world.getFigures()) {
            this.setFigure(figure.getCurrentX(), figure.getCurrentY(), figure);
        }

    }

    private void setFigure(int currentX, int currentY, GridWorld.Figure figure) {
        if (currentX >= 0 && currentY >= 0) {
            this.cells.get(currentY * width + currentX).addForegroundImage(figure.getImageUrl());
        }
    }

    private void setCell(int x, int y, GridWorld.Cell cell) {
        this.cells.get(y * width + x).setBackgroundImage(cell.getImageUrl());
    }

    private void addRow(int width) {
        SimulatorViewGridRow row = new SimulatorViewGridRow(this.rows.size());
        for (int i = 0; i < width; i++) {
            row.addCell(cells, width);
        }
        this.rows.add(row);
        this.rowContainer.add(row);
    }

    private void clear() {
        this.cells = new HashMap<>();
        this.rows.clear();
        this.rowContainer.removeAll();
    }




    @Override
    public void accept(World world) {
        getUI().ifPresent(ui -> ui.access(() -> {
            this.show(this.renderer.render(world));
        }));
    }
}

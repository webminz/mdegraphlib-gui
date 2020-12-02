package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.*;
import no.hvl.past.webui.transfer.quickRuleEngine.service.RuleEngineConfigurationService;
import no.hvl.past.webui.transfer.quickRuleEngine.service.WorldRenderer;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.*;

@Route("forskergrandprix/config")
@CssImport("animate.css/animate.css")
public class RuleEditorView extends SplitLayout implements HasUrlParameter<String> {

    private static final String OBJECTS_TREE_ROOT_TITLE_NO = "Objekter";
    private static final String RULES_TREE_ROOT_TITLE_NO = "Regler";
    private static final String NEW_RULE_NO = "Ny Regel";
    private static final String NEW_GROUP_NO = "Ny Gruppe";
    private static final String BACK_TO_SIMULATOR_NO = "Tilbake til spillet";


    // TODO has to be made more generic in the end



    public static class ConditionDropZone extends Div implements DropTarget<ConditionDropZone> {

        public ConditionDropZone(RuleEditorView mainView) {
            addClassName("drop-zone");
            addDropListener(event -> {
                DragItem.DragData data = (DragItem.DragData) event.getDragData().get();
                mainView.droppedOnCondition(ConditionDropZone.this, data.getElement(), data.getSourcePatternEditor(), data.getSourceXPos(), data.getSourceYPos());
            });
            setActive(true);
        }

        private void setBackground(String imageUrl) {
            getStyle().set("background-image", "url(" + imageUrl + ")");
            getStyle().set("background-repeat", "no-repeat");
            getStyle().set("background-position", "center");
            getStyle().set("background-size", "contain");
        }

        private void removeBackground() {
            getStyle().remove("background-image");
            getStyle().remove("background-repeat");
            getStyle().remove("background-position");
            getStyle().remove("background-size");
        }

        private void highlight() {
            addClassName("active");
        }

        private void deHighlight() {
            removeClassNames("active");
        }

        public void clear() {
            getStyle().remove("background-image");
            getStyle().remove("background-repeat");
            getStyle().remove("background-position");
            getStyle().remove("background-size");

        }
    }
    public static class PastebinDropZone extends Div implements DropTarget<PastebinDropZone> {

        public PastebinDropZone(RuleEditorView mainView) {
            addClassName("drop-zone");
            Icon icon = new Icon(VaadinIcon.TRASH);
            add(icon);

            addDropListener(event -> {
                DragItem.DragData data = (DragItem.DragData) event.getDragData().get();
                mainView.droppedOnBin(data.getElement(), data.getSourcePatternEditor(), data.getSourceXPos(), data.getSourceYPos());
            });

        }

        public void highlight() {
            addClassName("active");
        }

        public void deHighlight() {
            removeClassName("active");
        }
    }


    private RuleEngineConfigurationService service;
    private WorldRenderer renderer;

    private Menu menu;
    private GridWorldPatternEditor lhsEditor;
    private GridWorldPatternEditor rhsEditor;
    private List<ConditionDropZone> preconditions;
    private PastebinDropZone pastebin;

    private SplitLayout editorView;
    private VerticalLayout lhsContainer;
    private VerticalLayout rhsContainer;
    private HorizontalLayout preconditionsContainer;

    private MenuItem.Container objectContainer;
    private MenuItem.Container rulesContainer;

    private World currentWorld;
    private Collection<Thing> currentDeletes;
    private Collection<Thing> currentInserts;
    private String currentRule;
    private String ruleEngineID;

    private int ruleCounter = 0;


    @Override
    public void setParameter(BeforeEvent beforeEvent, String ruleEngineID) {
        this.ruleEngineID = ruleEngineID;
        if (ruleEngineID != null && !ruleEngineID.isEmpty()) {
            this.service.createRuleEngineIfNotExists(ruleEngineID, "PACMAN");
            initializeMenu();
        }
    }

    public RuleEditorView(@Autowired RuleEngineConfigurationService service) {
        this.service = service;

        setSizeFull();
        addClassName("rule-editor");
        setOrientation(Orientation.HORIZONTAL);

        this.menu = new Menu(this);
        this.lhsEditor = new GridWorldPatternEditor("lhs", this);
        this.rhsEditor = new GridWorldPatternEditor("rhs", this);
        this.preconditions = new ArrayList<>();

        ConditionDropZone firstPrecondition = new ConditionDropZone(this);
        this.preconditions.add(firstPrecondition);

        preconditionsContainer = new HorizontalLayout();
        preconditionsContainer.addClassName("preconditions");
        preconditionsContainer.add(firstPrecondition);

        lhsContainer = new VerticalLayout();
        lhsContainer.addClassName("lhs");
        lhsContainer.addClassName("grid-container");
        lhsContainer.add(lhsEditor);


        rhsContainer = new VerticalLayout();
        rhsContainer.addClassName("rhs");
        rhsContainer.addClassName("grid-container");
        rhsContainer.add(rhsEditor);
        rhsContainer.add(new Hr());
        this.pastebin = new PastebinDropZone(this);
        rhsContainer.add(pastebin);

        this.editorView = new SplitLayout();

        editorView.addToPrimary(lhsContainer);
        editorView.addToSecondary(rhsContainer);
        hideRHSditor();

        menu.setWidth("25%");
        editorView.setWidth("75%");
        addToPrimary(menu);
        addToSecondary(editorView);

        rhsContainer.add(new SwitcherButton(BACK_TO_SIMULATOR_NO, click -> {
            getUI().ifPresent(ui -> ui.navigate(SimulatorView.class, this.ruleEngineID));
        }));
    }

    private void initializeMenu() {
        this.renderer = service.getRenderer(this.ruleEngineID);

        List<MenuItem> menuItems = new ArrayList<>();
        this.objectContainer = new MenuItem.Container(OBJECTS_TREE_ROOT_TITLE_NO, VaadinIcon.CUBES);

        MenuItem.Container domainObjects = new MenuItem.Container("Pacman Domene", VaadinIcon.GLOBE);

        for (WorldObject wo : this.service.existingObjects(this.ruleEngineID)) {
            domainObjects.appendChild(new MenuItem.ObjectItem(this.renderer.render(wo)));
        }

        MenuItem.Container events = new MenuItem.Container("Hendelser", VaadinIcon.BELL);
        for (UserInteraction ui : this.service.possibleUserEvents(this.ruleEngineID)) {
            events.appendChild(new MenuItem.ObjectItem(this.renderer.render(ui)));
        }

        this.objectContainer.appendChild(domainObjects);
        this.objectContainer.appendChild(events);


        RuleHierarchy hierarchy = this.service.getRuleHierarchy(this.ruleEngineID);


        this.rulesContainer = new MenuItem.Container(RULES_TREE_ROOT_TITLE_NO, ruleGroupIcon(hierarchy.getType()));
        this.rulesContainer.appendChild(new MenuItem.NewRuleButton());
        updateRulesContainer(hierarchy);


        menuItems.add(objectContainer);
        menuItems.add(rulesContainer);

        this.menu.addItems(menuItems);
        this.menu.addClassName("editor-menu");
    }

    private void updateRulesContainer(RuleHierarchy hierarchy) {
        for (RuleHierarchy children : hierarchy.getChildren()) {
            setRuleHierarchy(children, this.rulesContainer);
        }
    }

    // Renderer

    private void setRuleHierarchy(RuleHierarchy hierarchy, MenuItem.Container currentContainer) {
        if (hierarchy.isLeaf() && hierarchy.getType().equals(RuleHierarchy.RuleHierarchyItemType.RULE)) {
            currentContainer.appendPenultimateChild(new MenuItem.RuleItem(hierarchy.getName()));
        } else {
            MenuItem.Container group = new MenuItem.Container(hierarchy.getName(), ruleGroupIcon(hierarchy.getType()));
            group.appendChild(new MenuItem.NewRuleButton());
            currentContainer.appendPenultimateChild(group);
            for (RuleHierarchy child : hierarchy.getChildren()) {
                setRuleHierarchy(child, group);
            }
        }
    }

    public void showRHSEditor() {
        this.rhsContainer.setVisible(true);
        this.lhsContainer.setWidth("50%");
    }

    public void hideRHSditor() {
        this.rhsContainer.setVisible(false);
        this.lhsContainer.setWidth("100%");
    }

    private void clear() {
        this.lhsEditor.clear();
        this.rhsEditor.clear();
        this.preconditions.forEach(cdz -> cdz.clear());
        // TODO
    }

    private void addToRightGrid(Thing thing) {
        if (thing instanceof WorldRelation.LocatedOn) {
            WorldRelation.LocatedOn loc = (WorldRelation.LocatedOn) thing;
            this.rhsEditor.addFigure(loc.getPlace().getxPos(), loc.getPlace().getyPos(), (GridWorld.Figure) this.renderer.render(loc.getSubject()));
        }
    }

    private void removeFromRightGrid(Thing thing) {
        if (thing instanceof WorldRelation.LocatedOn) {
            WorldRelation.LocatedOn loc = (WorldRelation.LocatedOn) thing;
            this.rhsEditor.removeFigure(loc.getPlace().getxPos(), loc.getPlace().getyPos(), (GridWorld.Figure) this.renderer.render(loc.getSubject()));
        }
    }

    private void updateGrids(WorldRepresentation render) {
        if (render instanceof GridWorld) {
            GridWorld world = (GridWorld) render;
            for (GridWorld.Cell cell : world.getCellGrid()) {
                this.lhsEditor.insertCell(cell);
                this.rhsEditor.insertCell(cell);
            }
            for (GridWorld.Figure figure : world.getFigures()) {
                this.lhsEditor.addFigure(figure.getCurrentX(), figure.getCurrentY(), figure);
                this.rhsEditor.addFigure(figure.getCurrentX(), figure.getCurrentY(), figure);
            }
        }
    }

    private void displayRule(String ruleName) {
        if (ruleName.equals(this.currentRule)) {
            return;
        }

        if (this.currentRule != null) {
            this.clear();
        }

        if (this.lhsEditor.isEmpty()) {
            this.lhsEditor.addFirst();
            lhsContainer.add(new Icon(VaadinIcon.PLUS));
            lhsContainer.add(preconditionsContainer);
        }

        this.currentRule = ruleName;
        for (Event event : this.service.eventTriggers(this.ruleEngineID, ruleName)) {
            ConditionDropZone cdz = new ConditionDropZone(this);
            cdz.setBackground(this.renderer.render(event).getImageUrl());
        }
        World context = this.service.getRuleContextPattern(this.ruleEngineID, ruleName);
        this.currentWorld = context;
        if (!context.getThings().isEmpty()) {
            this.showRHSEditor();
            updateGrids(this.renderer.render(context));

            Collection<Event> events = this.service.eventTriggers(ruleEngineID, ruleName);
            for (Event e : events) {
                if (e instanceof UserInteraction.ButtonPress) {
                    WorldRepresentation.VisualElement ve = this.renderer.render(e);
                    // TODO expand
                    this.preconditions.get(0).setBackground(ve.getImageUrl());
                }
            }
        }
        this.currentDeletes = new HashSet<>();
        this.currentInserts = new HashSet<>();



        this.service.getRuleDeletes(ruleEngineID, ruleName).forEach(thing -> {
            this.currentDeletes.add(thing);
            this.removeFromRightGrid(thing);
        });
        this.service.getRuleInserts(ruleEngineID, ruleName).forEach(thing -> {
            this.currentInserts.add(thing);
            this.addToRightGrid(thing);
        });
    }


    // Event handlers


    private void droppedOnCondition(ConditionDropZone conditionDropZone, WorldRepresentation.VisualElement element, String sourcePatternEditor, Integer sourceXPos, Integer sourceYPos) {
        if (element.getDisplays() instanceof Event) {
            conditionDropZone.setBackground(element.getImageUrl());
            this.service.addEventTriggerForRule(this.ruleEngineID, this.currentRule, (Event) element.getDisplays());
        }
    }

    public void ruleSelected(String ruleName) {
        this.displayRule(ruleName);
    }

    public void newGroup(String groupName) {
        this.service.createRuleGroup(ruleEngineID, groupName.equals(RULES_TREE_ROOT_TITLE_NO) ? "" : groupName, nextGroupName());
        RuleHierarchy hierarchy = this.service.getRuleHierarchy(this.ruleEngineID);
        this.updateRulesContainer(hierarchy);
        this.menu.getDataProvider().refreshAll();
    }


    public void newRule(String groupName) {
        this.service.createOrUpdateRule(ruleEngineID, groupName.equals(RULES_TREE_ROOT_TITLE_NO) ? "" : groupName, nextRuleName(), new World(new ArrayList<>()), Collections.emptySet(), Collections.emptySet());
        RuleHierarchy hierarchy = this.service.getRuleHierarchy(this.ruleEngineID);
        this.updateRulesContainer(hierarchy);
        this.menu.getDataProvider().refreshAll();
    }

    public void ruleRenamed(String oldValue, String newValue) {
        boolean isOk = this.service.renameRule(ruleEngineID, oldValue, newValue);
        if (!isOk) {
            Notification notification = new Notification("Navn '" + newValue + "' er allerede i bruk, velg en annen!", 5000);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        } else {
            RuleHierarchy hierarchy = this.service.getRuleHierarchy(this.ruleEngineID);
            for (RuleHierarchy children : hierarchy.getChildren()) {
                setRuleHierarchy(children, this.rulesContainer);
            }
            this.menu.getDataProvider().refreshAll();
        }
    }

    public void stopDragging() {
        this.lhsEditor.unhighlight();
        this.rhsEditor.unhighlight();
        this.preconditions.forEach(ConditionDropZone::deHighlight);
        this.pastebin.deHighlight();
    }



    public void startDragging(WorldRepresentation.VisualElement element, String sourcePatternEditor) {
        if (sourcePatternEditor == null || sourcePatternEditor.isEmpty()) {
            // from tree
            if (element instanceof GridWorld.Cell) {
                this.lhsEditor.highlightEmpty();
            }
            if (element instanceof GridWorld.Figure) {
                this.lhsEditor.highlightFilled();
            }
            if (element instanceof GridWorld.Context) {
                this.preconditions.forEach(ConditionDropZone::highlight);
            }
        } else if (sourcePatternEditor.equals("lhs")) {
            this.pastebin.highlight();
        } else if (sourcePatternEditor.equals("rhs")) {
            this.pastebin.highlight();
            if (element instanceof GridWorld.Figure) {
                this.rhsEditor.highlightFilled();
            }
        }
        // TODO event handling
    }

    public void dropped(
            WorldRepresentation.VisualElement element,
            String sourcePatternEditor,
            Integer sourceXPos,
            Integer sourceYPos,
            String targetEditorId,
            int targetXPos,
            int targetYPos) {

        this.stopDragging();
        if ((sourcePatternEditor == null || sourcePatternEditor.isEmpty()) && targetEditorId.equals("lhs")) {
            // from object tree dropped on LHS
            if (element instanceof GridWorld.Cell && !this.lhsEditor.getCell(targetXPos, targetYPos).map(GridDropZone::isFilled).orElse(true)) {
                // Grid cell
                WorldObject newObject = ((WorldObject) element.getDisplays()).instantiate();
                newObject.setxPos(targetXPos);
                newObject.setyPos(targetYPos);
                currentWorld.getThings().add(newObject);

                this.lhsEditor.getEastOf(targetXPos, targetYPos)
                        .ifPresent(cell -> {
                           WorldObject partner = (WorldObject) cell.getDisplays();
                           WorldRelation rel = new WorldRelation.HorizontalAdjacency(newObject, partner);
                            partner.getRelations().add(rel);
                            newObject.getRelations().add(rel);
                            currentWorld.getThings().add(rel);
                        });
                this.lhsEditor.getWestOf(targetXPos, targetYPos)
                        .ifPresent(cell -> {
                            WorldObject partner = (WorldObject) cell.getDisplays();
                            WorldRelation rel = new WorldRelation.HorizontalAdjacency(partner, newObject);
                            partner.getRelations().add(rel);
                            newObject.getRelations().add(rel);
                            currentWorld.getThings().add(rel);
                        });
                this.lhsEditor.getNorthOf(targetXPos, targetYPos)
                        .ifPresent(cell -> {
                            WorldObject partner = (WorldObject) cell.getDisplays();
                            WorldRelation rel = new WorldRelation.VerticalAdjacency(partner, newObject);
                            partner.getRelations().add(rel);
                            newObject.getRelations().add(rel);
                            currentWorld.getThings().add(rel);
                        });
                this.lhsEditor.getSouthOf(targetXPos, targetYPos)
                        .ifPresent(cell -> {
                            WorldObject partner = (WorldObject) cell.getDisplays();
                            WorldRelation rel = new WorldRelation.HorizontalAdjacency(newObject, partner);
                            partner.getRelations().add(rel);
                            newObject.getRelations().add(rel);
                            currentWorld.getThings().add(rel);
                        });


                // Render LHS and RHS worlds
                this.showRHSEditor();
                if (this.rhsEditor.isEmpty()) {
                    this.rhsEditor.addFirst();
                }
                this.lhsEditor.getCell(targetXPos, targetYPos).get().fill((GridWorld.Cell) this.renderer.render(newObject));
                this.rhsEditor.getCell(targetXPos, targetYPos).get().fill((GridWorld.Cell) this.renderer.render(newObject));
                this.lhsEditor.growUpTo(targetXPos, targetYPos);
                this.rhsEditor.growUpTo(targetXPos, targetYPos);

                this.service.createOrUpdateRule(this.ruleEngineID, null, currentRule, currentWorld, new HashSet<>(this.currentInserts), new HashSet<>(this.currentDeletes));
            }
            if (element instanceof GridWorld.Figure && this.lhsEditor.getCell(targetXPos, targetYPos).map(GridDropZone::isFilled).orElse(false)) {

                // dropped element is a figure
                this.lhsEditor.getCell(targetXPos, targetYPos).get().addFigure((GridWorld.Figure) element);
                this.rhsEditor.getCell(targetXPos, targetYPos).get().addFigure((GridWorld.Figure) element);

                WorldObject location = (WorldObject) this.lhsEditor.getCell(targetXPos, targetYPos).get().getCell().getDisplays();
                WorldObject subject = (WorldObject) element.getDisplays();

                this.currentWorld.getThings().add(subject);

                WorldRelation.LocatedOn locatedOn = new WorldRelation.LocatedOn(location, subject);
                location.getRelations().add(locatedOn);
                subject.getRelations().add(locatedOn);
                this.currentWorld.getThings().add(locatedOn);

                this.service.createOrUpdateRule(this.ruleEngineID, null, currentRule, currentWorld, new HashSet<>(this.currentInserts), new HashSet<>(this.currentDeletes));
            }

        } else if (sourcePatternEditor != null && sourcePatternEditor.equals("rhs") && targetEditorId != null && targetEditorId.equals("rhs")) {
            if (element instanceof GridWorld.Figure) {
                // A figure from the left is moved to a different location
                this.rhsEditor.getCell(sourceXPos, sourceYPos).ifPresent(zone -> zone.removeFigure((GridWorld.Figure) element));
                this.rhsEditor.getCell(targetXPos, targetYPos).ifPresent(zone -> zone.addFigure((GridWorld.Figure) element));

                this.currentWorld.getThings().stream()
                        .filter(t -> t instanceof WorldRelation)
                        .map(t -> (WorldRelation)t)
                        .filter(r -> r.getName().equals(WorldRelation.LOCATED_ON))
                        .filter(r -> r.getThingInRelation(WorldRelation.LocatedOn.SUBJECT).equals(element.getDisplays()))
                        .forEach(r -> this.currentDeletes.add(r));


                WorldObject location = (WorldObject) this.rhsEditor.getCell(targetXPos, targetYPos).get().getCell().getDisplays();
                WorldObject subject = (WorldObject) element.getDisplays();
                WorldRelation.LocatedOn locatedOn = new WorldRelation.LocatedOn(location, subject);
                this.currentInserts.add(locatedOn);

                this.service.createOrUpdateRule(this.ruleEngineID, null, currentRule, currentWorld, new HashSet<>(this.currentInserts), new HashSet<>(this.currentDeletes));
            }
        }
        // TODO more event handling
    }

    private void droppedOnBin(WorldRepresentation.VisualElement element, String editorId, Integer sourceXPos, Integer sourceYPos) {
        if (editorId != null && editorId.equals("rhs") && element instanceof GridWorld.Figure) {
            this.rhsEditor.getCell(sourceXPos, sourceYPos).ifPresent(zone -> zone.removeFigure((GridWorld.Figure) element));

            this.currentWorld.getThings().stream()
                    .filter(t -> t instanceof WorldRelation)
                    .map(t -> (WorldRelation)t)
                    .filter(r -> r.getName().equals(WorldRelation.LOCATED_ON))
                    .filter(r -> r.getThingInRelation(WorldRelation.LocatedOn.SUBJECT).equals(element.getDisplays()))
                    .forEach(r -> this.currentDeletes.add(r));
            this.currentDeletes.add(element.getDisplays());

            this.service.createOrUpdateRule(this.ruleEngineID, null, currentRule, currentWorld, new HashSet<>(this.currentInserts), new HashSet<>(this.currentDeletes));
        }

        // TODO more event handling

    }

    // helpers

    private String nextRuleName() {
        if (ruleCounter == 0) {
            ruleCounter++;
            return NEW_RULE_NO;
        } else {
            return NEW_RULE_NO + " " + ruleCounter++;
        }
    }

    private String nextGroupName() {
        if (ruleCounter == 0) {
            ruleCounter++;
            return NEW_GROUP_NO;
        } else {
            return NEW_GROUP_NO + " " + ruleCounter++;
        }
    }

    private VaadinIcon ruleGroupIcon(RuleHierarchy.RuleHierarchyItemType type) {
        switch (type) {
            case MUTEX_GROUP:
                return VaadinIcon.LIST_SELECT;
            case PARALLEL_GROUP:
                return VaadinIcon.LIST_UL;
            case SEQUENTIAL_GROUP:
            default:
                return VaadinIcon.LIST_OL;
        }
    }
}

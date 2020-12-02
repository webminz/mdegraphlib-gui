package no.hvl.past.webui.backend.quickRuleEngine.pacman;

import no.hvl.past.webui.backend.quickRuleEngine.*;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.*;
import no.hvl.past.webui.transfer.quickRuleEngine.service.WorldRenderer;
import org.apache.commons.compress.utils.Sets;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PacmanDomain implements Domain {

    @Override
    public Collection<WorldObject> objects() {
        List<WorldObject> existing = new ArrayList<>();
        existing.add(new LabyrinthElement.Corridor());
        existing.add(new LabyrinthElement.Wall());
        existing.add(new PacmanFigures.Pacman());
        existing.add(new PacmanFigures.Marble());
        existing.add(new PacmanFigures.RedGhost());
        existing.add(new PacmanFigures.TealGhost());
        existing.add(new PacmanFigures.OrangeGhost());
        existing.add(new PacmanFigures.RoseGhost());
        return existing;
    }

    @Override
    public Set<Event> interactions() {
        return Sets.newHashSet(
                new UserInteraction.ButtonPress(Collections.singleton(UserInteraction.Buttons.LEFT), ""),
                new UserInteraction.ButtonPress(Collections.singleton(UserInteraction.Buttons.UP), ""),
                new UserInteraction.ButtonPress(Collections.singleton(UserInteraction.Buttons.RIGHT), ""),
                new UserInteraction.ButtonPress(Collections.singleton(UserInteraction.Buttons.DOWN), ""));
    }

    public void configureMoveRightRule(Rule target) {
        PacmanFigures.Pacman pacmanH = new PacmanFigures.Pacman();
        moveRightKernel(target, pacmanH);
    }

    private void moveRightKernel(Rule targetRule,WorldObject figure) {
        World.Builder wb = new World.Builder();
        LabyrinthElement.Corridor sourceH = new LabyrinthElement.Corridor();
        sourceH.setxPos(1);
        sourceH.setyPos(1);
        LabyrinthElement.Corridor targetH = new LabyrinthElement.Corridor();
        targetH.setxPos(2);
        targetH.setyPos(1);
        WorldRelation sourceLocationH = wb.locatedOn(figure, sourceH);
        wb.locatedBesides(sourceH, targetH);
        WorldRelation targetLocationH = new WorldRelation.LocatedOn(targetH, figure);

        Pattern moveRightPattern = new Pattern(wb.build());
        targetRule.setContext(moveRightPattern);
        targetRule.setInsertions(Collections.singleton(targetLocationH));
        targetRule.setDeletions(Collections.singleton(sourceLocationH));
    }

    public void configureMoveDownRule(Rule target) {
        PacmanFigures.Pacman pacmanV = new PacmanFigures.Pacman();
        moveDownKernel(target, pacmanV);
    }

    private void moveDownKernel(Rule targetRule, WorldObject object) {
        World.Builder wb = new World.Builder();

        LabyrinthElement.Corridor sourceV = new LabyrinthElement.Corridor();
        sourceV.setyPos(1);
        sourceV.setxPos(1);
        LabyrinthElement.Corridor targetV = new LabyrinthElement.Corridor();
        targetV.setxPos(1);
        targetV.setyPos(2);
        WorldRelation sourceLocationV = wb.locatedOn(object, sourceV);
        wb.locatedAbove(sourceV, targetV);
        WorldRelation targetLocationV = new WorldRelation.LocatedOn(targetV, object);

        Pattern moveDownPattern = new Pattern(wb.build());
        targetRule.setContext(moveDownPattern);
        targetRule.setInsertions(Collections.singleton(targetLocationV));
        targetRule.setDeletions(Collections.singleton(sourceLocationV));
    }

    public void configureMoveLeftRule(Rule target) {
        PacmanFigures.Pacman pacmanH = new PacmanFigures.Pacman();
        moveLeftKernel(target, pacmanH);
    }

    private void moveLeftKernel(Rule targetRule, WorldObject pacmanH) {
        World.Builder wb = new World.Builder();
        LabyrinthElement.Corridor sourceH = new LabyrinthElement.Corridor();
        sourceH.setxPos(2);
        sourceH.setyPos(1);
        LabyrinthElement.Corridor targetH = new LabyrinthElement.Corridor();
        targetH.setxPos(1);
        targetH.setyPos(1);
        WorldRelation sourceLocationH = wb.locatedOn(pacmanH, sourceH);
        wb.locatedBesides(targetH, sourceH);
        WorldRelation targetLocationH = new WorldRelation.LocatedOn(targetH, pacmanH);

        Pattern moveRightPattern = new Pattern(wb.build());
        targetRule.setContext(moveRightPattern);
        targetRule.setInsertions(Collections.singleton(targetLocationH));
        targetRule.setDeletions(Collections.singleton(sourceLocationH));
    }

    public void configureMoveUpRule(Rule target) {
        PacmanFigures.Pacman pacmanV = new PacmanFigures.Pacman();
        moveUpKernel(target, pacmanV);
    }

    private void moveUpKernel(Rule targetRule, WorldObject figure) {
        World.Builder wb = new World.Builder();
        LabyrinthElement.Corridor sourceV = new LabyrinthElement.Corridor();
        sourceV.setyPos(2);
        sourceV.setxPos(1);
        LabyrinthElement.Corridor targetV = new LabyrinthElement.Corridor();
        targetV.setxPos(1);
        targetV.setyPos(1);
        WorldRelation sourceLocationV = wb.locatedOn(figure, sourceV);
        wb.locatedAbove(targetV, sourceV);
        WorldRelation targetLocationV = new WorldRelation.LocatedOn(targetV, figure);

        Pattern moveDownPattern = new Pattern(wb.build());
        targetRule.setContext(moveDownPattern);
        targetRule.setInsertions(Collections.singleton(targetLocationV));
        targetRule.setDeletions(Collections.singleton(sourceLocationV));
    }

    public void configureEatMarbleRule(Rule target) {
        PacmanFigures.Pacman eater = new PacmanFigures.Pacman();
        PacmanFigures.Marble eatee = new PacmanFigures.Marble();
        eatKernel(target, eater, eatee);
    }

    private void eatKernel(Rule targetRule, WorldObject eater, WorldObject eatee) {
        World.Builder wb = new World.Builder();
        LabyrinthElement.Corridor c = new LabyrinthElement.Corridor();
        c.setxPos(1);
        c.setyPos(1);
        WorldRelation toBeEatenLocation = wb.locatedOn(eatee, c);
        wb.locatedOn(eater, c);

        Pattern eatMarblePattern = new Pattern(wb.build());
        targetRule.setContext(eatMarblePattern);
        targetRule.setDeletions(Sets.newHashSet(toBeEatenLocation, eatee));
    }

    public void configureGhostKillsPacmanRule(Rule target, PacmanFigures.GhostColor color) {
        WorldObject eater = createGhost(color);
        WorldObject eatee = new PacmanFigures.Pacman();
        eatKernel(target, eater, eatee);
    }

    public static PacmanFigures.Ghost createGhost(PacmanFigures.GhostColor ghostColor) {
        switch (ghostColor) {
            case ROSE:
                return new PacmanFigures.RoseGhost();
            case RED:
                return new PacmanFigures.RedGhost();
            case TEAL:
                return new PacmanFigures.TealGhost();
            case ORANGE:
                return new PacmanFigures.OrangeGhost();
            default:
                return null;
        }
    }

    public void configureGhostUp(Rule target, PacmanFigures.GhostColor ghostColor) {
        PacmanFigures.Ghost ghost = createGhost(ghostColor);
        moveUpKernel(target,ghost);
    }

    public void configureGhostDown(Rule target, PacmanFigures.GhostColor ghostColor) {
        PacmanFigures.Ghost ghost = createGhost(ghostColor);
        moveDownKernel(target, ghost);
    }

    public void configureGhostLeft(Rule target, PacmanFigures.GhostColor ghostColor) {
        PacmanFigures.Ghost ghost = createGhost(ghostColor);
        moveLeftKernel(target, ghost);
    }

    public void configureGhostRight(Rule target, PacmanFigures.GhostColor ghostColor) {
        PacmanFigures.Ghost ghost = createGhost(ghostColor);
        moveRightKernel(target, ghost);
    }


    @Override
    public WorldRenderer renderer() {
        // TODO the following may become a standard GridWorldRenderer?
        return new WorldRenderer() {

            int width = 0;
            int height = 0;

            @Override
            public WorldRepresentation render(World world) {
                Set<GridWorld.Figure> figures = world.getThings().stream()
                        .filter(t -> t instanceof PacmanFigures)
                        .map(t -> render((WorldObject) t))
                        .map(t -> (GridWorld.Figure) t)
                        .collect(Collectors.toSet());
                List<GridWorld.Cell> cells = world.getThings().stream()
                        .filter(t -> t instanceof LabyrinthElement)
                        .map(t -> {
                            GridWorld.Cell rendered = (GridWorld.Cell) this.render((WorldObject) t);
                            if (rendered.getX() == width) {
                                width = rendered.getX() + 1;
                            }
                            if (rendered.getY() == height) {
                                height = rendered.getY() + 1;
                            }
                            return rendered;
                        }) // TODO the latter my be unnecessary
                        .sorted((c1,c2) -> {
                            if (c1.getY() == c2.getY()) {
                                if (c1.getX() == c2.getX()) {
                                    return 0;
                                } else if (c1.getX() < c2.getX()) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            } else if (c1.getY() < c2.getY()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }).collect(Collectors.toList());
                return new GridWorld(world, width, height, cells, figures, Collections.emptyList());
            }

            @Override
            public WorldRepresentation.VisualElement render(WorldObject object) {
                if (object instanceof LabyrinthElement.Corridor) {
                    return new GridWorld.Cell(object, "Gang", "images/pacman/corridor.png", object.getxPos(), object.getyPos(), "cell");
                }
                if (object instanceof LabyrinthElement.Wall) {
                    return new GridWorld.Cell(object, "Vegg", "images/pacman/wall.png", object.getxPos(), object.getyPos(), "cell");
                }
                if (object instanceof PacmanFigures.Pacman) {
                    return new GridWorld.Figure(object, "images/pacman/pacman_o.png", object.currentXPos(), object.currentYPos(), "Pacman");
                }
                if (object instanceof PacmanFigures.Marble) {
                    return new GridWorld.Figure(object, "images/pacman/marble_mod.png", object.currentXPos(), object.currentYPos(), "Klinkekule");
                }
                if (object instanceof PacmanFigures.Ghost) {
                    String image = null;
                    String ghostName = "";
                    switch (((PacmanFigures.Ghost) object).getColor()) {
                        case RED:
                            image = "images/pacman/ghost1.png";
                            ghostName = "Rød ånd";
                            break;
                        case TEAL:
                            image = "images/pacman/ghost2.png";
                            ghostName = "Blå ånd";
                            break;
                        case ORANGE:
                            image = "images/pacman/ghost3.png";
                            ghostName = "Oransje ånd";
                            break;
                        case ROSE:
                            image = "images/pacman/ghost4.png";
                            ghostName = "Rosa ånd";
                            break;
                    }
                    return new GridWorld.Figure(object, image, object.currentXPos(), object.currentYPos(), ghostName);
                }

                return null;
            }

            @Override
            public WorldRepresentation.VisualElement render(Event event) {
                if (event instanceof UserInteraction.ButtonPress) {
                    UserInteraction.ButtonPress buttnPress = (UserInteraction.ButtonPress) event;
                    if (buttnPress.getButtonsPressed().contains(UserInteraction.Buttons.LEFT)) {
                        return new GridWorld.Context(event, "images/pacman/key_left.png", "Venstre", "user-interaction-button");
                    }
                    if (buttnPress.getButtonsPressed().contains(UserInteraction.Buttons.RIGHT)) {
                        return new GridWorld.Context(event, "images/pacman/key_right.png", "Høyre", "user-interaction-button");
                    }
                    if (buttnPress.getButtonsPressed().contains(UserInteraction.Buttons.UP)) {
                        return new GridWorld.Context(event, "images/pacman/key_up.png", "Opp", "user-interaction-button");
                    }
                    if (buttnPress.getButtonsPressed().contains(UserInteraction.Buttons.DOWN)) {
                        return new GridWorld.Context(event, "images/pacman/key_down.png", "Ned", "user-interaction-button");
                    }
                }
                return null;
            }

            @Override
            public WorldRepresentation.VisualElement render(TemporalThing temporalThing) {
                return null;
            }

            @Override
            public WorldRepresentation.VisualElement render(WorldRelation relation) {
                return null;
            }
        };
    }

    @Override
    public World startWorld() {
        String runDir = System.getProperty("user.dir");
        PacmanLabyrinthBuilder labyrinthBuilder = new PacmanLabyrinthBuilder(new File(runDir + "/target/classes/pacman.level"));
        return labyrinthBuilder.build();
    }

    @Override
    public void domainSpecificRuleSetUp(RuleEngine engine) {
        Rule moveRight = engine.getOrCreateRule("pacmanMovement", "moveRight");
        configureMoveRightRule(moveRight);
        moveRight.setTrigger(new EventTrigger<UserInteraction.ButtonPress>(engine, new UserInteraction.ButtonPress(Collections.singleton(UserInteraction.Buttons.RIGHT), "")));

        Rule moveLeft = engine.getOrCreateRule("pacmanMovement", "moveLeft");
        configureMoveLeftRule(moveLeft);
        moveLeft.setTrigger(new EventTrigger<UserInteraction.ButtonPress>(engine, new UserInteraction.ButtonPress(Collections.singleton(UserInteraction.Buttons.LEFT), "")));

        Rule moveUp = engine.getOrCreateRule("pacmanMovement", "moveUp");
        configureMoveUpRule(moveUp);
        moveUp.setTrigger(new EventTrigger<UserInteraction.ButtonPress>(engine, new UserInteraction.ButtonPress(Collections.singleton(UserInteraction.Buttons.UP), "")));

        Rule moveDown = engine.getOrCreateRule("pacmanMovement", "moveDown");
        configureMoveDownRule(moveDown);
        moveDown.setTrigger(new EventTrigger<UserInteraction.ButtonPress>(engine, new UserInteraction.ButtonPress(Collections.singleton(UserInteraction.Buttons.DOWN), "")));

        Rule moveRedGhostRight = engine.getOrCreateRule("redGhostMovement", "moveRedGhostRight");
        configureGhostRight(moveRedGhostRight, PacmanFigures.GhostColor.RED);

        Rule moveRedGhostLeft = engine.getOrCreateRule("redGhostMovement", "moveRedGhostLeft");
        configureGhostLeft(moveRedGhostLeft, PacmanFigures.GhostColor.RED);

        Rule moveRedGhostUp = engine.getOrCreateRule("redGhostMovement", "moveRedGhostUp");
        configureGhostUp(moveRedGhostUp, PacmanFigures.GhostColor.RED);

        Rule moveRedGhostDown = engine.getOrCreateRule("redGhostMovement", "moveRedGhostDown");
        configureGhostDown(moveRedGhostDown, PacmanFigures.GhostColor.RED);

        engine.setRuleGroupMode("redGhostMovement", RuleGroup.ExecutionType.ALTERNATIVE);


        Rule moveOrangeGhostRight = engine.getOrCreateRule("orangeGhostMovement", "moveOrangeGhostRight");
        configureGhostRight(moveOrangeGhostRight, PacmanFigures.GhostColor.ORANGE);

        Rule moveOrangeGhostLeft = engine.getOrCreateRule("orangeGhostMovement", "moveOrangeGhostLeft");
        configureGhostLeft(moveOrangeGhostLeft, PacmanFigures.GhostColor.ORANGE);

        Rule moveOrangeGhostUp = engine.getOrCreateRule("orangeGhostMovement", "moveOrangeGhostUp");
        configureGhostUp(moveOrangeGhostUp, PacmanFigures.GhostColor.ORANGE);

        Rule moveOrangeGhostDown = engine.getOrCreateRule("orangeGhostMovement", "moveOrangeGhostDown");
        configureGhostDown(moveOrangeGhostDown, PacmanFigures.GhostColor.ORANGE);

        engine.setRuleGroupMode("orangeGhostMovement", RuleGroup.ExecutionType.ALTERNATIVE);


        Rule moveTealGhostRight = engine.getOrCreateRule("tealGhostMovement", "moveTealGhostRight");
        configureGhostRight(moveTealGhostRight, PacmanFigures.GhostColor.TEAL);

        Rule moveTealGhostLeft = engine.getOrCreateRule("tealGhostMovement", "moveTealGhostLeft");
        configureGhostLeft(moveTealGhostLeft, PacmanFigures.GhostColor.TEAL);

        Rule moveTealGhostUp = engine.getOrCreateRule("tealGhostMovement", "moveTealGhostUp");
        configureGhostUp(moveTealGhostUp, PacmanFigures.GhostColor.TEAL);

        Rule moveTealGhostDown = engine.getOrCreateRule("tealGhostMovement", "moveTealGhostDown");
        configureGhostDown(moveTealGhostDown, PacmanFigures.GhostColor.TEAL);

        engine.setRuleGroupMode("tealGhostMovement", RuleGroup.ExecutionType.ALTERNATIVE);

        Rule moveRoseGhostRight = engine.getOrCreateRule("roseGhostMovement", "moveRoseGhostRight");
        configureGhostRight(moveRoseGhostRight, PacmanFigures.GhostColor.ROSE);

        Rule moveRoseGhostLeft = engine.getOrCreateRule("roseGhostMovement", "moveRoseGhostLeft");
        configureGhostLeft(moveRoseGhostLeft, PacmanFigures.GhostColor.ROSE);

        Rule moveRoseGhostUp = engine.getOrCreateRule("roseGhostMovement", "moveRoseGhostUp");
        configureGhostUp(moveRoseGhostUp, PacmanFigures.GhostColor.ROSE);

        Rule moveRoseGhostDown = engine.getOrCreateRule("roseGhostMovement", "moveRoseGhostDown");
        configureGhostDown(moveRoseGhostDown, PacmanFigures.GhostColor.ROSE);

        engine.setRuleGroupMode("roseGhostMovement", RuleGroup.ExecutionType.ALTERNATIVE);


        Rule eatMarble = engine.getOrCreateRule("eatRules", "eatMarble");
        configureEatMarbleRule(eatMarble);

        Rule redEatsPacman = engine.getOrCreateRule("eatRules", "redGhostEatsPacman");
        configureGhostKillsPacmanRule(redEatsPacman, PacmanFigures.GhostColor.RED);

        Rule orangeEatsPacman = engine.getOrCreateRule("eatRules", "orangeGhostEatsPacman");
        configureGhostKillsPacmanRule(orangeEatsPacman, PacmanFigures.GhostColor.ORANGE);

        Rule tealEatsPacman = engine.getOrCreateRule("eatRules", "tealGhostEatsPacman");
        configureGhostKillsPacmanRule(tealEatsPacman, PacmanFigures.GhostColor.TEAL);

        Rule roseEatsPacman = engine.getOrCreateRule("eatRules", "roseGhostEatsPacman");
        configureGhostKillsPacmanRule(roseEatsPacman, PacmanFigures.GhostColor.ROSE);

    }
}

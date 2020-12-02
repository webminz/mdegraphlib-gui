package no.hvl.past.backend.quickRuleEngine.pacman;

import no.hvl.past.webui.backend.quickRuleEngine.Rule;
import no.hvl.past.webui.backend.quickRuleEngine.RuleEngine;
import no.hvl.past.webui.backend.quickRuleEngine.RuleGroup;
import no.hvl.past.webui.backend.quickRuleEngine.Trigger;
import no.hvl.past.webui.backend.quickRuleEngine.pacman.LabyrinthElement;
import no.hvl.past.webui.backend.quickRuleEngine.pacman.PacmanDomain;
import no.hvl.past.webui.backend.quickRuleEngine.pacman.PacmanFigures;
import no.hvl.past.webui.backend.quickRuleEngine.pacman.PacmanLabyrinthBuilder;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldObject;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldRelation;
import org.junit.Test;


import java.util.Optional;

import static junit.framework.TestCase.*;

public class PacmanTest {


    @Test
    public void testWorldBuilding() {
        PacmanLabyrinthBuilder b = new PacmanLabyrinthBuilder("PW\n.R\n");
        World world = b.build();
        assertEquals(world.getThings().stream().filter(w -> w instanceof LabyrinthElement).count(), 4);
        assertEquals(world.getThings().stream().filter(w -> w instanceof PacmanFigures).count(), 3);

        WorldObject topLeft = world.getThings().stream().filter(w -> w instanceof  LabyrinthElement)
                .map(w -> (WorldObject) w)
                .filter(w -> w.getxPos() == 0 && w.getyPos() == 0).findFirst().get();
        WorldObject topRight = world.getThings().stream().filter(w -> w instanceof  LabyrinthElement)
                .map(w -> (WorldObject) w)
                .filter(w -> w.getxPos() == 1 && w.getyPos() == 0).findFirst().get();
        WorldObject bottomLeft = world.getThings().stream().filter(w -> w instanceof  LabyrinthElement)
                .map(w -> (WorldObject) w)
                .filter(w -> w.getxPos() == 0 && w.getyPos() == 1).findFirst().get();
        WorldObject bottomRight = world.getThings().stream().filter(w -> w instanceof  LabyrinthElement)
                .map(w -> (WorldObject) w)
                .filter(w -> w.getxPos() == 1 && w.getyPos() == 1).findFirst().get();
        PacmanFigures.Pacman pacman = world.getThings().stream().filter(w -> w instanceof PacmanFigures.Pacman)
                .map(w -> (PacmanFigures.Pacman) w)
                .findFirst().get();
        PacmanFigures.Ghost ghost = world.getThings().stream().filter(w -> w instanceof PacmanFigures.Ghost)
                .map(w -> (PacmanFigures.Ghost) w)
                .findFirst().get();
        PacmanFigures.Marble marble = world.getThings().stream().filter(w -> w instanceof PacmanFigures.Marble)
                .map(w -> (PacmanFigures.Marble) w)
                .findFirst().get();

        assertEquals(ghost.getColor(), PacmanFigures.GhostColor.RED);


        assertTrue(topLeft.navigateRelation(WorldRelation.HORIZONTAL_LOCATION, "east").contains(topRight));
        assertTrue(topLeft.navigateRelation(WorldRelation.VERTICAL_LOCATION, "south").contains(bottomLeft));
        assertTrue(topLeft.navigateRelation(WorldRelation.LOCATED_ON, "subject").contains(pacman));
        assertTrue(topLeft instanceof LabyrinthElement.Corridor);

        assertTrue(topRight.navigateRelation(WorldRelation.HORIZONTAL_LOCATION, "west").contains(topLeft));
        assertTrue(topRight.navigateRelation(WorldRelation.VERTICAL_LOCATION, "south").contains(bottomRight));
        assertTrue(topRight instanceof LabyrinthElement.Wall);

        assertTrue(bottomLeft.navigateRelation(WorldRelation.HORIZONTAL_LOCATION, "east").contains(bottomRight));
        assertTrue(bottomLeft.navigateRelation(WorldRelation.VERTICAL_LOCATION, "north").contains(topLeft));
        assertTrue(bottomLeft.navigateRelation(WorldRelation.LOCATED_ON, "subject").contains(marble));
        assertTrue(bottomLeft instanceof LabyrinthElement.Corridor);

        assertTrue(bottomRight.navigateRelation(WorldRelation.HORIZONTAL_LOCATION, "west").contains(bottomLeft));
        assertTrue(bottomRight.navigateRelation(WorldRelation.VERTICAL_LOCATION, "north").contains(topRight));
        assertTrue(bottomRight.navigateRelation(WorldRelation.LOCATED_ON, "subject").contains(ghost));
        assertTrue(bottomRight instanceof LabyrinthElement.Corridor);

    }


    @Test
    public void testPatternMatching() {
        PacmanLabyrinthBuilder b = new PacmanLabyrinthBuilder("PW\n.R\n");
        World world = b.build();
        RuleEngine engine = new RuleEngine("1", world);

        PacmanDomain domain = new PacmanDomain();


        Rule eatPacman = engine.getOrCreateRule("", "eatPacman");
        Rule eatMarble = engine.getOrCreateRule("", "eatMarble");
        Rule moveRight = engine.getOrCreateRule("", "moveRight");
        Rule moveDown = engine.getOrCreateRule("", "moveDown");


        domain.configureMoveDownRule(moveDown);
        domain.configureMoveRightRule(moveRight);
        domain.configureEatMarbleRule(eatMarble);
        domain.configureGhostKillsPacmanRule(eatPacman, PacmanFigures.GhostColor.RED);

        // Testing pattern matching

        assertTrue(moveDown.getContext().match(world).isPresent());
        assertFalse(moveRight.getContext().match(world).isPresent());
        assertFalse(eatMarble.getContext().match(world).isPresent());

        // Applying all possible rules (i.e. move down)

        engine.executeRules();
        World afterFirstRuleApplication = engine.getCurrentState();

        WorldObject topLeft = afterFirstRuleApplication.getThings().stream().filter(w -> w instanceof  LabyrinthElement)
                .map(w -> (WorldObject) w)
                .filter(w -> w.getxPos() == 0 && w.getyPos() == 0).findFirst().get();
        WorldObject topRight = afterFirstRuleApplication.getThings().stream().filter(w -> w instanceof  LabyrinthElement)
                .map(w -> (WorldObject) w)
                .filter(w -> w.getxPos() == 1 && w.getyPos() == 0).findFirst().get();
        WorldObject bottomLeft = afterFirstRuleApplication.getThings().stream().filter(w -> w instanceof  LabyrinthElement)
                .map(w -> (WorldObject) w)
                .filter(w -> w.getxPos() == 0 && w.getyPos() == 1).findFirst().get();
        WorldObject bottomRight = afterFirstRuleApplication.getThings().stream().filter(w -> w instanceof  LabyrinthElement)
                .map(w -> (WorldObject) w)
                .filter(w -> w.getxPos() == 1 && w.getyPos() == 1).findFirst().get();

        assertEquals(2 ,topLeft.getRelations().size());
        assertEquals(2, bottomLeft.navigateRelation(WorldRelation.LOCATED_ON, "subject").size());

        // In the new world state, eat marble and move right should be possible

        assertFalse(moveDown.getContext().match(afterFirstRuleApplication).isPresent());
        assertTrue(moveRight.getContext().match(afterFirstRuleApplication).isPresent());
        assertTrue(eatMarble.getContext().match(afterFirstRuleApplication).isPresent());

        engine.executeRules(); // i.e. eat marble and move right

        // Now Pacman and the red ghost are on the same location, thus eat Pacman should be active

        assertTrue(eatPacman.getContext().match(engine.getCurrentState()).isPresent());
    }

    @Test
    public void testBig() {
        PacmanDomain domain = new PacmanDomain();
        RuleEngine ruleEngine = new RuleEngine("fgp", domain.startWorld());


        Rule moveRight = ruleEngine.getOrCreateRule("", "moveRight");
        domain.configureMoveRightRule(moveRight);

        Rule moveLeft = ruleEngine.getOrCreateRule("", "moveLeft");
        domain.configureMoveLeftRule(moveLeft);

        Rule moveUp = ruleEngine.getOrCreateRule("", "moveUp");
        domain.configureMoveUpRule(moveUp);

        Rule moveDown = ruleEngine.getOrCreateRule("", "moveDown");
        domain.configureMoveDownRule(moveDown);

        Rule eatMarble = ruleEngine.getOrCreateRule("", "eatMarble");
        domain.configureEatMarbleRule(eatMarble);
        eatMarble.setTrigger(Trigger.never(ruleEngine));


        Trigger.ManualTrigger moveRightTrigger = Trigger.manual(ruleEngine, true);
        moveRight.setTrigger(moveRightTrigger);
        Trigger.ManualTrigger moveLeftTrigger = Trigger.manual(ruleEngine, false);
        moveLeft.setTrigger(moveLeftTrigger);
        Trigger.ManualTrigger moveUpTrigger = Trigger.manual(ruleEngine, false);
        moveUp.setTrigger(moveUpTrigger);
        Trigger.ManualTrigger moveDownTrigger = Trigger.manual(ruleEngine, false);
        moveDown.setTrigger(moveDownTrigger);

        assertTrue(moveRight.getContext().match(ruleEngine.getCurrentState()).isPresent());
        assertFalse(eatMarble.getContext().match(ruleEngine.getCurrentState()).isPresent());

        Optional<WorldObject> pacman = ruleEngine.getCurrentState().findSingleton(new PacmanFigures.Pacman());
        Integer X = WorldRelation.LocatedOn.getXPosOfObjectInWorld(pacman.get(), ruleEngine.getCurrentState());
        Integer Y = WorldRelation.LocatedOn.getYPosOfObjectInWorld(pacman.get(), ruleEngine.getCurrentState());
        assertNotNull(X);
        assertNotNull(Y);

        System.out.println("X: " + X + ", Y: " + Y);
        ruleEngine.executeRules();

        Integer X2 = WorldRelation.LocatedOn.getXPosOfObjectInWorld(pacman.get(), ruleEngine.getCurrentState());
        Integer Y2 = WorldRelation.LocatedOn.getYPosOfObjectInWorld(pacman.get(), ruleEngine.getCurrentState());

        assertEquals(X + 1, (int) X2);
        assertEquals(Y, Y2);
        assertTrue(moveRight.getContext().match(ruleEngine.getCurrentState()).isPresent());
        assertTrue(eatMarble.getContext().match(ruleEngine.getCurrentState()).isPresent());

        ruleEngine.executeRules();

        Integer X3 = WorldRelation.LocatedOn.getXPosOfObjectInWorld(pacman.get(), ruleEngine.getCurrentState());
        Integer Y3 = WorldRelation.LocatedOn.getYPosOfObjectInWorld(pacman.get(), ruleEngine.getCurrentState());
        assertEquals(X + 2, (int) X3);
        assertEquals(Y, Y3);

        ruleEngine.executeRules();

        moveRightTrigger.setValue(false);
        moveDownTrigger.setValue(true);

        ruleEngine.executeRules();
        Integer X4 = WorldRelation.LocatedOn.getXPosOfObjectInWorld(pacman.get(), ruleEngine.getCurrentState());
        Integer Y4 = WorldRelation.LocatedOn.getYPosOfObjectInWorld(pacman.get(), ruleEngine.getCurrentState());

        assertEquals(X + 3, (int) X4);
        assertEquals(Y + 1, (int) Y4);


    }


    @Test
    public void testGhostBehavior() {
        PacmanLabyrinthBuilder b = new PacmanLabyrinthBuilder("P  \n R \n   \n");
        World world = b.build();

        PacmanDomain domain = new PacmanDomain();
        RuleEngine ruleEngine = new RuleEngine("fgp", world);

        Rule moveRedGhostUp = ruleEngine.getOrCreateRule("redGhost", "moveRedGhostUp");
        domain.configureGhostUp(moveRedGhostUp, PacmanFigures.GhostColor.RED);
        Rule moveRedGhostDown = ruleEngine.getOrCreateRule("redGhost", "moveRedGhostDown");
        domain.configureGhostDown(moveRedGhostDown, PacmanFigures.GhostColor.RED);
        Rule moveRedGhostLeft = ruleEngine.getOrCreateRule("redGhost", "moveRedGhostLeft");
        domain.configureGhostLeft(moveRedGhostLeft, PacmanFigures.GhostColor.RED);
        Rule moveRedGhostRight = ruleEngine.getOrCreateRule("redGhost", "moveRedGhostRight");
        domain.configureGhostRight(moveRedGhostRight, PacmanFigures.GhostColor.RED);


        ruleEngine.setRuleGroupMode("redGhost", RuleGroup.ExecutionType.ALTERNATIVE);

        Optional<WorldObject> ghost = ruleEngine.getCurrentState().findSingleton(new PacmanFigures.RedGhost());
        Integer X = WorldRelation.LocatedOn.getXPosOfObjectInWorld(ghost.get(), ruleEngine.getCurrentState());
        Integer Y = WorldRelation.LocatedOn.getYPosOfObjectInWorld(ghost.get(), ruleEngine.getCurrentState());
        assertEquals(1, (int) X);
        assertEquals(1, (int) Y);

        ruleEngine.executeRules();


        Integer XTPlus1 = WorldRelation.LocatedOn.getXPosOfObjectInWorld(ghost.get(), ruleEngine.getCurrentState());
        Integer YTPlus1 = WorldRelation.LocatedOn.getYPosOfObjectInWorld(ghost.get(), ruleEngine.getCurrentState());

        assertTrue(
                (XTPlus1.equals(2) && YTPlus1.equals(1)) ||
                        (XTPlus1.equals(1) && YTPlus1.equals(2)) ||
                        (XTPlus1.equals(0) && YTPlus1.equals(1)) ||
                        (XTPlus1.equals(1) && YTPlus1.equals(0))
        );

        Rule eatPacman = ruleEngine.getOrCreateRule("", "eatPacman");
        domain.configureGhostKillsPacmanRule(eatPacman, PacmanFigures.GhostColor.RED);

        Optional<WorldObject> pacman = ruleEngine.getCurrentState().findSingleton(new PacmanFigures.Pacman());
        assertTrue(pacman.isPresent());

        moveRedGhostRight.setTrigger(Trigger.never(ruleEngine));
        moveRedGhostDown.setTrigger(Trigger.never(ruleEngine));

        while (pacman.isPresent()) {
            ruleEngine.executeRules();
            pacman = ruleEngine.getCurrentState().findSingleton(new PacmanFigures.Pacman());
        }

        Integer FinalGhostX = WorldRelation.LocatedOn.getXPosOfObjectInWorld(ghost.get(), ruleEngine.getCurrentState());
        Integer FinalGhostY = WorldRelation.LocatedOn.getYPosOfObjectInWorld(ghost.get(), ruleEngine.getCurrentState());

        assertEquals(0, (int) FinalGhostX);
        assertEquals(0, (int) FinalGhostY);
    }

    @Test
    public void testSetUp() {
        PacmanDomain domain = new PacmanDomain();
        RuleEngine ruleEngine = new RuleEngine("fgp", domain.startWorld());
        domain.domainSpecificRuleSetUp(ruleEngine);

        assertEquals(6, ruleEngine.getRules().getRules().size());
    }

    private static char[][] DIAG_TOP_DOWN_X_WINS = new char[][]{{ 'X', ' ', ' '}, {' ', 'X', ' '}, {' ', ' ', 'X'}};
    private static char[][] DIAG_BOTTOM_UP_O_WINS = new char[][]{{ ' ', ' ', 'O'}, {' ', 'O', ' '}, {'O', ' ', ' '}};
    private static int CUBE_SIZE = 3; // antar at array er alltid en kvadrat

    @Test
    public void testTicTacToeDiagonals() {
        assertTrue(hasPlayerWohn(DIAG_TOP_DOWN_X_WINS, 'X'));
        assertFalse(hasPlayerWohn(DIAG_TOP_DOWN_X_WINS, 'O'));
        assertTrue(hasPlayerWohn(DIAG_BOTTOM_UP_O_WINS, 'O'));
        assertFalse(hasPlayerWohn(DIAG_BOTTOM_UP_O_WINS, 'X'));
    }

    private static boolean hasPlayerWohn(char[][] field, char player) {
        return checkDownDiagonal(field, player) || checkUpDiagonal(field, player); // TODO plus all horizontal and diagonals
    }

    private static boolean checkDownDiagonal(char[][] field, char player) {
        for (int i = 0; i < CUBE_SIZE; i++) {
            if (field[i][i] != player) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkUpDiagonal(char[][] field, char player) {
        for (int i = 0; i < CUBE_SIZE; i++) {
            if (field[i][CUBE_SIZE - 1 - i] != player) {
                return false;
            }
        }
        return true;
    }

}

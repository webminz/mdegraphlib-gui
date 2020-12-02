package no.hvl.past.webui.backend.quickRuleEngine.pacman;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PacmanLabyrinthBuilder {

    private static final char MARBLE_SIGN = '.';
    private static final char WALL_SIGN = 'W';
    private static final char EMPTY_CORRIDOR_SIGN = ' ';
    private static final char PACMAN_LOCATION = 'P';
    private static final char RED_GHOST_LOCATION = 'R';
    private static final char TEAL_GHOST_LOCATION = 'T';
    private static final char ROSE_GHOST_LOCATION = 'S';
    private static final char ORANGE_GHOST_LOCATION = 'O';
    private static final char NEWLINE = '\n';


    private World.Builder worldBuilder;
    private String memorySource;
    private File fileSource;
    private boolean isMemory;

    private int width = 0;
    private int height = 0;
    private int currentX = 0;
    private int currentY = 0;
    private List<LabyrinthElement> lastRow = new ArrayList<>();
    private List<LabyrinthElement> currentRow = new ArrayList<>();

    public PacmanLabyrinthBuilder(String rep) {
        this.worldBuilder = new World.Builder();
        this.memorySource = rep;
        this.isMemory = true;
    }

    public PacmanLabyrinthBuilder(File file) {
        this.worldBuilder = new World.Builder();
        this.fileSource = file;
        this.isMemory = false;
    }

    private void handle(int character) {
        if (character > 0) {
            char c = (char) character;
            switch (character) {
                case MARBLE_SIGN:
                    PacmanFigures.Marble m = new PacmanFigures.Marble();
                    LabyrinthElement.Corridor corr = new LabyrinthElement.Corridor();
                    this.currentRow.add(corr);
                    this.worldBuilder.object(corr, currentX, currentY);
                    this.worldBuilder.locatedOn(m, corr);
                    currentX++;
                    break;
                case WALL_SIGN:
                    LabyrinthElement.Wall w = new LabyrinthElement.Wall();
                    this.currentRow.add(w);
                    this.worldBuilder.object(w, currentX, currentY);
                    currentX++;
                    break;
                case EMPTY_CORRIDOR_SIGN:
                    LabyrinthElement.Corridor x = new LabyrinthElement.Corridor();
                    this.currentRow.add(x);
                    this.worldBuilder.object(x, currentX, currentY);
                    currentX++;
                    break;
                case PACMAN_LOCATION:
                    PacmanFigures.Pacman p = new PacmanFigures.Pacman();
                    LabyrinthElement.Corridor corr1 = new LabyrinthElement.Corridor();
                    this.currentRow.add(corr1);
                    this.worldBuilder.object(corr1, currentX, currentY);
                    this.worldBuilder.locatedOn(p, corr1);
                    currentX++;
                    break;
                case RED_GHOST_LOCATION:
                    PacmanFigures.Ghost rg = new PacmanFigures.RedGhost();
                    LabyrinthElement.Corridor corr2 = new LabyrinthElement.Corridor();
                    this.currentRow.add(corr2);
                    this.worldBuilder.object(corr2, currentX, currentY);
                    this.worldBuilder.locatedOn(rg, corr2);
                    currentX++;
                    break;
                case TEAL_GHOST_LOCATION:
                    PacmanFigures.Ghost tg = new PacmanFigures.TealGhost();
                    LabyrinthElement.Corridor corr3 = new LabyrinthElement.Corridor();
                    this.currentRow.add(corr3);
                    this.worldBuilder.object(corr3, currentX, currentY);
                    this.worldBuilder.locatedOn(tg, corr3);
                    currentX++;
                    break;
                case ROSE_GHOST_LOCATION:
                    PacmanFigures.Ghost sg = new PacmanFigures.RoseGhost();
                    LabyrinthElement.Corridor corr4 = new LabyrinthElement.Corridor();
                    this.currentRow.add(corr4);
                    this.worldBuilder.object(corr4, currentX, currentY);
                    this.worldBuilder.locatedOn(sg, corr4);
                    currentX++;
                    break;
                case ORANGE_GHOST_LOCATION:
                    PacmanFigures.Ghost og = new PacmanFigures.OrangeGhost();
                    LabyrinthElement.Corridor corr5 = new LabyrinthElement.Corridor();
                    this.currentRow.add(corr5);
                    this.worldBuilder.object(corr5, currentX, currentY);
                    this.worldBuilder.locatedOn(og, corr5);
                    currentX++;
                    break;
                case NEWLINE:
                    while (this.lastRow.size() > this.currentRow.size()) {
                        LabyrinthElement.Corridor cplus = new LabyrinthElement.Corridor();
                        this.currentRow.add(cplus);
                        this.worldBuilder.object(cplus, currentX, currentY);
                        currentX++;
                    }
                    for (int i = 0; i < this.currentRow.size(); i++) {
                        if (!this.lastRow.isEmpty()) {
                            this.worldBuilder.locatedAbove(this.lastRow.get(i), this.currentRow.get(i));
                        }
                        if (i + 1 < currentRow.size()) {
                            this.worldBuilder.locatedBesides(this.currentRow.get(i), this.currentRow.get(i + 1));
                        }
                    }
                    this.currentX = 0;
                    this.currentY++;
                    this.lastRow.clear();
                    this.lastRow.addAll(this.currentRow);
                    this.currentRow.clear();
                    break;
            }
        }
    }

    public World build()   {
        if (isMemory) {
            memorySource.chars().forEach(this::handle);
        } else {
            try {
                FileInputStream fis = new FileInputStream(fileSource);
                int c;
                while ((c = fis.read()) >= 0) {
                    handle(c);
                }
                fis.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.worldBuilder.build();
    }
}

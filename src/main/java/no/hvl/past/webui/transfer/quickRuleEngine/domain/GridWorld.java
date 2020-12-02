package no.hvl.past.webui.transfer.quickRuleEngine.domain;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Thing;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldRepresentation;

import java.util.*;

public class GridWorld extends WorldRepresentation {


    public static class Cell extends WorldRepresentation.VisualElement {

        private int x;
        private int y;
        private String displayName;

        public Cell(Thing displays, String displayName, String imageUrl, Integer xParam, Integer yParam, String... cssClassNames) {
            super(displays, imageUrl, cssClassNames);
            this.x = (xParam == null) ? -1 : xParam;
            this.y = (yParam == null) ? -1 : yParam;
            this.displayName = displayName;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String getTitle() {
            return displayName;
        }
    }

    public static class Figure extends WorldRepresentation.VisualElement {

        private int currentX;
        private int currentY;
        private String caption;

        public Figure(Thing displays, String imageUrl, Integer currentXParam, Integer currentYParam, String caption, String... cssClassNames) {
            super(displays, imageUrl, cssClassNames);
            this.currentX = currentXParam == null ? -1 : currentXParam;
            this.currentY = currentYParam == null ? -1 : currentYParam;
            this.caption = caption;
        }

        public int getCurrentX() {
            return currentX;
        }

        public void setCurrentX(int currentX) {
            this.currentX = currentX;
        }

        public int getCurrentY() {
            return currentY;
        }

        public void setCurrentY(int currentY) {
            this.currentY = currentY;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        @Override
        public String getTitle() {
            return caption;
        }
    }

    public static class Context extends WorldRepresentation.VisualElement {

        private String caption;

        public Context(Thing displays, String imageUrl, String caption, String... cssClassNames) {
            super(displays, imageUrl, cssClassNames);
            this.caption = caption;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        @Override
        public String getTitle() {
            return getCaption();
        }
    }

    private int width;
    private int height;
    private List<Cell> cellGrid;
    private Collection<Figure> figures;
    private Collection<Context> contextObjects;

    public GridWorld(World world, int width, int height, List<Cell> cellGrid, Collection<Figure> figures, Collection<Context> contextObjects) {
        super(world);
        this.width = width;
        this.height = height;
        this.cellGrid = cellGrid;
        this.figures = figures;
        this.contextObjects = contextObjects;
    }

    public Cell getCell(int x, int y) {
        return this.cellGrid.get(y * width + x);
    }

    public List<Cell> getCellGrid() {
        return cellGrid;
    }

    public Collection<Figure> getFigures() {
        return figures;
    }

    public Collection<Context> getContextObjects() {
        return contextObjects;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Collection<VisualElement> getDisplayed() {
        Collection<VisualElement> result = new ArrayList<>();
        result.addAll(figures);
        result.addAll(this.cellGrid);
        result.addAll(contextObjects);
        return result;
    }
}

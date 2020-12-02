package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.GridWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class GridWorldPatternEditor extends VerticalLayout {




    public enum Frontier {
        INITIAL,
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NONE
    }


    private static class GridRow extends HorizontalLayout {

        private final List<GridDropZone> cells;
        private int yPos;
        private final String editorId;
        private final RuleEditorView mainView;

        public GridRow(String editorId, int yPos, RuleEditorView mainView) {
            this.editorId = editorId;
            this.mainView = mainView;
            this.setSpacing(false);
            this.setPadding(false);
            this.yPos = yPos;
            this.cells = new ArrayList<>();
            GridDropZone firstElement = new GridDropZone(editorId, 0, yPos, mainView);
            this.cells.add(firstElement);
            add(firstElement);
        }


        public Optional<GridDropZone> getCell(int col) {
            if (col < 0 || (col + 1) > cells.size()) {
                return Optional.empty();
            } else {
                return Optional.of(cells.get(col));
            }
        }

        public int getColumns() {
            return cells.size();
        }

        public void addCellAtEnd() {
            GridDropZone element = new GridDropZone(editorId, cells.size(), yPos, mainView);
            this.cells.add(element);
            add(element);
        }

        public void addAtBeginning() {
            GridDropZone element = new GridDropZone(editorId, 0, yPos, mainView);
            this.cells.add(0, element);
            removeAll();
            for (GridDropZone component : this.cells) {
                component.setPosX(component.getPosX() + 1);
                add(component);
            }
            this.cells.get(0).setPosX(0);
        }

        public int getyPos() {
            return yPos;
        }

        public void setyPos(int yPos) {
            this.yPos = yPos;
            for (GridDropZone dropZone : this.cells) {
                dropZone.setPosY(yPos);
            }
        }
    }

    private List<GridRow> rows;
    private final String editorId;
    private final RuleEditorView ruleEditor;

    public GridWorldPatternEditor(String editorId, RuleEditorView ruleEditor) {
        addClassName("drop-zone-grid");
        this.editorId = editorId;
        this.ruleEditor = ruleEditor;
        this.rows = new ArrayList<>();
        this.setPadding(false);
        this.setSpacing(false);
    }

    public void clear() {
        if (!rows.isEmpty()) {
            this.rows.clear();
            this.removeAll();
        }
    }

    public boolean isEmpty() {
        return this.rows.isEmpty();
    }

    public void addFirst() {
        GridRow firstRow = new GridRow(editorId, 0, ruleEditor);
        this.rows.add(firstRow);
        this.add(firstRow);
    }

    public void fill(int x, int y, GridWorld.Cell cell) {
        getCell(x, y).get().fill(cell);
    }

    public void addFigure(int x, int y, GridWorld.Figure figure) {
        getCell(x, y).get().addFigure(figure);
    }

    public void removeFigure(int x, int y, GridWorld.Figure figure) {
        getCell(x, y).get().removeFigure(figure);
    }

    public void highlightAll() {
        for (GridRow row : this.rows) {
            for (GridDropZone cell : row.cells) {
                cell.highlightActive();
            }
        }
    }

    public void highlightEmpty() {
        for (GridRow row : this.rows) {
            for (GridDropZone cell : row.cells) {
                if (!cell.isFilled()) {
                    cell.highlightActive();
                }
            }
        }
    }

    public void highlightFilled() {
        for (GridRow row : this.rows) {
            for (GridDropZone cell : row.cells) {
                if (cell.isFilled()) {
                    cell.highlightActive();
                }
            }
        }
    }

    public void unhighlight() {
        for (GridRow row : this.rows) {
            for (GridDropZone cell : row.cells) {
                cell.deHighlight();
            }
        }
    }

    public Optional<GridWorld.Cell> getEastOf(int targetXPos, int targetYPos) {
        return this.getCell(targetXPos + 1, targetYPos).filter(GridDropZone::isFilled).map(GridDropZone::getCell);
    }

    public Optional<GridWorld.Cell> getWestOf(int targetXPos, int targetYPos) {
        return this.getCell(targetXPos - 1, targetYPos).filter(GridDropZone::isFilled).map(GridDropZone::getCell);
    }

    public Optional<GridWorld.Cell> getNorthOf(int targetXPos, int targetYPos) {
        return this.getCell(targetXPos, targetYPos - 1).filter(GridDropZone::isFilled).map(GridDropZone::getCell);

    }

    public Optional<GridWorld.Cell> getSouthOf(int targetXPos, int targetYPos) {
        return this.getCell(targetXPos, targetYPos + 1).filter(GridDropZone::isFilled).map(GridDropZone::getCell);
    }

    public void growUpTo(int targetXPos, int targetYPos) {
        // TODO
        switch (getFrontier(targetXPos, targetYPos)) {
            case INITIAL:
                this.growCentrically();
                break;
            case NORTH:
                this.growNorth(targetXPos);
                break;
            case EAST:
                this.growEast(targetYPos);
                break;
            case WEST:
                this.growWest(targetYPos);
                break;
            case SOUTH:
                this.growSouth(targetXPos);
                break;
            case NONE:
            default: // Nothing to do
                break;
        }
    }

    public Frontier getFrontier(int x, int y) {
        if (rows.size() == 1 && this.rows.get(0).cells.size() == 1) {
            return Frontier.INITIAL;
        } else if (y == 0) {
            return Frontier.NORTH;
        } else if (y + 1 == this.rows.size()) {
        } else {
            if (x == 0) {
                return Frontier.WEST;
            } else if (x + 1 == this.rows.get(y).cells.size()) {
                return Frontier.EAST;
            }
        }
        return Frontier.NONE;
    }

    public void insertCell(GridWorld.Cell cell) {
        if (isEmpty()) {
            this.addFirst();
        }
        this.growUpTo(cell.getX(), cell.getY());
        this.fill(cell.getX(), cell.getY(), cell);
    }

    private void growCentrically() {
        this.rows.get(0).getCell(0).ifPresent(gdz -> {
            gdz.getCell().setX(1);
            gdz.getCell().setY(1);
        });
        this.rows.get(0).addCellAtEnd();
        this.rows.get(0).addAtBeginning();
        this.appendRow();
        this.prependRow();
    }

    private void growWest(int responsibleRow) {
        this.getRow(responsibleRow).ifPresent(GridRow::addAtBeginning);
        this.getRow(responsibleRow - 1).ifPresent(GridRow::addAtBeginning);
        this.getRow(responsibleRow + 1).ifPresent(GridRow::addCellAtEnd);
    }

    private void growEast(int responsibleRow) {
        this.getRow(responsibleRow).ifPresent(GridRow::addCellAtEnd);
        this.getRow(responsibleRow - 1).ifPresent(GridRow::addCellAtEnd);
        this.getRow(responsibleRow + 1).ifPresent(GridRow::addCellAtEnd);
        // TODO
    }

    private void growNorth(int responsibleColumn) {
        prependRow();
        int size = this.rows.get(1).getColumns();
        for (int i = 0; i < size; i++) {
            this.rows.get(0).addCellAtEnd();
        }
    }

    private void growSouth(int responsibleColumn) {
        appendRow();
        int size = this.rows.get(this.rows.size() - 2).getColumns();
        for (int i = 0; i < size; i++) {
            this.rows.get(this.rows.size() - 1).addCellAtEnd();
        }
    }

    public void appendRow() {
        GridRow row = new GridRow(editorId, rows.size(), ruleEditor);
        this.rows.add(row);
        this.add(row);
    }

    public void prependRow() {
        GridRow row = new GridRow(editorId, 0, ruleEditor);
        this.rows.add(0, row);
        this.removeAll();
        for (GridRow c : this.rows) {
            c.setyPos(c.getyPos() + 1);
            this.add(c);
        }
        this.rows.get(0).setyPos(0);
    }

    public int getRows() {
        return this.rows.size();
    }

    private Optional<GridRow> getRow(int row) {
        if (row < 0 || (row + 1) > this.rows.size()) {
            return Optional.empty();
        }
        return Optional.of(this.rows.get(row));
    }

    public Optional<GridDropZone> getCell(int x, int y) {
        return getRow(y).flatMap(r -> r.getCell(x));
    }

}

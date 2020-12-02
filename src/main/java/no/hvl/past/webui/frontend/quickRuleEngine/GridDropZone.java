package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.GridWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GridDropZone extends Div implements DropTarget<GridDropZone> {

    private GridWorld.Cell background;
    private List<GridWorld.Figure> figures = new ArrayList<>();

    private int posX;
    private int posY;
    private String editorID;
    private RuleEditorView mainView;


    public GridDropZone(String editorId, int x, int y, RuleEditorView mainGUI) {
        addClassName("drop-zone");
        this.posX = x;
        this.posY = y;
        this.editorID = editorId;
        this.mainView = mainGUI;

      //  setDropEffect(DropEffect.COPY);
        addDropListener(event -> {
            DragItem.DragData data = (DragItem.DragData) event.getDragData().get();
            mainGUI.dropped(data.getElement(), data.getSourcePatternEditor(), data.getSourceXPos(), data.getSourceYPos(), editorId, posX, posY);
        });

        setActive(true);
    }

    public void fill(GridWorld.Cell cell) {
        this.background = cell;
        getStyle().set("background-image", "url(" + cell.getImageUrl() + ")");
        getStyle().set("background-repeat", "no-repeat");
        getStyle().set("background-position", "center");
        getStyle().set("background-size", "contain");
    }

    public boolean isFilled() {
        return this.background != null;
    }

    public void addFigure(GridWorld.Figure figure) {
        this.figures.add(figure);
        DragItem dragItem = new DragItem(mainView, figure, editorID, posX, posY, false);
        dragItem.addClassName("drop-zone-child");
        add(dragItem);
    }

    public void removeFigure(GridWorld.Figure figure) {
        Optional<DragItem> first = getChildren().filter(c -> c instanceof DragItem)
                .map(c -> (DragItem) c)
                .filter(dragItem -> dragItem.getData().getItemKey().startsWith(figure.getDisplays().getName()))
                .findFirst();
        if (first.isPresent()) {
            remove(first.get());
        }
        this.figures.remove(figure);
    }

    public List<GridWorld.Figure> getFigures() {
        return figures;
    }

    public void highlightActive() {
        addClassName("active");
    }

    public void highlightForbidden() {
        addClassName("forbidden");
    }

    public void deHighlight() {
        removeClassNames("active", "forbidden");
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public GridWorld.Cell getCell() {
        return this.background;
    }
}

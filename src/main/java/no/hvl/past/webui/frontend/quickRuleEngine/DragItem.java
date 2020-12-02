package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.GridWorld;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldRepresentation;

import java.util.Optional;

public class DragItem extends Div implements DragSource<DragItem> {

    public static class DragData {

        private final WorldRepresentation.VisualElement element;
        private final String sourcePatternEditor;
        private final Integer sourceXPos;
        private final Integer sourceYPos;

        public DragData(WorldRepresentation.VisualElement element, String sourcePatternEditor, Integer sourceXPos, Integer sourceYPos) {
            this.element = element;
            this.sourcePatternEditor = sourcePatternEditor;
            this.sourceXPos = sourceXPos;
            this.sourceYPos = sourceYPos;
        }

        public WorldRepresentation.VisualElement getElement() {
            return element;
        }

        public String getSourcePatternEditor() {
            return sourcePatternEditor;
        }

        public String getItemKey() {
            StringBuilder builder = new StringBuilder();
            builder.append(element.getDisplays().getName());
            if (this.sourcePatternEditor != null) {
                builder.append('_');
                builder.append(sourcePatternEditor);
            }
            if (this.sourceXPos != null) {
                builder.append('_');
                builder.append(sourceXPos);
            }
            if (this.sourceYPos != null) {
                builder.append('_');
                builder.append(sourceYPos);
            }
            return builder.toString();
        }

        public Integer getSourceXPos() {
            return sourceXPos;
        }

        public Integer getSourceYPos() {
            return sourceYPos;
        }
    }


    private final DragData data;

    public DragItem(RuleEditorView mainView, WorldRepresentation.VisualElement element, String sourceEditorID, Integer xPos, Integer yPos) {
        this(mainView, element, sourceEditorID, xPos, yPos, true);
    }


    public DragItem(RuleEditorView mainView, WorldRepresentation.VisualElement element, String sourceEditorID, Integer xPos, Integer yPos, boolean withCaption) {
        this.data = new DragData(element, sourceEditorID, xPos, yPos);

        addClassName("drag-item");

        if (element.getImageUrl() != null && !element.getImageUrl().isEmpty()) {
            Image img = new Image(element.getImageUrl(), "");
            img.addClassName("drag-item-img");
            add(img);
        }

        if (withCaption) {
            Span titleSpan = new Span(element.getTitle());
            titleSpan.addClassName("drag-item-title");
            add(titleSpan);
        }


        for (String cssClass : element.getCssClassNames()) {
            addClassName(cssClass);
        }

        // maybe later
       //  if (subtitle != null) {
       //     Span subtitleSpan = new Span(subtitle);
        //    subtitleSpan.addClassName("drag-item-subtitle");
        //    add(subtitleSpan);
       // }

        setDraggable(true);
        setDragData(getData());

        addDragStartListener(event -> {
            addClassName("moving");
            addClassName("animate__animated");
            addClassName("animate__shakeX");
            mainView.startDragging(getData().element, getData().getSourcePatternEditor());
        });
        addDragEndListener(event -> {
            removeClassName("moving");
            removeClassName("animate__animated");
            removeClassName("animate__shakeX");
            mainView.stopDragging();
        });
    }

    public DragData getData() {
        return this.data;
    }


}

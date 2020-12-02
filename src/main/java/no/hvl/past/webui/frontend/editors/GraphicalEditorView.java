package no.hvl.past.webui.frontend.editors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.Route;
import no.hvl.past.webui.frontend.MainLayout;

@Route(value = "editor", layout = MainLayout.class)
@CssImport("./styles/diaged-styles.css")
@JsModule("./src/diaged/editor.ts")
@JavaScript("./src/run.js")
public class GraphicalEditorView extends VerticalLayout {



    private String currentModelId;

    public GraphicalEditorView() {
        setSizeFull();
        setPadding(false);
        setMargin(false);




        Div editorContainer = new Div();
        editorContainer.addClassName("editor-container");
        editorContainer.setId("TEST");
        editorContainer.setSizeFull();

        add(editorContainer);


        Page page = UI.getCurrent().getPage();
        page.executeJs("window.startGraphicalEditor($0, '1', null)", editorContainer);
    }
}

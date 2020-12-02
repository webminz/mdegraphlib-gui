package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldRepresentation;

import java.util.*;

public abstract class MenuItem {

    public static class Container extends MenuItem {

        private final String name;
        private final String imageUrl;
        private final VaadinIcon icon;
        private final List<MenuItem> children;

        public Container(String name, String imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
            this.icon = null;
            this.children = new ArrayList<>();
        }

        public Container(String name) {
            this.name = name;
            this.imageUrl = null;
            this.icon = null;
            this.children = new ArrayList<>();
        }

        public Container(String name, VaadinIcon icon) {
            this.name = name;
            this.imageUrl = null;
            this.icon = icon;
            this.children = new ArrayList<>();
        }

        public void prependChild(MenuItem item) {
            item.setParent(this);
            children.add(0,item);
        }

        public void appendChild(MenuItem item) {
            item.setParent(this);
            children.add(item);
        }

        public void appendPenultimateChild(MenuItem item) {
            if (children.isEmpty()) {
                this.appendChild(item);
            } else if (this.children.size() == 1) {
                this.prependChild(item);
            } else {
                item.setParent(this);
                this.children.add(children.size() - 1, item);
            }

        }

        @Override
        public Component render(RuleEditorView mainGui) {
            Div title = new Div();
            title.addClassName("tree-item-group");
            if (imageUrl != null) {
                Image img = new Image(imageUrl, "");
                title.add(img);
            } else if (icon != null) {
                title.add(new Icon(icon));
            }
            Span titleName = new Span(name);
            title.add(titleName);
            return title;
        }

        @Override
        public Collection<MenuItem> children() {
            return children;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class RuleItem extends MenuItem {

        private final String ruleName;
        private final Image ruleIcon;
        private final Span ruleNameSpan;
        private RuleEditorView mainGui;
        private Div resultComponent;

        public RuleItem(String ruleName) {
            this.ruleName = ruleName;
            ruleIcon = new Image("images/pacman/rule.png", "");
            ruleIcon.addClassName("rule-icon");
            ruleNameSpan = new Span(this.ruleName);
            ruleNameSpan.addClickListener(click -> {
                if (click.getClickCount() == 1) {
                    mainGui.ruleSelected(this.ruleName);
                    // TODO add controls to component
                } else {
                    resultComponent.remove(ruleNameSpan);
                    TextField textField = new TextField();
                    textField.setValue(ruleName);
                    textField.addBlurListener(event -> {
                        renderNewAfterRename(ruleName, textField.getValue());
                    });
                    textField.addKeyPressListener(Key.ENTER, event -> {
                        renderNewAfterRename(ruleName, textField.getValue());
                    });
                    resultComponent.add(textField);
                    textField.focus();
                }
            });
        }

        @Override
        public Component render(RuleEditorView mainGui) {
            this.mainGui = mainGui;
            this.resultComponent = new Div();
            resultComponent.addClassName("tree-item-rule");
            makeContent();
            return resultComponent;
        }

        private void makeContent() {
            this.resultComponent.add(ruleIcon);
            this.resultComponent.add(ruleNameSpan);
        }

        private void renderNewAfterRename(String oldValue, String newValue) {
            mainGui.ruleRenamed(oldValue, newValue);
            resultComponent.removeAll();
            makeContent();
        }

        @Override
        public Collection<MenuItem> children() {
            return Collections.emptyList();
        }

        @Override
        public String getName() {
            return ruleName;
        }
    }

    public static class NewRuleButton extends MenuItem {

        public static final String NY_REGEL = "Ny Regel";
        public static final String NY_GRUPPE = "Ny Gryppe";

        @Override
        public Component render(RuleEditorView mainGui) {
            HorizontalLayout layout = new HorizontalLayout();
            Button btn = new Button(NY_REGEL, new Icon(VaadinIcon.PLUS));
            btn.addClassName("tree-item-btn");
            btn.addClickListener(click -> {
                mainGui.newRule(getParent().map(MenuItem::getName).orElse(""));
            });
            Button btn2 = new Button(NY_GRUPPE, new Icon(VaadinIcon.FOLDER_ADD));
            btn2.addClassName("tree-item-btn");
            btn2.addClickListener(click -> {
                mainGui.newGroup(getParent().map(MenuItem::getName).orElse(""));
            });
            layout.add(btn);
            layout.add(btn2);
            return layout;
        }

        @Override
        public Collection<MenuItem> children() {
            return Collections.emptyList();
        }

        @Override
        public String getName() {
            return NY_REGEL;
        }
    }

    public static class ObjectItem extends MenuItem {

        private final WorldRepresentation.VisualElement element;

        public ObjectItem(WorldRepresentation.VisualElement object) {
            this.element = object;
        }

        @Override
        public Component render(RuleEditorView mainGui) {
            DragItem dragItem = new DragItem(mainGui, element, null, null, null);
            dragItem.addClassName("tree-item-object");
            return dragItem;
        }

        @Override
        public Collection<MenuItem> children() {
            return Collections.emptyList();
        }

        @Override
        public String getName() {
            return element.getTitle();
        }
    }

    private MenuItem parent = null;

    public Optional<MenuItem> getParent() {
        if (parent == null) {
            return Optional.empty();
        }
        return Optional.of(parent);
    }

    protected void setParent(MenuItem parent) {
        this.parent = parent;
    }

    public boolean hasChildren() {
        return !children().isEmpty();
    }
    public boolean isLeaf() {
        return children().isEmpty();
    }
    public abstract Component render(RuleEditorView mainGui);
    public abstract Collection<MenuItem> children();

    public abstract String getName();


}

package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.Consumer;

public class SwitcherButton extends Div {

    private final String ccaption;
    private final Consumer<ClickEvent<?>> eventHandler;

    public SwitcherButton(String ccaption, Consumer<ClickEvent<?>> eventHandler) {
        this.ccaption = ccaption;
        this.eventHandler = eventHandler;
        getStyle().set("position", "absolute");
        getStyle().set("right", "50px");
        getStyle().set("bottom", "30px");
        getStyle().set("width", "250px");
        getStyle().set("height", "50px");
        Button btn = new Button(ccaption, new Icon(VaadinIcon.EXTERNAL_LINK));
        btn.setIconAfterText(true);
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btn.addClickListener(eventHandler::accept);
        add(btn);
        btn.setSizeFull();

    }
}

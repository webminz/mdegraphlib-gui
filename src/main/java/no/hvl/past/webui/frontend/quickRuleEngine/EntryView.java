package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import no.hvl.past.webui.transfer.quickRuleEngine.service.RuleEngineConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("forskergrandprix")
public class EntryView extends VerticalLayout {

    private static final String PACMAN_DOMAIN = "PACMAN";
    private static final String USERNAME_LABEL_NO = "Ditt brukernavn?";
    private static final String CONFIRM_LABEL_NO = "Kom i gang";
    private static final String ERROR_MSG_EMPTY_USERNAME = "Du må velge et navn! Kanskje din epost adresse?";
    public static final String WELCOME_HEADER_NO = "Velkommen!";


    private TextField username;
    private Button confirm;
    private RuleEngineConfigurationService service;

    public EntryView(@Autowired RuleEngineConfigurationService service) {
        setSizeFull();
        this.service = service;
        this.username = new TextField(USERNAME_LABEL_NO);
        this.confirm = new Button(CONFIRM_LABEL_NO);
        this.confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.confirm.addClickListener((event) -> click());
        this.setAlignItems(Alignment.CENTER);
        add(new H1(WELCOME_HEADER_NO));
        add(username);
        add(confirm);
    }

    private void click() {
        if (this.username.getValue() == null || this.username.getValue().isEmpty()) {
            this.username.setInvalid(true);
            this.username.setErrorMessage(ERROR_MSG_EMPTY_USERNAME);
        } else {
            this.service.createRuleEngineIfNotExists(this.username.getValue(), PACMAN_DOMAIN);
            getUI().ifPresent(ui -> ui.navigate(SimulatorView.class, this.username.getValue()));
        }
    }


}

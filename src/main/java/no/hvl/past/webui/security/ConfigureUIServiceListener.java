package no.hvl.past.webui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import no.hvl.past.webui.frontend.quickRuleEngine.EntryView;
import no.hvl.past.webui.frontend.quickRuleEngine.RuleEditorView;
import no.hvl.past.webui.frontend.quickRuleEngine.SimulatorView;
import no.hvl.past.webui.frontend.users.LoginView;
import no.hvl.past.webui.frontend.users.RegistrationForm;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
        //    ui.addBeforeEnterListener(this::authenticateNavigation);
        });
    }

    private void authenticateNavigation(BeforeEnterEvent event) {
        if (!LoginView.class.equals(event.getNavigationTarget()) &&
                        !SecurityUtils.isUserLoggedIn() &&
                        !RegistrationForm.class.equals(event.getNavigationTarget()) && // TODO the following lines for FGP demo purposes
                        !SimulatorView.class.equals(event.getNavigationTarget()) &&
                        !RuleEditorView.class.equals(event.getNavigationTarget()) &&
                        !EntryView.class.equals(event.getNavigationTarget())) {
            event.rerouteTo(LoginView.class);
        }
    }
}

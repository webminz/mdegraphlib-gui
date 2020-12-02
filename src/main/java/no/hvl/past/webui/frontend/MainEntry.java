package no.hvl.past.webui.frontend;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import no.hvl.past.webui.security.SecurityUtils;
import no.hvl.past.webui.transfer.api.RepoService;
import no.hvl.past.webui.transfer.api.UserService;
import no.hvl.past.webui.transfer.entities.RepoItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

@Route("")
public class MainEntry extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    UserService userService;
    @Autowired
    RepoService repoService;


    public MainEntry() {
        add("Pleas wait ...");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String cred = SecurityUtils.getUsername();
        if (cred != null) {
            String workingDir = this.repoService.getWorkingDir(this.userService.userObjectForName(cred));
            beforeEnterEvent.forwardTo("browse", Collections.singletonList(workingDir));
        } else {
            beforeEnterEvent.rerouteTo("login");
        }
    }
}

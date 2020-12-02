package no.hvl.past.webui.frontend;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.RouterLink;
import no.hvl.past.webui.frontend.browser.RepoBrowserView;
import no.hvl.past.webui.frontend.editors.GraphicalEditorView;
import no.hvl.past.webui.frontend.settings.ConfigurationView;
import no.hvl.past.webui.frontend.users.AvatarComponent;
import no.hvl.past.webui.security.SecurityUtils;
import no.hvl.past.webui.transfer.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;


@CssImport("./styles/shared-styles.css")
@CssImport("./styles/fontawesome/all.css")
public class MainLayout extends AppLayout {

    UserService userService;

    public MainLayout(@Autowired UserService userService) {
        this.userService = userService;
        createHeader();
        createDrawer();
    }


    private void closeDrawer() {
        setDrawerOpened(false);
    }

    private void openDrawer() {
        setDrawerOpened(true);
    }


    private void createDrawer() {
        Div repoBrowser = new Div();
        repoBrowser.addClassName("main-menu-item");
        repoBrowser.add(new Icon(VaadinIcon.FILE_TREE));
        repoBrowser.add(new RouterLink("Repository", RepoBrowserView.class));

        Div graphicalEditor = new Div();
        graphicalEditor.addClassName("main-menu-item");
        graphicalEditor.add(new Icon(VaadinIcon.EDIT));
        RouterLink editorLink = new RouterLink("Editor", GraphicalEditorView.class);
        editorLink.setHighlightAction((route,highlight) -> {
            if (highlight) {
                closeDrawer();
            } else {
                openDrawer();
            }
        });
        graphicalEditor.add(editorLink);

        Div config = new Div();
        config.addClassName("main-menu-item");
        config.add(new Icon(VaadinIcon.COGS));
        config.add(new RouterLink("Settings", ConfigurationView.class));


        VerticalLayout container = new VerticalLayout();
        container.addClassName("main-menu");
        container.add(repoBrowser, graphicalEditor, config);
        container.setSizeFull();

        addToDrawer(container);
    }

    private void createHeader() {
        H1 logo = new H1("");
        logo.addClassName("logo");

        AvatarComponent avatar = new AvatarComponent();
        avatar.updateUser(this.userService.userObjectForName(SecurityUtils.getUsername()));

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, avatar);
        header.addClassName("header");
        header.expand(logo);
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }


}

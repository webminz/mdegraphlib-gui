package no.hvl.past.webui.frontend.users;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import no.hvl.past.webui.transfer.entities.User;

import java.util.Random;

public class AvatarComponent extends Div {

    private static String[] RANDOM_COLORS = new String[]{
            "#7e1e9c",
            "#15b01a",
            "#ff81c0",
            "#653700",
            "#e500000",
            "#95d0fc",
            "#f97306",
            "#c20078",
            "#ffff14"
    };

    private User user;
    private ContextMenu userDialogWindow;

    public AvatarComponent() {
        add(new Span("?"));
        addClassName("avatar");
        setVisible(false);
        this.userDialogWindow = new ContextMenu(this);
        this.userDialogWindow.setOpenOnClick(true);

    }

    public void updateUser(User user) {
        removeAll();
        setVisible(true);
        String randomColor = RANDOM_COLORS[new Random().nextInt(RANDOM_COLORS.length)];
        getStyle().set("background-color", randomColor);
        String shortcut = calculateUserNameShortcut(user);
        if (user.getAvatarUrl() != null) {
            Image image = new Image(user.getAvatarUrl(), shortcut);
            add(image);
        } else {
            Span initals = new Span(shortcut);
            add(initals);
        }
        this.userDialogWindow.removeAll();
        this.userDialogWindow.add(contextMenuContent(user, randomColor));
    }

    private Component contextMenuContent(User user, String backgroundColor) {
        VerticalLayout container = new VerticalLayout();
        container.addClassName("avatar-menu-container");
        container.setSpacing(false);
        container.setPadding(false);
        Div usernameDiv = new Div();
        usernameDiv.addClassName("user-avatar");
        if (user.getDisplayName() != null) {
            Span primary = new Span(user.getDisplayName());
            primary.addClassName("avatar-user-name-primary");
            Span secondary = new Span("(" + user.getUsername() + ")");
            secondary.addClassName("avatar-user-name-secondary");
            usernameDiv.add(primary);
            usernameDiv.add(secondary);
        } else {
            Span primary = new Span(user.getUsername());
            primary.addClassName("avatar-user-name-primary");
            usernameDiv.add(primary);
        }
        if (user.getAvatarUrl() != null) {
            usernameDiv.getStyle().set("background-image", "url(" + user.getAvatarUrl() + ")");
        } else {
            usernameDiv.getStyle().set("background-color", backgroundColor);
        }
        Anchor changePW = new Anchor("config", "Change Password"); // TODO adjust URL
        Anchor changeAvatar = new Anchor("config", "Change Avatar"); // TODO adjust URL
        Anchor logout = new Anchor("logout", "Log out");
        container.add(usernameDiv);
        container.add(changePW);
        container.add(changeAvatar);
        container.add(logout);

        return container;
    }




    private String calculateUserNameShortcut(User user) {
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            if (!user.getDisplayName().contains(" ")) {
                return user.getDisplayName().toUpperCase().substring(0,1);
            }
            String[] s = user.getDisplayName().split(" ");
            char c1 = s[0].toUpperCase().charAt(0);
            char c2 = s[s.length - 1].toUpperCase().charAt(0);
            return "" + c1 + c2;
        } else {
            return user.getUsername().toUpperCase().substring(0,1);
        }
    }


}

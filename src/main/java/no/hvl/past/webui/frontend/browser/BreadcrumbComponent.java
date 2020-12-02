package no.hvl.past.webui.frontend.browser;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import no.hvl.past.webui.transfer.entities.User;

public class BreadcrumbComponent extends Div {

    private User user;
    private String currentPath;
    private OrderedList listComponent;

    public BreadcrumbComponent(User currentUser) {
        this.user = currentUser;
        addClassName("breadcrumb");
        setWidth("100%");
        listComponent = new OrderedList();
        add(listComponent);
    }

    public void setCurrentPath(String path) {
        this.currentPath = path;
        this.listComponent.removeAll();

        if (this.currentPath == null) {
            ListItem li = new ListItem("404");
            li.addClassName("path-not-found");
            listComponent.add(li);
        } else {
            String[] split = this.currentPath.split("/");
            if (split.length == 0) {
                this.listComponent.add(new Span(this.currentPath));
            } else if (split.length == 1) {
                this.listComponent.add(new Span(split[0]));
            } else if (split[0].equals("root") && split[1].equals(user.getUsername())) {
                if (split.length == 2) {
                    listComponent.add(new ListItem(new Icon(VaadinIcon.HOME)));
                } else {
                    ListItem li = new ListItem();
                    StringBuilder pathBuilder = new StringBuilder();
                    pathBuilder.append("browse/");
                    pathBuilder.append(split[0]);
                    pathBuilder.append('/');
                    pathBuilder.append(split[1]);
                    Anchor homeAnchor = new Anchor(pathBuilder.toString(), new Icon(VaadinIcon.HOME));
                    Span span = new Span("/");
                    li.add(homeAnchor);
                    li.add(span);
                    listComponent.add(li);
                    for (int i = 2; i < split.length; i++) {
                        pathBuilder.append('/');
                        pathBuilder.append(split[i]);
                        if (i + 1 == split.length) {
                            listComponent.add(new ListItem(new Span(split[i])));
                        } else {
                            li = new ListItem();
                            li.add(new Anchor(pathBuilder.toString(), split[i]));
                            li.add(new Span("/"));
                            listComponent.add(li);
                        }
                    }

                }
            } else {
                StringBuilder pathBuilder = new StringBuilder();
                pathBuilder.append("browse");
                for (int i = 0; i < split.length; i++) {
                    pathBuilder.append('/');
                    pathBuilder.append(split[i]);
                    if (i + 1 == split.length) {
                        listComponent.add(new ListItem(new Span(split[i])));
                    } else {
                        ListItem li = new ListItem();
                        li.add(new Anchor("browse", split[i]));
                        li.add(new Span("/"));
                        listComponent.add(li);
                    }
                }
            }
        }

    }
}

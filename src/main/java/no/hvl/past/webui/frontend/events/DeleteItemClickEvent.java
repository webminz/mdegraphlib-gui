package no.hvl.past.webui.frontend.events;

import no.hvl.past.webui.frontend.browser.BrowserWindow;
import no.hvl.past.webui.transfer.entities.RepoItem;

import java.util.Collection;
import java.util.Collections;

public class DeleteItemClickEvent extends RepoEvent {

    private final Collection<RepoItem> items;

    public DeleteItemClickEvent(BrowserWindow source,Collection<RepoItem> items) {
        super(source);
        this.items = items;
    }

    public Collection<RepoItem> getItems() {
        return items;
    }
}

package no.hvl.past.webui.frontend.events;

import no.hvl.past.webui.frontend.browser.BrowserWindow;
import no.hvl.past.webui.transfer.entities.RepoItem;

public class RenameItemClickEvent extends RepoEvent {

    private final RepoItem item;

    public RenameItemClickEvent(BrowserWindow source, RepoItem item) {
        super(source);
        this.item = item;
    }

    public RepoItem getItem() {
        return item;
    }
}

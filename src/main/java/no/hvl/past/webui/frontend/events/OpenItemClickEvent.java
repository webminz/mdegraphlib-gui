package no.hvl.past.webui.frontend.events;

import no.hvl.past.webui.frontend.browser.BrowserWindow;
import no.hvl.past.webui.transfer.entities.RepoItem;

public class OpenItemClickEvent extends RepoEvent  {

    private final RepoItem item;

    public OpenItemClickEvent(BrowserWindow source, RepoItem item) {
        super(source);
        this.item = item;
    }

    public RepoItem getItem() {
        return item;
    }
}

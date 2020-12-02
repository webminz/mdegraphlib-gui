package no.hvl.past.webui.frontend.events;

import no.hvl.past.webui.frontend.browser.BrowserWindow;
import no.hvl.past.webui.transfer.entities.Folder;

public class RepoEvent extends UIEvent<BrowserWindow> {


    public RepoEvent(BrowserWindow source) {
        super(source);
    }
}

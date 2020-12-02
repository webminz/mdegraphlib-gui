package no.hvl.past.webui.frontend.events;

import no.hvl.past.webui.frontend.browser.BrowserWindow;

public class CreateNewFolderClickEvent extends RepoEvent {

    public CreateNewFolderClickEvent(BrowserWindow source) {
        super(source);
    }
}

package no.hvl.past.webui.frontend.events;

import no.hvl.past.webui.frontend.browser.BrowserWindow;

public class CreateNewModelClickEvent extends RepoEvent {
    public CreateNewModelClickEvent(BrowserWindow source) {
        super(source);
    }
}

package no.hvl.past.webui.frontend.events;

import no.hvl.past.webui.frontend.browser.BrowserWindow;
import no.hvl.past.webui.transfer.entities.Folder;
import no.hvl.past.webui.transfer.entities.RepoItem;

import java.util.List;

public class MovedFilesByDraggingEvent extends RepoEvent {

    private final List<RepoItem> moved;
    private final RepoItem target;

    public MovedFilesByDraggingEvent(BrowserWindow source, List<RepoItem> moved, RepoItem target) {
        super(source);
        this.moved = moved;
        this.target = target;
    }

    public List<RepoItem> getMoved() {
        return moved;
    }

    public RepoItem getTarget() {
        return target;
    }
}

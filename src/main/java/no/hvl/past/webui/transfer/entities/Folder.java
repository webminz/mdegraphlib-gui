package no.hvl.past.webui.transfer.entities;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class Folder extends RepoItem {

    private List<RepoItem> children;

    public Folder() {
    }

    public Folder(String name, Folder parent, User owner, LocalDateTime createdAt, LocalDateTime lastModifiedAt, Boolean shared, List<RepoItem> children) {
        super(name, parent, owner, createdAt, lastModifiedAt, shared);
        this.children = children;
    }

    public List<RepoItem> getChildren() {
        return children;
    }

    public void setChildren(List<RepoItem> children) {
        this.children = children;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public RepoItem navigate(String path) {
        String remaining = path;
        if (!remaining.isEmpty() && remaining.charAt(0) == '/') {
            remaining = remaining.substring(1);
        }
        if (remaining.startsWith(getName())) {
            remaining = remaining.substring(getName().length());
        }
        if (remaining.isEmpty() || path.equals(".")) {
            return this;
        }
        if (remaining.charAt(0) == '/') {
            remaining = remaining.substring(1);
        }
        int splitIdx = remaining.indexOf('/');
        splitIdx = splitIdx < 0 ? remaining.length() : splitIdx;
        String fragment = remaining.substring(0, splitIdx);
        for (RepoItem repoItem : children) {
            if (repoItem.getName().equals(fragment)) {
                return repoItem.navigate(remaining.substring(splitIdx));
            }
        }
        if (remaining.isEmpty()) {
            return this;
        } else {
            return null;
        }
    }
}

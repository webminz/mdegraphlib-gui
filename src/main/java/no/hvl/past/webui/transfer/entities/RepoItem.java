package no.hvl.past.webui.transfer.entities;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public abstract class RepoItem {

    @NotNull
    @NotEmpty
    private String name;

    private Folder parent;

    @NotNull
    private User owner;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime lastModifiedAt;

    private Boolean shared;

    public RepoItem() {
    }

    public RepoItem(String name, Folder parent, User owner, LocalDateTime createdAt, LocalDateTime lastModifiedAt, Boolean shared) {
        this.name = name;
        this.parent = parent;
        this.owner = owner;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.shared = shared;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public abstract boolean isFolder();

    public String getFullPath() {
        if (parent == null) {
            return name;
        } else {
            return parent.getFullPath() + "/" + name;
        }
    }

    public abstract RepoItem navigate(String path);
}

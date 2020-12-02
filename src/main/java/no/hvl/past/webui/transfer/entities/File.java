package no.hvl.past.webui.transfer.entities;

import no.hvl.past.webui.frontend.MainLayout;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class File extends RepoItem {

    public enum FileType {
        MODEL, CODE, PDF, IMAGE_GIF, IMAGE_JPG, IMAGE_PNG, WORD_DOC, BINARY
    }

    @NotNull
    private FileType type;

    private String artifactId;

    public File() {
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public RepoItem navigate(String path) {
        String remaining = path;
        if (!remaining.isEmpty() && remaining.charAt(0) == '/') {
            remaining = remaining.substring(1);
        }
        if (remaining.isEmpty() || remaining.equals(".") || remaining.equals(getName())) {
            return this;
        }
        return null;
    }

    public File(String name, Folder parent, User owner, LocalDateTime createdAt, LocalDateTime lastModifiedAt, Boolean shared, @NotNull FileType type, String artifactId) {
        super(name, parent, owner, createdAt, lastModifiedAt, shared);
        this.type = type;
        this.artifactId = artifactId;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public boolean isModel() {
        return getType().equals(FileType.CODE) || getType().equals(FileType.MODEL);
    }

    public String produceDownloadPath() {
        switch (this.type) {
            case CODE:
                return "textEditor/" + artifactId;
            case MODEL:
                return "editor/" + artifactId;
            case IMAGE_PNG:
                return "files/png/" + artifactId + "/" + getName();
            case IMAGE_JPG:
                return "files/jpg/" + artifactId + "/" + getName();
            case IMAGE_GIF:
                return "files/gif/" + artifactId + "/" + getName();
            case PDF:
                return "files/pdf/" + artifactId + "/" + getName();
            case WORD_DOC:
            case BINARY:
            default:
                return "files/binary/" + artifactId + "/" + getName();
        }

    }
}

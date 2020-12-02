package no.hvl.past.webui.transfer.api;

import no.hvl.past.webui.transfer.entities.File;
import no.hvl.past.webui.transfer.entities.Folder;
import no.hvl.past.webui.transfer.entities.RepoItem;
import no.hvl.past.webui.transfer.entities.User;

import java.time.LocalDateTime;
import java.util.List;

public interface RepoService {

    /**
     * Provides the current working directory for the given user.
     * If the user has logged in before it is the directory where this user has been last,
     * otherwise it is the users home directory.
     */
    String getWorkingDir(User user);

    /**
     * Opens the given folder as a directory and retrieves its contents.
     */
    List<RepoItem> getDirectoryContents(Folder folder);

    RepoItem getPathContents(String path);

    void newUser(User user, LocalDateTime timestamp);

    void move(RepoItem toBeMoved, Folder newContainer, User responsible, LocalDateTime timestamp);

    void delete(RepoItem toBeDeleted, User responsible, LocalDateTime timestamp);

    void createFolder(String name, Folder container, User responsible, LocalDateTime timestamp);

    void createModel(String name, Folder container, String modelId, User responsible, LocalDateTime timestamp);

    void rename(RepoItem toBeRenamed, String value, User responsible, LocalDateTime timestamp);

    boolean recognizedFileTyped(String fileName, String mimeType);

    File addFile(User responsible, LocalDateTime timestamp, Folder parent, String fileName, String mimeType, File.FileType type);

}

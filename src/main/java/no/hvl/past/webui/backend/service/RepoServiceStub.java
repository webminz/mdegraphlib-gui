package no.hvl.past.webui.backend.service;

import no.hvl.past.webui.transfer.api.RepoService;
import no.hvl.past.webui.transfer.entities.File;
import no.hvl.past.webui.transfer.entities.Folder;
import no.hvl.past.webui.transfer.entities.RepoItem;
import no.hvl.past.webui.transfer.entities.User;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class RepoServiceStub implements RepoService {

    Folder root;
    Folder user;

    private long count = 0L;

    @PostConstruct
    private void createStub() {
        root = new Folder("root", null, UserServiceStub.SYSTEM_USER, LocalDateTime.MIN, LocalDateTime.MIN, false, new ArrayList<>());
        Folder system = new Folder("system", root, UserServiceStub.SYSTEM_USER, LocalDateTime.MIN, LocalDateTime.MIN, true, new ArrayList<>());
        user = new Folder("past@hvl.no", root, UserServiceStub.PATRICK, LocalDateTime.of(2020, 8, 1, 11, 23), LocalDateTime.of(2020, 8, 1, 11, 23), false, new ArrayList<>());
        Folder projects = new Folder("Projects", user, UserServiceStub.PATRICK, LocalDateTime.of(2020, 8, 3, 7, 0), LocalDateTime.of(2020, 8, 3, 7, 0), false, new ArrayList<>());
        File f1 = new File("Ecore.emf", user, UserServiceStub.PATRICK, LocalDateTime.of(2020, 8, 1, 11, 23), LocalDateTime.of(2020, 8, 1, 11, 23), false, File.FileType.MODEL, "1.1");
        File f2 = new File("Test.g", user, UserServiceStub.PATRICK, LocalDateTime.of(2020, 8, 2, 10, 11), LocalDateTime.of(2020, 8, 2, 10, 11), false, File.FileType.MODEL, "1.2");
        File f3 = new File("Service.java", projects, UserServiceStub.PATRICK, LocalDateTime.of(2020, 8, 2, 10, 11), LocalDateTime.of(2020, 8, 2, 10, 11), false, File.FileType.BINARY, "202001");
        File f4 = new File("Requirements.docx", projects, UserServiceStub.PATRICK, LocalDateTime.of(2020, 8, 2, 10, 11), LocalDateTime.of(2020, 8, 2, 10, 11), true, File.FileType.WORD_DOC, "202002");
        root.getChildren().add(system);
        root.getChildren().add(user);
        user.getChildren().add(f1);
        user.getChildren().add(f2);
        user.getChildren().add(projects);
        projects.getChildren().add(f3);
        projects.getChildren().add(f4);
    }


    @Override
    public String getWorkingDir(User user) {
        return this.user.getFullPath();
    }

    @Override
    public List<RepoItem> getDirectoryContents(Folder folder) {
        return folder.getChildren();
    }

    @Override
    public RepoItem getPathContents(String path) {
        if (!path.startsWith("root")) {
            return null;
        }
        return root.navigate(path);
    }

    @Override
    public void newUser(User user, LocalDateTime timestamp) {
        Folder newUserHome = new Folder(user.getUsername(), root, user, timestamp, timestamp, false, new ArrayList<>());
        root.setLastModifiedAt(timestamp);
        root.getChildren().add(newUserHome);
    }

    @Override
    public void move(RepoItem toBeMoved, Folder newContainer, User responsible, LocalDateTime timestamp) {
        toBeMoved.getParent().setLastModifiedAt(timestamp);
        toBeMoved.getParent().getChildren().remove(toBeMoved);
        toBeMoved.setParent(newContainer);
        newContainer.getChildren().add(toBeMoved);
        toBeMoved.setLastModifiedAt(timestamp);
        toBeMoved.getParent().setLastModifiedAt(timestamp);

    }

    @Override
    public void delete(RepoItem toBeDeleted, User responsible, LocalDateTime timestamp) {
        toBeDeleted.getParent().getChildren().remove(toBeDeleted);
    }

    @Override
    public void createFolder(String name, Folder container, User responsible, LocalDateTime timestamp) {
        container.getChildren().add(new Folder(name, container, responsible, timestamp, timestamp, false, new ArrayList<>()));
    }

    @Override
    public void createModel(String name, Folder container, String modelId, User responsible, LocalDateTime timestamp) {
        container.getChildren().add(new File(name, container, responsible, timestamp, timestamp, false, File.FileType.MODEL, modelId));
    }

    @Override
    public void rename(RepoItem toBeRenamed, String value, User responsible, LocalDateTime timestamp) {
        toBeRenamed.setName(value);
        toBeRenamed.setLastModifiedAt(timestamp);
    }

    @Override
    public boolean recognizedFileTyped(String fileName, String mimeType) {
        File.FileType result = null;
        if (mimeType == null) {
            result = detectFileType(fileName, "");
        } else {
            result = detectFileType(fileName, mimeType);
        }
        return result != null;
    }

    @Override
    public File addFile(User responsible, LocalDateTime timestamp, Folder parent, String fileName, String mimeType, File.FileType type) {
        File.FileType fileType = type;
        if (type == null) {
            if (mimeType == null) {
                fileType = detectFileType(fileName, "");
            } else {
                fileType = detectFileType(fileName, mimeType);
            }
        }

        String artifactId;
        if (fileType.equals(File.FileType.MODEL) || fileType.equals(File.FileType.CODE)) {
            artifactId = generateModelId(responsible, parent);
        } else {
            artifactId = generateFileId(fileName, timestamp);
        }
        File result = new File(fileName, parent, responsible, timestamp, timestamp, false, fileType, artifactId);
        parent.setLastModifiedAt(timestamp);
        parent.getChildren().add(result);
        return result;
    }

    private String generateModelId(User responsible, Folder parent) {
        return "" + responsible.getId() + "." + count++; // TODO stub
    }

    private String generateFileId(String fileName, LocalDateTime timestamp) {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS_")) + Math.abs(fileName.hashCode());
    }

    private File.FileType detectFileType(String fileName, String mimeType) {
        if (mimeType.equals(MediaType.IMAGE_JPEG_VALUE) || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return File.FileType.IMAGE_JPG;
        }
        if (mimeType.equals(MediaType.IMAGE_GIF_VALUE) || fileName.endsWith(".gif")) {
            return File.FileType.IMAGE_GIF;
        }
        if (mimeType.equals(MediaType.IMAGE_PNG_VALUE) || fileName.endsWith(".png")) {
            return File.FileType.IMAGE_PNG;
        }
        if (mimeType.equals(MediaType.APPLICATION_PDF_VALUE) || fileName.endsWith(".pdf")) {
            return File.FileType.PDF;
        }
        if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".odt")) {
            return File.FileType.WORD_DOC;
        }
        if (isRecognizedGraphicalModelFormat(fileName)) {
            return File.FileType.MODEL;
        }
        if (isRecognizedTextualModelFormat(fileName)) {
            return File.FileType.CODE;
        }
        return null;
    }

    private boolean isRecognizedTextualModelFormat(String fileName) {
        return fileName.endsWith(".java");
    }

    private boolean isRecognizedGraphicalModelFormat(String fileName) {
        return fileName.endsWith(".g") || fileName.endsWith(".emf") || fileName.endsWith(".ecore"); // TODO stub
    }




}


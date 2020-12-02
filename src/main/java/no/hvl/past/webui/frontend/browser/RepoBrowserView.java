package no.hvl.past.webui.frontend.browser;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import no.hvl.past.webui.frontend.MainLayout;
import no.hvl.past.webui.frontend.dialog.CreateFolderDialog;
import no.hvl.past.webui.frontend.dialog.CreateModelDialog;
import no.hvl.past.webui.frontend.dialog.ExceptionDialog;
import no.hvl.past.webui.frontend.dialog.RenameItemDialog;
import no.hvl.past.webui.frontend.events.*;
import no.hvl.past.webui.security.SecurityUtils;
import no.hvl.past.webui.transfer.api.FileService;
import no.hvl.past.webui.transfer.api.ModelService;
import no.hvl.past.webui.transfer.api.RepoService;
import no.hvl.past.webui.transfer.api.UserService;
import no.hvl.past.webui.transfer.entities.File;
import no.hvl.past.webui.transfer.entities.Folder;
import no.hvl.past.webui.transfer.entities.RepoItem;
import no.hvl.past.webui.transfer.entities.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Route(value = "browse", layout = MainLayout.class)
@PageTitle("Browse Repository | ModelVM")
public class RepoBrowserView extends VerticalLayout implements HasUrlParameter<String> {


    private RepoService repoService;
    private UserService userService;
    private ModelService modelService;
    private FileService fileService;

    private User currentUser;
    private RepoItem currentItem;

    private BreadcrumbComponent breadcrumbComponent;
    private BrowserWindow browserWindow;


    public RepoBrowserView(@Autowired RepoService repoService, @Autowired UserService userService, @Autowired ModelService modelService, @Autowired FileService fileService) {
        this.repoService = repoService;
        this.userService = userService;
        this.modelService = modelService;
        this.fileService = fileService;

        this.currentUser = this.userService.userObjectForName(SecurityUtils.getUsername());

        this.breadcrumbComponent = new BreadcrumbComponent(this.currentUser);
        add(this.breadcrumbComponent);

        this.browserWindow = new BrowserWindow();
        add(this.browserWindow);

        this.browserWindow.addListener(MovedFilesByDraggingEvent.class, this::moveFiles);
        this.browserWindow.addListener(CreateNewFolderClickEvent.class, this::openCreateNewFolderDialog);
        this.browserWindow.addListener(CreateNewModelClickEvent.class, this::openCreateNewModelDialog);
        this.browserWindow.addListener(RenameItemClickEvent.class, this::openRenameDialog);
        this.browserWindow.addListener(DeleteItemClickEvent.class, this::deleteItems);
        this.browserWindow.addListener(OpenItemClickEvent.class, this::openItem);
        this.browserWindow.addListener(UploadFileEvent.class, this::handleUpload);
    }

    private void handleUpload(UploadFileEvent event) {
        if (this.currentItem.isFolder()) {
            if (this.repoService.recognizedFileTyped(event.getFileName(), event.getMimeType())) {
                File file = this.repoService.addFile(this.currentUser, LocalDateTime.now() ,(Folder) this.currentItem, event.getFileName(), event.getMimeType(), null);
                if (file.isModel()) {
                    this.modelService.importModel(file, event.getInputStream());
                }
                try {
                    this.fileService.safeFile(file.getArtifactId(),event.getInputStream());
                } catch (IOException e) {
                    ExceptionDialog.open("Could not safe the file " + event.getFileName(), e);
                }
            } else {
                // TODO open dialog

            }
            refreshBrowser();
        }
    }

    private void openCreateNewFolderDialog(CreateNewFolderClickEvent event) {
        if (this.currentItem.isFolder()) {
            CreateFolderDialog.openDialog(this::refreshBrowser, currentUser, (Folder) currentItem, repoService);
        }
    }

    private void openCreateNewModelDialog(CreateNewModelClickEvent event) {
        if (this.currentItem.isFolder()) {
            CreateModelDialog.opeDialog(this::refreshBrowser, currentUser, (Folder) currentItem, repoService, modelService);
        }
    }

    private void openRenameDialog(RenameItemClickEvent event) {
        RenameItemDialog.openDialog(this::refreshBrowser, currentUser, event.getItem(), repoService);
    }

    private void openItem(OpenItemClickEvent event) {
        RepoItem toBeOpened = event.getItem();
        if (toBeOpened.isFolder()) {
            UI.getCurrent().navigate(RepoBrowserView.class, toBeOpened.getFullPath());
        } else {
            File file = (File) toBeOpened;
            switch (file.getType()) {
                case MODEL:
                    UI.getCurrent().navigate("editor"); // TODO params
                    break;
                case CODE:
                    UI.getCurrent().navigate("textEditor"); // TODO make work
                    break;
                default:
                    getUI().ifPresent(ui -> ui.getPage().setLocation(file.produceDownloadPath()));
            }
        }
    }

    private void moveFiles(MovedFilesByDraggingEvent event) {
        LocalDateTime timestamp = LocalDateTime.now();
        for (RepoItem moved : event.getMoved()) {
            this.repoService.move(moved, (Folder) event.getTarget(),currentUser, timestamp);
        }
        this.refreshBrowser();
    }

    private void deleteItems(DeleteItemClickEvent event) {
        LocalDateTime timestamp = LocalDateTime.now();
        for (RepoItem toBeDeleted : event.getItems()) {
            this.repoService.delete(toBeDeleted,currentUser, timestamp);
        }
        this.refreshBrowser();
    }

    private void refreshBrowser() {
        if (this.currentItem.isFolder()) {
            this.browserWindow.setCurrentItemFolder(this.repoService.getDirectoryContents((Folder) this.currentItem));
        }
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String s) {
        if (s == null || s.isEmpty()) {
            beforeEvent.forwardTo("browse", Collections.singletonList(this.repoService.getWorkingDir(this.currentUser)));
        } else {
            this.openPath(s);
        }
    }

    private void openPath(String newPath) {
        this.currentItem = this.repoService.getPathContents(newPath);
        if (this.currentItem == null) {
            this.breadcrumbComponent.setCurrentPath(null);
            this.browserWindow.setCurrentItemNotFound();
        } else {
            this.breadcrumbComponent.setCurrentPath(newPath);
            if (this.currentItem.isFolder()) {
                this.browserWindow.setCurrentItemFolder(this.repoService.getDirectoryContents((Folder) this.currentItem));
            } else {
                File file = (File) this.currentItem;
                File.FileType type = file.getType();
                switch (type) {
                    case MODEL:
                        this.browserWindow.setCurrentItemModel(file);
                        break;
                    case BINARY:
                        this.browserWindow.setCurrentItemBinary(file);
                        break;
                    case CODE:
                        this.browserWindow.setCurrentItemSource(file);
                        break;
                }
            }
        }
    }


}

package no.hvl.past.webui.frontend.browser;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.shared.Registration;
import no.hvl.past.webui.frontend.events.*;
import no.hvl.past.webui.transfer.entities.File;
import no.hvl.past.webui.transfer.entities.RepoItem;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BrowserWindow extends VerticalLayout {

    private Grid<RepoItem> folderContents;
    private VerticalLayout contentActionsPane;
    private Component bottomActionsPane;
    private List<RepoItem> dragged;
    private List<RepoItem> selected;


    public BrowserWindow() {
        this.dragged = Collections.emptyList();
        this.selected = Collections.emptyList();

        this.contentActionsPane = createContentActions();
        this.add(contentActionsPane);
        this.folderContents = createFolderItems();
        this.add(folderContents);
        this.bottomActionsPane = createBottomBar();
        this.add(bottomActionsPane);

        this.contentActionsPane.setVisible(true);
        this.folderContents.setVisible(false);
        this.bottomActionsPane.setVisible(false);
    }

    private VerticalLayout createContentActions() {
        VerticalLayout component = new VerticalLayout();
        component.setWidthFull();
        component.addClassName("file-content-actions");

        return component;
    }

    private HorizontalLayout createBottomBar() {
        HorizontalLayout container = new HorizontalLayout();
        container.addClassName("create-panel");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            getEventBus().fireEvent(new UploadFileEvent(this, event.getFileName(), event.getMIMEType(), buffer.getInputStream()));
        });

        container.add(upload);

        container.add(new Span("or"));

        HorizontalLayout btns = new HorizontalLayout();
        btns.addClassName("create-panel-btns");

        Button createFolder = new Button("Create Folder", new Icon(VaadinIcon.FOLDER_ADD));
        createFolder.addClickListener(click -> {
            getEventBus().fireEvent(new CreateNewFolderClickEvent(BrowserWindow.this));
        });

        Button createFile = new Button("Create Model", new Icon(VaadinIcon.FILE_ADD));
        createFile.addClickListener(click -> {
            getEventBus().fireEvent(new CreateNewModelClickEvent(BrowserWindow.this));
        });

        btns.add(createFolder, createFile);

        container.add(btns);

        return container;
    }

    private Grid<RepoItem> createFolderItems() {
        Grid<RepoItem> folderContentBrowser = new Grid<>(RepoItem.class);
        folderContentBrowser.setWidthFull();
        folderContentBrowser.addClassName("folder-contents");
        folderContentBrowser.removeAllColumns();
        Grid.Column<RepoItem> typeColumn = folderContentBrowser.addComponentColumn(repoItem -> {
            if (repoItem.isFolder()) {
                return new Icon(VaadinIcon.FOLDER);
            } else {
                File file = (File) repoItem;
                switch (file.getType()) {
                    case MODEL:
                        return new Icon(VaadinIcon.CLUSTER);
                    case CODE:
                        return new Icon(VaadinIcon.CODE);
                    case IMAGE_GIF:
                    case IMAGE_JPG:
                    case IMAGE_PNG:
                        return new Icon(VaadinIcon.FILE_PICTURE);
                    case PDF:
                    case WORD_DOC:
                        return new Icon(VaadinIcon.FILE_TEXT_O);
                    case BINARY:
                    default:
                        return new Icon(VaadinIcon.FILE_O);
                }
            }
        }).setKey("type").setWidth("40px").setFlexGrow(0);
        Grid.Column<RepoItem> nameColumn = folderContentBrowser.addColumn("name").setFlexGrow(1).setResizable(true);
        Grid.Column<RepoItem> createdAtColumn = folderContentBrowser.addColumn("createdAt").setAutoWidth(true);
        Grid.Column<RepoItem> lastModifiedAtColumn = folderContentBrowser.addColumn("lastModifiedAt").setAutoWidth(true);


      //  FooterRow footerRow = folderContentBrowser.appendFooterRow();
      //  FooterRow.FooterCell bottomCell = footerRow.join(typeColumn, nameColumn, createdAtColumn, lastModifiedAtColumn);



        GridContextMenu<RepoItem> contextMenu = new GridContextMenu<>(folderContentBrowser);
        GridMenuItem<RepoItem> newFolderItem = contextMenu.addItem("New Folder", event -> {
            getEventBus().fireEvent(new CreateNewFolderClickEvent(BrowserWindow.this));
        });
        GridMenuItem<RepoItem> createModelItem = contextMenu.addItem("New Model", event -> {
            getEventBus().fireEvent(new CreateNewModelClickEvent(BrowserWindow.this));
        });
        GridMenuItem<RepoItem> openItem = contextMenu.addItem("Open", event -> {
            getEventBus().fireEvent(new OpenItemClickEvent(BrowserWindow.this, this.selected.get(0)));
        });
        GridMenuItem<RepoItem> renameItem = contextMenu.addItem("Rename", event -> {
            getEventBus().fireEvent(new RenameItemClickEvent(BrowserWindow.this, this.selected.get(0)));
        });
        GridMenuItem<RepoItem> deleteItem = contextMenu.addItem("Delete", event -> {
            getEventBus().fireEvent(new DeleteItemClickEvent(BrowserWindow.this, this.selected));
        });


        contextMenu.addGridContextMenuOpenedListener(event -> {
            if (event.getItem().isPresent()) {
                this.selected = Collections.singletonList(event.getItem().get());
                openItem.setVisible(true);
                renameItem.setVisible(true);
                deleteItem.setVisible(true);
            } else if (!this.selected.isEmpty()) {
                deleteItem.setVisible(true);
                if (this.selected.size() == 1) {
                    openItem.setVisible(true);
                    renameItem.setVisible(true);
                } else {
                    openItem.setVisible(false);
                    renameItem.setVisible(false);
                }
            } else {
                openItem.setVisible(false);
                renameItem.setVisible(false);
                deleteItem.setVisible(false);
            }
        });

        folderContentBrowser.setRowsDraggable(true);
        folderContentBrowser.setDropMode(GridDropMode.ON_TOP);
        folderContentBrowser.setSelectionMode(Grid.SelectionMode.MULTI);
        folderContentBrowser.addDragStartListener(event -> {
            dragged = event.getDraggedItems();
        });
        folderContentBrowser.addDragEndListener(event -> {
            dragged = Collections.emptyList();
        });
        folderContentBrowser.addDropListener(event -> {
            Optional<RepoItem> target = event.getDropTargetItem();
            if (target.isPresent()) {
                getEventBus().fireEvent(new MovedFilesByDraggingEvent(BrowserWindow.this, dragged, target.get()));
            }
        });

      //  folderContentBrowser.asMultiSelect().addValueChangeListener(event -> {
       //     this.selected = new ArrayList<>(event.getValue());
      //      if (selected.size() == 0) {
     //           bottomCell.setText("No items selected");
     //       } else if (selected.size() == 1) {
      //          bottomCell.setText(this.selected.get(0).getName() + " selected");
    //        } else {
    //            bottomCell.setText(Integer.toString(this.selected.size()) + " items selected");
    //        }
   //     });


        return folderContentBrowser;
    }

    public void setCurrentItemSource(File file) {
        this.contentActionsPane.setVisible(true);
        this.folderContents.setVisible(false);
        this.bottomActionsPane.setVisible(false);

        this.contentActionsPane.removeAll();
        this.contentActionsPane.add(new Paragraph("You are watching the textual model " + file.getName() + " located at " + file.getFullPath() + " with model id" + file.getArtifactId()));
        this.contentActionsPane.add(new Button("Open Text Editor", new Icon(VaadinIcon.EDIT), click -> {
            getEventBus().fireEvent(new OpenItemClickEvent(BrowserWindow.this, file));
        }));
    }

    public void setCurrentItemBinary(File file) {

        this.contentActionsPane.setVisible(true);
        this.folderContents.setVisible(false);
        this.bottomActionsPane.setVisible(false);

        this.contentActionsPane.removeAll();
        this.contentActionsPane.add(new Paragraph("You are watching the file " + file.getName() + " located at " + file.getFullPath()));
        this.contentActionsPane.add(new Button("Download", new Icon(VaadinIcon.DOWNLOAD), click -> {
            getEventBus().fireEvent(new OpenItemClickEvent(BrowserWindow.this, file));
        }));
    }

    public void setCurrentItemModel(File file) {
        this.contentActionsPane.setVisible(true);
        this.folderContents.setVisible(false);
        this.bottomActionsPane.setVisible(false);


        this.contentActionsPane.removeAll();
        this.contentActionsPane.add(new Paragraph("You are watching the graphical model " + file.getName() + " located at " + file.getFullPath() + " with model id " + file.getArtifactId() ));
        this.contentActionsPane.add(new Button("Open Editor", new Icon(VaadinIcon.EDIT), click -> {
            getEventBus().fireEvent(new OpenItemClickEvent(BrowserWindow.this, file));
        }));
    }

    public void setCurrentItemFolder(List<RepoItem> children) {

        this.contentActionsPane.setVisible(false);
        this.folderContents.setVisible(true);
        this.bottomActionsPane.setVisible(true);

        this.folderContents.setItems(children);
    }

    public void setCurrentItemNotFound() {
        this.contentActionsPane.removeAll();
        this.contentActionsPane.add(new Paragraph("There is nothing to be found on this location"));
    }

    @Override
    protected <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

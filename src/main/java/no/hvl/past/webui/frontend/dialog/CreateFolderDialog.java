package no.hvl.past.webui.frontend.dialog;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import no.hvl.past.webui.transfer.api.RepoService;
import no.hvl.past.webui.transfer.entities.Folder;
import no.hvl.past.webui.transfer.entities.User;

import java.time.LocalDateTime;

public class CreateFolderDialog extends ModalDialog {

    private TextField nameField;
    private User currentUser;
    private Folder parent;
    private RepoService service;


    public CreateFolderDialog(DialogCallback callback, User currentUser, Folder parent, RepoService service) {
        super(callback, "Create New Folder", "Create");
        nameField = new TextField("Name", "New Folder Name");
        getForm().add(nameField);
        this.currentUser = currentUser;
        this.parent = parent;
        this.service = service;
    }


    @Override
    protected boolean validate(FormLayout form) {
        if (!nameField.getValue().isEmpty()) {
            return true;
        } else {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Folder name must not be empty");
            return false;
        }
    }

    @Override
    protected void executeAction() {
        this.service.createFolder(nameField.getValue(), parent, currentUser, LocalDateTime.now());
    }

    public static void openDialog(DialogCallback callback, User currentUser, Folder parent, RepoService service) {
        Dialog dialog = new CreateFolderDialog(callback, currentUser, parent, service);
        dialog.open();
    }

}

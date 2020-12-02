package no.hvl.past.webui.frontend.dialog;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import no.hvl.past.webui.transfer.api.RepoService;
import no.hvl.past.webui.transfer.entities.RepoItem;
import no.hvl.past.webui.transfer.entities.User;

import java.time.LocalDateTime;

public class RenameItemDialog extends ModalDialog {

    private final User currentUser;
    private final RepoItem toBeRenamed;
    private final RepoService service;
    private TextField nameField;

    public RenameItemDialog(DialogCallback callback, User currentUser, RepoItem toBeRenamed, RepoService service) {
        super(callback,"Rename item", "Rename");
        this.currentUser = currentUser;
        this.toBeRenamed = toBeRenamed;
        this.service = service;
        nameField = new TextField("New Name", "New item name", toBeRenamed.getName());
        getForm().add(nameField);
    }


    @Override
    protected boolean validate(FormLayout form) {
        if (!nameField.getValue().isEmpty()) {
            return true;
        } else {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Item name must not be empty");
            return false;
        }
    }

    @Override
    protected void executeAction() {
        this.service.rename(toBeRenamed, nameField.getValue(), currentUser, LocalDateTime.now());
    }

    public static void openDialog(DialogCallback callback, User currentUser, RepoItem toBeRenamed, RepoService service) {
        RenameItemDialog dialog = new RenameItemDialog(callback, currentUser, toBeRenamed, service);
        dialog.open();
    }
}

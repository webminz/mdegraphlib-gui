package no.hvl.past.webui.frontend.dialog;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import no.hvl.past.webui.transfer.api.ModelService;
import no.hvl.past.webui.transfer.api.RepoService;
import no.hvl.past.webui.transfer.entities.Folder;
import no.hvl.past.webui.transfer.entities.Model;
import no.hvl.past.webui.transfer.entities.User;

import java.time.LocalDateTime;

public class CreateModelDialog extends ModalDialog {

    private static final String TYPE_GRAPHICAL = "graphical";
    private static final String TYPE_TEXTUAL = "textual";

    private TextField nameField;
    private RadioButtonGroup<String> typeSelection;
    private Checkbox advanced;
    private FormLayout advancedSettingsForm;
    private Select<Model> metamodelSelection;
    private User currentUser;
    private Folder parent;
    private RepoService repoService;
    private ModelService modelService;

    public CreateModelDialog(DialogCallback callback, User currentUser, Folder parent, RepoService repoService, ModelService modelService) {
        super(callback, "Create New Model", "Create");
        this.currentUser = currentUser;
        this.parent = parent;
        this.repoService = repoService;
        this.modelService = modelService;

        nameField = new TextField("Name", "New model name");
        getForm().add(nameField);

        typeSelection = new RadioButtonGroup<>();
        typeSelection.setLabel("Type");
        typeSelection.setItems(TYPE_GRAPHICAL, TYPE_TEXTUAL);
        typeSelection.setValue(TYPE_GRAPHICAL);
        getForm().add(typeSelection);

        advanced = new Checkbox("Advanced Settings");
        advanced.setValue(false);
        advanced.addValueChangeListener(event -> {
            if (event.getValue()) {
                showAdvanced();
            } else {
                hideAdvanced();
            }
        });
        getForm().add(advanced);

        this.advancedSettingsForm = new FormLayout();
        this.advancedSettingsForm.setVisible(false);
        getAdditionalDialogContent().add(advancedSettingsForm);

        metamodelSelection = new Select<>();
        metamodelSelection.setLabel("Metamodel");
        this.advancedSettingsForm.add(metamodelSelection);
    }

    private void hideAdvanced() {
        this.advancedSettingsForm.setVisible(false);
    }

    private void showAdvanced() {
        this.metamodelSelection.setItems(this.modelService.availableMetamodels());
        this.advancedSettingsForm.setVisible(true);
    }

    private boolean isGraphicalSelected() {
        return typeSelection.getValue().equals(TYPE_GRAPHICAL);
    }

    private boolean isTextualSelected() {
        return typeSelection.getValue().equals(TYPE_TEXTUAL);
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
        Model model = this.modelService.newModel(nameField.getValue(), metamodelSelection.getOptionalValue());
        this.repoService.createModel(nameField.getValue(), parent, model.getId(), currentUser, LocalDateTime.now());
    }

    public static void opeDialog(DialogCallback callback, User currentUser, Folder parent, RepoService repoService, ModelService modelService) {
        Dialog dialog = new CreateModelDialog(callback, currentUser, parent, repoService, modelService);
        dialog.open();
    }
}

package no.hvl.past.webui.frontend.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class ModalDialog extends Dialog {

    private FormLayout form;
    private VerticalLayout additionalDialogContent;

    public ModalDialog(DialogCallback callback, String headerText, String actionBtnName) {
        setModal(true);
        setDraggable(true);
        setResizable(true);

        this.form = new FormLayout();
        this.additionalDialogContent = new VerticalLayout();

        HorizontalLayout buttonContainer = new HorizontalLayout();
        Button submit = new Button(actionBtnName, click -> {
            if (this.validate(form)) {
                this.executeAction();
                this.close();
                callback.success();
            }
        });
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", click -> {
            close();
        });
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonContainer.add(submit, cancel);

        VerticalLayout dialogContainer = new VerticalLayout();
        dialogContainer.addClassName("modal-dialog-content");

        dialogContainer.add(new H1(headerText));
        dialogContainer.add(form);
        dialogContainer.add(additionalDialogContent);
        dialogContainer.add(buttonContainer);

        add(dialogContainer);
    }

    protected FormLayout getForm() {
        return form;
    }

    protected VerticalLayout getAdditionalDialogContent() {
        return additionalDialogContent;
    }

    protected abstract boolean validate(FormLayout form);

    protected abstract void executeAction();
}

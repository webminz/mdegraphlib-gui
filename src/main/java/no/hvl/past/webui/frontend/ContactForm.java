package no.hvl.past.webui.frontend;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import no.hvl.past.webui.backend.entities.Company;
import no.hvl.past.webui.backend.entities.Contact;

import java.util.List;


public class ContactForm extends FormLayout {

    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    EmailField email = new EmailField("Email");
    ComboBox<Contact.Status> status = new ComboBox<>("Status");
    ComboBox<Company> company = new ComboBox<>("Company");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

    public ContactForm(List<Company> companies) {
        addClassName("contact-form");

        binder.bindInstanceFields(this);
        status.setItems(Contact.Status.values());
        company.setItems(companies);
        company.setItemLabelGenerator(Company::getName);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(click -> fireEvent(new CloseEvent(this, binder.getBean())));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        add(
                firstName,
                lastName,
                email,
                status,
                company,
                createButtonsLayout());
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }

    }

    public void setContact(Contact contact) {
        binder.setBean(contact);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(save, delete, close);
    }

    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {

        private final Contact contact;

        public ContactFormEvent(ContactForm source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    public static class SaveEvent extends ContactFormEvent {

        public SaveEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {

        public DeleteEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }

    public static class CloseEvent extends ContactFormEvent {

        public CloseEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }

    @Override
    protected <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

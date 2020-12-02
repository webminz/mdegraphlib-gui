package no.hvl.past.webui.backend.repositories;

import no.hvl.past.webui.backend.entities.Contact;

public class ContactRepository extends AbstractRepository<Contact> {

    private static ContactRepository instance;

    private ContactRepository() {
    }

    public static ContactRepository getInstance() {
        if (instance == null) {
            instance = new ContactRepository();
        }
        return instance;
    }
}

package no.hvl.past.webui.backend.repositories;

import no.hvl.past.webui.backend.entities.Company;

public class CompanyRepository extends AbstractRepository<Company> {

    private static CompanyRepository instance;

    private CompanyRepository() {
    }

    public static CompanyRepository getInstance() {
        if (instance == null) {
            instance = new CompanyRepository();
        }
        return instance;
    }
}

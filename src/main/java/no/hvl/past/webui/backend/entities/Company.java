package no.hvl.past.webui.backend.entities;

import java.util.ArrayList;
import java.util.List;

public class Company extends AbstractEntity {

    private String name;

    private List<Contact> employees;

    public Company() {
        this.employees = new ArrayList<>();
    }

    public Company(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Contact> getEmployees() {
        return employees;
    }

}

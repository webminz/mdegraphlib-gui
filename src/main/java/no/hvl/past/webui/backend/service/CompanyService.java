package no.hvl.past.webui.backend.service;

import no.hvl.past.webui.backend.entities.Company;
import no.hvl.past.webui.backend.repositories.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyService {
    private CompanyRepository companyRepository;

    public CompanyService() {
        this.companyRepository = CompanyRepository.getInstance();
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> result = new HashMap<>();
        findAll().forEach(company -> result.put(company.getName(), company.getEmployees().size()));
        return result;
    }
}

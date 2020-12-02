package no.hvl.past.webui.backend.service;

import no.hvl.past.webui.transfer.api.ModelService;
import no.hvl.past.webui.transfer.entities.File;
import no.hvl.past.webui.transfer.entities.Model;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ModelServiceStub implements ModelService {

    private int idgenerator = 1;
    private static final Model GRAPH = new Model("1.0", "Graph", "The implicit metamodel of every graph comprising nodes and edges");
    private static final Model ECORE = new Model("1.1", "Ecore.emf", "The Ecore metamodel.");

    @Override
    public Model newModel(String name, Optional<Model> metamodel) {
        return new Model("23." + (idgenerator++), name, null);
    }

    @Override
    public List<Model> availableMetamodels() {
        return Arrays.asList(GRAPH, ECORE);
    }

    @Override
    public void importModel(File repoObject, InputStream inputStream) {

    }
}

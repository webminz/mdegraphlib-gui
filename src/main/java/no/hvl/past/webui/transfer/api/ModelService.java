package no.hvl.past.webui.transfer.api;

import no.hvl.past.webui.transfer.entities.File;
import no.hvl.past.webui.transfer.entities.Model;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface ModelService {

    Model newModel(String name, Optional<Model> metamodel);

    List<Model> availableMetamodels();

    void importModel(File repoObject, InputStream inputStream);
}

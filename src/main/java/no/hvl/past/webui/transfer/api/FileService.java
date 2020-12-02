package no.hvl.past.webui.transfer.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileService {

    String safeFile(String artifactId, InputStream is) throws IOException;

    InputStream getFile(String fileId) throws IOException;

}

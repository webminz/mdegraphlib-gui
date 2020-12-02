package no.hvl.past.webui.backend.service;

import no.hvl.past.webui.transfer.api.FileService;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class StubFileService implements FileService {

    File stubWS = new File("/Users/past/Documents/dev/modelvm/workspace");


    @Override
    public String safeFile(String artifactId, InputStream is) throws IOException {
        File file = new File(stubWS, artifactId);
        FileOutputStream fos = new FileOutputStream(file);
        int nextByte = is.read();
        while (nextByte >= 0) {
            fos.write(nextByte);
            nextByte = is.read();
        }
        fos.flush();
        fos.close();
        is.close();
        return artifactId;
    }

    @Override
    public InputStream getFile(String fileId) throws FileNotFoundException {
        File result;
        if (fileId.equals("32")) {
            result = new File(stubWS, "A_PDF.pdf");
        } else
        if (fileId.equals("202001")) {
            result = new File(stubWS, "Service.java");
        } else
        if (fileId.equals("202002")) {
            result = new File(stubWS, "Requirements.docx");
        } else {
            result = new File(stubWS, fileId);
        }
        FileInputStream fis = new FileInputStream(result);
        return fis;
    }
}

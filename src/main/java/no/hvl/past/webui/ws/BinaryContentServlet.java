package no.hvl.past.webui.ws;

import no.hvl.past.webui.transfer.api.FileService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Component
@RestController
public class BinaryContentServlet {

    @Autowired
    FileService service;

    @GetMapping(value = "/files/binary/{fileId}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getFile(@PathVariable String fileId, @PathVariable String fileName) {
        try {
            return IOUtils.toByteArray(service.getFile(fileId));
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @GetMapping(value = "/files/pdf/{fileId}/{fileName}", produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getPDF(@PathVariable String fileId, @PathVariable String fileName) {
        try {
            return IOUtils.toByteArray(service.getFile(fileId));
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @GetMapping(value = "/files/jpg/{fileId}/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getJPEG(@PathVariable String fileId, @PathVariable String fileName) {
        try {
            return IOUtils.toByteArray(service.getFile(fileId));
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    @GetMapping(value = "/files/gif/{fileId}/{fileName}", produces = MediaType.IMAGE_GIF_VALUE)
    public @ResponseBody byte[] getGIF(@PathVariable String fileId, @PathVariable String fileName) {
        try {
            return IOUtils.toByteArray(service.getFile(fileId));
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @GetMapping(value = "/files/png/{fileId}/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getPNG(@PathVariable String fileId, @PathVariable String fileName) {
        try {
            return IOUtils.toByteArray(service.getFile(fileId));
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }



}

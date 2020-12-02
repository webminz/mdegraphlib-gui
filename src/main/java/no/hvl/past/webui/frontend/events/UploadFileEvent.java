package no.hvl.past.webui.frontend.events;

import no.hvl.past.webui.frontend.browser.BrowserWindow;

import java.io.InputStream;

public class UploadFileEvent extends RepoEvent {

    private final String fileName;
    private final String mimeType;
    private final InputStream inputStream;

    public UploadFileEvent(BrowserWindow source, String fileName, String mimeType, InputStream inputStream) {
        super(source);
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}

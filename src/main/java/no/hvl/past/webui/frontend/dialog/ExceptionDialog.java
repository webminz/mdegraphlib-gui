package no.hvl.past.webui.frontend.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class ExceptionDialog extends Dialog {

    private final String message;
    private final Throwable exception;

    public ExceptionDialog(String message, Throwable exception) {
        this.message = message;
        this.exception = exception;
        setModal(true);
        setResizable(false);
        setDraggable(false);
        add(new H1("Whooops... something went wrong"));
        add(new Paragraph(message));
        TextArea textArea = new TextArea();
        textArea.setEnabled(false);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(bos));
        textArea.setValue(bos.toString());
        add(textArea);
        Button btn = new Button("Close");
        btn.addClickListener(click -> {
            close();
        });
    }

    public static void open(String message, Throwable exception) {
        Dialog dialog = new ExceptionDialog(message, exception);
        dialog.open();
    }
}

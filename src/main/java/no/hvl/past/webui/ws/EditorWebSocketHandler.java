package no.hvl.past.webui.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Random;

@Component
public class EditorWebSocketHandler extends TextWebSocketHandler {

    private int counter = 0;
    private boolean started = false;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Object editorId = session.getAttributes().getOrDefault("editorId", "");

        session.sendMessage(new TextMessage("Hi, you said : " + payload + ". I say:" + counter++ + " and you are editing " + editorId));




    }
}

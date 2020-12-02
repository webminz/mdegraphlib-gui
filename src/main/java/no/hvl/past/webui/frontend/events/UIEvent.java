package no.hvl.past.webui.frontend.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class UIEvent<S extends Component> extends ComponentEvent<S> {

    public UIEvent(S source) {
        super(source, false);
    }
}

package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Event;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldObject;
import no.hvl.past.webui.transfer.quickRuleEngine.service.WorldRenderer;

import java.util.Collection;
import java.util.Set;

public interface Domain {

    Collection<WorldObject> objects();

    Set<Event> interactions();

    WorldRenderer renderer();

    World startWorld();

    void domainSpecificRuleSetUp(RuleEngine engine);

}

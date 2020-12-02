package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.RuleHierarchy;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;

import java.util.Optional;

public abstract class AbstractRule {

    public abstract World apply(World current);

    public abstract boolean isTriggered();

    public abstract void deny();

    public abstract RuleHierarchy getHierarchy(RuleHierarchy parent);

    public abstract Optional<Rule> findRule(String rulName);

    public abstract Optional<RuleGroup> findGroup(String groupName);

    public abstract boolean hasMatch(World inWorld);
}

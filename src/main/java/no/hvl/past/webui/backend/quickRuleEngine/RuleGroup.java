package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.RuleHierarchy;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;

import java.util.*;

public class RuleGroup extends AbstractRule {




    public enum ExecutionType {
        SEQUENCE,
        PARALLEL,
        ALTERNATIVE
    }

    private String name;
    private ExecutionType execution;
    private List<AbstractRule> rules;

    public RuleGroup(String name) {
        this.name = name;
        this.execution = ExecutionType.SEQUENCE;
        this.rules = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addChild(AbstractRule rule) {
        this.rules.add(rule);
    }

    public void removeChild(Rule rule) {
        this.rules.remove(rule);
    }

    public ExecutionType getExecution() {
        return execution;
    }

    public void setExecution(ExecutionType execution) {
        this.execution = execution;
    }

    public List<AbstractRule> getRules() {
        return rules;
    }


    @Override
    public boolean isTriggered() {
        return this.rules.stream().anyMatch(AbstractRule::isTriggered);
    }

    @Override
    public void deny() {
        this.rules.forEach(AbstractRule::deny);
    }

    @Override
    public RuleHierarchy getHierarchy(RuleHierarchy parent) {
        RuleHierarchy.RuleHierarchyItemType type;
        switch (this.execution) {
            case ALTERNATIVE:
                type = RuleHierarchy.RuleHierarchyItemType.MUTEX_GROUP;
                break;
            case PARALLEL:
                type = RuleHierarchy.RuleHierarchyItemType.PARALLEL_GROUP;
                break;
            case SEQUENCE:
            default:
                type = RuleHierarchy.RuleHierarchyItemType.SEQUENTIAL_GROUP;
                break;
        }
        List<RuleHierarchy> children = new ArrayList<>();
        RuleHierarchy result = new RuleHierarchy(getName(), parent, type, children);
        for (AbstractRule r : this.rules) {
            children.add(r.getHierarchy(result));
        }
        return result;
    }

    @Override
    public Optional<Rule> findRule(String rulName) {
        for (AbstractRule r : this.rules) {
            Optional<Rule> recusriveResult = r.findRule(rulName);
            if (recusriveResult.isPresent()) {
                return recusriveResult;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<RuleGroup> findGroup(String groupName) {
        if (groupName.equals(this.name)) {
            return Optional.of(this);
        }
        for (AbstractRule r : this.rules) {
            Optional<RuleGroup> recursiveResult = r.findGroup(groupName);
            if (recursiveResult.isPresent()) {
                return recursiveResult;
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean hasMatch(World inWorld) {
        for (AbstractRule r : this.rules) {
            if (r.hasMatch(inWorld)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public World apply(World current) {
        switch (execution) {
            case SEQUENCE:
                return executeSequentially(current);
            case PARALLEL:
                return executeSequentially(current); // TODO acutally wrong, needs proper conflict handling hre
            case ALTERNATIVE:
                return executeAlternative(current);
            default:
                return current;
        }
    }


    public RuleGroup findParent(AbstractRule of) {
        if (this.rules.contains(of)) {
            return this;
        }
        for (AbstractRule r : this.rules) {
            if (r instanceof RuleGroup) {
                RuleGroup result = ((RuleGroup) r).findParent(of);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private World executeSequentially(World current) {
        World result = current;
        for (AbstractRule r : this.rules) {
            if (r.isTriggered()) {
                result = r.apply(result);
            }
        }
        return result;
    }

    private World executeAlternative(World current) {
        Set<AbstractRule> choices = new HashSet<>();
        this.rules.stream().filter(AbstractRule::isTriggered).filter(r -> r.hasMatch(current)).forEach(choices::add);
        if (choices.isEmpty()) {
            return current;
        }
        Random rnd = new Random(System.currentTimeMillis());
        int idx = rnd.nextInt(choices.size());
        Iterator<AbstractRule> it = choices.iterator();
        AbstractRule toBeExecuted = null;
        for (int i = 0; i <= idx; i++) {
            toBeExecuted = it.next();
            if (i != idx) {
                toBeExecuted.deny();
            }
        }
        while (it.hasNext()) {
            it.next().deny();
        }
        return toBeExecuted.apply(current);
    }

}

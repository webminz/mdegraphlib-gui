package no.hvl.past.webui.transfer.quickRuleEngine.domain;

import java.util.List;

public class RuleHierarchy {

    public enum RuleHierarchyItemType {
        RULE,
        SEQUENTIAL_GROUP,
        MUTEX_GROUP,
        PARALLEL_GROUP
    }

    private String name;
    private RuleHierarchy parent;
    private RuleHierarchyItemType type;
    private List<RuleHierarchy> children;

    public RuleHierarchy(String name, RuleHierarchy parent, RuleHierarchyItemType type, List<RuleHierarchy> children) {
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.children = children;
    }

    public boolean isRoot() {
        return this.parent.getParent() == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleHierarchy getParent() {
        return parent;
    }

    public void setParent(RuleHierarchy parent) {
        this.parent = parent;
    }

    public RuleHierarchyItemType getType() {
        return type;
    }

    public void setType(RuleHierarchyItemType type) {
        this.type = type;
    }

    public List<RuleHierarchy> getChildren() {
        return children;
    }

    public void setChildren(List<RuleHierarchy> children) {
        this.children = children;
    }
}

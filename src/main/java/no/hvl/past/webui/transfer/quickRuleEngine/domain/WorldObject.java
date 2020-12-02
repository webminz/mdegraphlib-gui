package no.hvl.past.webui.transfer.quickRuleEngine.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class WorldObject extends Thing implements Cloneable {

    private boolean isSingleton;
    private Collection<WorldObject> children;
    private Collection<WorldRelation> relations;
    private Integer xPos;
    private Integer yPos;


    public WorldObject(String name,
                       boolean isSingleton,
                       Collection<WorldObject> children,
                       Collection<WorldRelation> relations,
                       Integer xPos,
                       Integer yPos) {
        super(name);
        this.isSingleton = isSingleton;
        this.children = children;
        this.relations = relations;
        this.xPos = xPos;
        this.yPos = yPos;
    }


    public WorldObject(String name, boolean isSingleton) {
        this(name, isSingleton, new ArrayList<>(), new ArrayList<>(), null, null);
    }

    public Collection<Thing> navigateRelation(String relationName, String targetRoleName) {
        return this.relations.stream().filter(r -> r.getName().equals(relationName)).map(r -> r.getThingInRelation(targetRoleName)).collect(Collectors.toList());
    }

    public Collection<Thing> navigateRelation(String relationName) {
        return this.relations.stream()
                .filter(r -> r.getName().equals(relationName))
                .flatMap(r -> r.getRelates().values().stream().filter(v -> !v.equals(this)))
                .collect(Collectors.toList());
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    public Collection<WorldObject> getChildren() {
        return children;
    }

    public void setChildren(Collection<WorldObject> children) {
        this.children = children;
    }

    public Collection<WorldRelation> getRelations() {
        return relations;
    }

    public void setRelations(Collection<WorldRelation> relations) {
        this.relations = relations;
    }

    public Integer getxPos() {
        return xPos;
    }

    public void setxPos(Integer xPos) {
        this.xPos = xPos;
    }

    public Integer getyPos() {
        return yPos;
    }

    public void setyPos(Integer yPos) {
        this.yPos = yPos;
    }

    @Override
    public boolean isMatchableWith(Thing other) {
        if (other instanceof WorldObject) {
            WorldObject otherAsWO = (WorldObject) other;
            return getClass().isAssignableFrom(otherAsWO.getClass()); // TODO depends also on attributes, children and relations
        }
        return false;
    }

    public int currentXPos() {
        Collection<Thing> locations = this.navigateRelation(WorldRelation.LOCATED_ON, "place");
        for (Thing t : locations) {
            if (t instanceof WorldObject) {
                if (((WorldObject) t).getxPos() != null) {
                    return ((WorldObject) t).getxPos();
                }
            }
        }
        return -1;
    }

    public int currentYPos() {
        Collection<Thing> locations = this.navigateRelation(WorldRelation.LOCATED_ON, "place");
        for (Thing t : locations) {
            if (t instanceof WorldObject) {
                if (((WorldObject) t).getyPos() != null) {
                    return ((WorldObject) t).getyPos();
                }
            }
        }
        return -1;
    }

    public WorldObject instantiate() {
        if (isSingleton) {
            return this;
        } else {
            try {
                WorldObject clone = (WorldObject) this.clone();
                clone.relations = new ArrayList<>();
                clone.children = new ArrayList<>();
                clone.xPos = null;
                clone.yPos = null;
                return clone;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

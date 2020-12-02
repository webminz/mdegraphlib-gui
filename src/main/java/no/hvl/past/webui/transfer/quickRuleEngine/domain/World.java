package no.hvl.past.webui.transfer.quickRuleEngine.domain;


import java.util.*;
import java.util.stream.Collectors;

public class World {

    public static class Builder {

        private Collection<Thing> things = new ArrayList<>();

        public WorldObject object(WorldObject object, int x, int y) {
            object.setxPos(x);
            object.setyPos(y);
            things.add(object);
            return object;
        }

        public WorldRelation locatedOn(WorldObject subject, WorldObject location) {
            if (!things.contains(subject)) {
                this.things.add(subject);
            }
            if (!things.contains(location)) {
                this.things.add(location);
            }
            WorldRelation rel = new WorldRelation.LocatedOn(location, subject);
            this.things.add(rel);
            subject.getRelations().add(rel);
            location.getRelations().add(rel);
            return rel;
        }

        public World build() {
            World world = new World(things);
            things = new ArrayList<>();
            return world;
        }

        public WorldRelation locatedAbove(WorldObject north, WorldObject south) {
            if (!this.things.contains(north)) {
                this.things.add(north);
            }
            if (!this.things.contains(south)) {
                this.things.add(south);
            }
            WorldRelation.VerticalAdjacency rel = new WorldRelation.VerticalAdjacency(north, south);
            this.things.add(rel);
            north.getRelations().add(rel);
            south.getRelations().add(rel);
            return rel;
        }

        public WorldRelation locatedBesides(WorldObject west, WorldObject east) {
            if (!this.things.contains(east)) {
                this.things.add(east);
            }
            if (!this.things.contains(west)) {
                this.things.add(west);
            }
            WorldRelation.HorizontalAdjacency rel = new WorldRelation.HorizontalAdjacency(west, east);
            this.things.add(rel);
            east.getRelations().add(rel);
            west.getRelations().add(rel);
            return rel;
        }
    }

    private Collection<Thing> things;

    public World(Collection<Thing> things) {
        this.things = things;
    }

    public Collection<Thing> getThings() {
        return things;
    }

    public void setThings(Collection<Thing> things) {
        this.things = things;
    }


    public World copy() {
        Collection<Thing> thingCollection = new ArrayList<>(things);
        return new World(thingCollection);
    }

    public Optional<WorldObject> findSingleton(WorldObject worldObject) {
        return this.things
                .stream()
                .filter(t -> t instanceof WorldObject)
                .map(t -> (WorldObject) t)
                .filter(WorldObject::isSingleton)
                .filter(w -> worldObject.getClass().isAssignableFrom(w.getClass()))
                .findFirst();
    }

    public List<WorldRelation> findRelation(String relationName, String roleName, WorldObject object) {
        return this.things
                .stream()
                .filter(t -> t instanceof WorldRelation)
                .map(t -> (WorldRelation) t)
                .filter(r -> r.getName().equals(relationName))
               // .filter(r -> r.getThingInRelation(roleName) != null) // TODO weird sanity check
                .filter(r -> r.getThingInRelation(roleName).equals(object))
                .collect(Collectors.toList());

    }
}

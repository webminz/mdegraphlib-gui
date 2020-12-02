package no.hvl.past.webui.transfer.quickRuleEngine.domain;

import no.hvl.past.webui.transfer.quickRuleEngine.service.WorldRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class WorldRelation extends Thing implements Cloneable {

    public static final String HORIZONTAL_LOCATION = "Horizontal_Location";
    public static final String VERTICAL_LOCATION = "Vertical_Location";
    public static final String LOCATED_ON = "Located_On";
    public static final String HAPPENED_AT = "Happened_At";



    public abstract static class LocationRelation extends WorldRelation {

        public LocationRelation(String name) {
            super(name, 2);
        }

        public abstract boolean isHorizontal();
        public abstract boolean isVertical();
    }

    public static class HorizontalAdjacency extends LocationRelation {

        public static final String EAST = "east";
        public static final String WEST = "west";



        public HorizontalAdjacency(WorldObject west, WorldObject east) {
            super(HORIZONTAL_LOCATION);
            getRelates().put(EAST, east);
            getRelates().put(WEST, west);
        }

        public WorldObject getWest() {
            return (WorldObject) getRelates().get(WEST);
        }

        public WorldObject getEast() {
            return (WorldObject) getRelates().get(EAST);
        }

        @Override
        public boolean isHorizontal() {
            return true;
        }

        @Override
        public boolean isVertical() {
            return false;
        }
    }

    public static class VerticalAdjacency extends LocationRelation {

        public static final String NORTH = "north";
        public static final String SOUTH = "south";


        public VerticalAdjacency(WorldObject north, WorldObject south) {
            super(VERTICAL_LOCATION);
            getRelates().put(NORTH, north);
            getRelates().put(SOUTH, south);
        }

        public WorldObject getNorth() {
            return (WorldObject) getRelates().get(NORTH);
        }

        public WorldObject getSouth() {
            return (WorldObject) getRelates().get(SOUTH);
        }

        @Override
        public boolean isHorizontal() {
            return false;
        }

        @Override
        public boolean isVertical() {
            return true;
        }
    }

    public static class LocatedOn extends WorldRelation {

        public static final String PLACE = "place";
        public static final String SUBJECT = "subject";


        public LocatedOn(WorldObject place, WorldObject subject) {
            super(LOCATED_ON, 2);
            getRelates().put(PLACE, place);
            getRelates().put(SUBJECT, subject);
        }

        public WorldObject getPlace() {
            return (WorldObject) getRelates().get(PLACE);
        }

        public WorldObject getSubject() {
            return (WorldObject) getRelates().get(SUBJECT);
        }


        public static Integer getXPosOfObjectInWorld(WorldObject object, World world) {
            return world.getThings().stream()
                    .filter(t -> t instanceof WorldRelation)
                    .filter(t -> t.getName().equals(LOCATED_ON))
                    .map(t -> (WorldRelation) t)
                    .filter(r -> r.getThingInRelation(SUBJECT).equals(object))
                    .map(r -> r.getThingInRelation(PLACE))
                    .filter(t -> t instanceof WorldObject)
                    .map(t -> (WorldObject) t)
                    .map(WorldObject::getxPos)
                    .findFirst().orElse(null);
        }

        public static Integer getYPosOfObjectInWorld(WorldObject object, World world) {
            return world.getThings().stream()
                    .filter(t -> t instanceof WorldRelation)
                    .filter(t -> t.getName().equals(LOCATED_ON))
                    .map(t -> (WorldRelation) t)
                    .filter(r -> r.getThingInRelation(SUBJECT).equals(object))
                    .map(r -> r.getThingInRelation(PLACE))
                    .filter(t -> t instanceof WorldObject)
                    .map(t -> (WorldObject) t)
                    .map(WorldObject::getyPos)
                    .findFirst().orElse(null);
        }
    }

    public static class HappenedAt extends WorldRelation {

        private static final String THING = "something";
        private static final String HAPPENED_AT = "happened_at";

        private Thing thing;
        private TemporalThing.Appointment at;

        public HappenedAt(Thing thing, TemporalThing.Appointment at) {
            super(HAPPENED_AT, 2);
            this.thing = thing;
            this.at = at;
            getRelates().put(THING, thing);
            getRelates().put(HAPPENED_AT, at);
        }

        public Thing getThing() {
            return thing;
        }

        public TemporalThing.Appointment getAt() {
            return at;
        }
    }


    private Map<String, Thing> relates;
    private int arity;

    public WorldRelation(String name, int arity) {
        super(name);
        this.arity = arity;
        this.relates = new HashMap<>();
    }

    public int getArity() {
        return arity;
    }

    public void setArity(int arity) {
        this.arity = arity;
    }


    Map<String, Thing> getRelates() {
        return relates;
    }

    public void addRelation(String roleName, WorldObject object) {
        this.relates.put(roleName, object);
        object.getRelations().add(this);
    }

    public Thing getThingInRelation(String roleName) {
        if (!getRelates().containsKey(roleName)) {
            System.out.println("Err, role not found:" + getName() + " " + getRelates().toString() + " " + roleName);
        }
        return this.getRelates().get(roleName);
    }

    public String getRole(WorldObject start) {
        return this.relates.entrySet().stream().filter(e -> e.getValue().equals(start)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public Set<String> getRoles() {
        return this.relates.keySet();
    }

    public WorldRelation instantiateWithBinding(Map<Thing, Thing> binding) {
        try {
            WorldRelation clone = (WorldRelation) this.clone();
            clone.relates = new HashMap<>();
            this.relates.keySet().forEach(role ->  {
                Thing thing = binding.get(this.relates.get(role));
                if (thing instanceof WorldObject) {
                    ((WorldObject) thing).getRelations().add(clone);
                }
                clone.relates.put(role, thing);
            });
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isMatchableWith(Thing other) {
        if (other instanceof WorldRelation) {
            WorldRelation otherAsRelation = (WorldRelation) other;
            if (getArity() != otherAsRelation.getArity()) {
                return false;
            }
            return getClass().isAssignableFrom(otherAsRelation.getClass());
        }
        return false;
    }
}

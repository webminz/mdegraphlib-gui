package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Thing;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldObject;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldRelation;

import java.util.*;

public class Binding {

    private final Map<Thing, Thing> bindingMap;

    public Binding(World toBind) {
        this.bindingMap = new HashMap<>();
        for (Thing t : toBind.getThings()) {
            this.bindingMap.put(t, null);
        }
    }

    public boolean hasUnboundElements() {
        return this.bindingMap.entrySet().stream().anyMatch(e -> e.getValue() == null);
    }
    
    public void insert(Set<Thing> insertions, World result) {
        insertions.stream().filter(t -> !(t instanceof  WorldRelation)).forEach(t ->result.getThings().add(t));
        insertions.stream().filter(t -> t instanceof WorldRelation)
                .map(t -> (WorldRelation)t)
                .forEach(rel -> {
                    WorldRelation newRel = rel.instantiateWithBinding(this.bindingMap);
                    result.getThings().add(newRel);
                });
    }

    public void delete(Set<Thing> deletions, World result) {
        deletions.stream().filter(t -> !(t instanceof WorldRelation)).forEach(t -> result.getThings().remove(this.bindingMap.get(t)));
        deletions.stream()
                .filter(r -> r instanceof WorldRelation)
                .map(this.bindingMap::get)
                .map(t -> (WorldRelation)t)
                .forEach(rel -> {
                    result.getThings().remove(rel);
                    rel.getRoles().forEach(role -> {
                        Thing t = rel.getThingInRelation(role);
                        if (t instanceof WorldObject) {
                            ((WorldObject) t).getRelations().remove(rel);
                        }
                    });
                });
    }


    public boolean narrow(Thing start, Thing startPartner, World pattern, World world) {
        if (start instanceof WorldObject && startPartner instanceof WorldObject) {
            return this.narrow((WorldObject) start, (WorldObject) startPartner, pattern, world);
        } else {
            this.bindingMap.put(start, startPartner);
            return true;
        }
    }

    public boolean narrow(WorldObject start, WorldObject startPartner, World pattern, World world) {
        if (this.bindingMap.containsKey(start) && this.bindingMap.get(start) == null) {
            this.bindingMap.put(start, startPartner);

            for (WorldRelation relation : start.getRelations()) {
                if (bindingMap.containsKey(relation) && this.bindingMap.get(relation) == null) { // only relevant if the pattern world contains this relation and it has not been bound yet
                    String roleName = relation.getRole(start);
                    List<WorldRelation> partners = world.findRelation(relation.getName(), roleName, startPartner);
                    if (partners.isEmpty()) {
                        return false; // err, we could not match this relation
                    }
                    WorldRelation partner;
                    if (partners.size() == 1) {
                        partner = partners.get(0);
                    } else {
                        Optional<WorldRelation> alt = partners.stream().filter(r -> !this.bindingMap.values().contains(r)).findFirst();
                        if (alt.isPresent()) {
                            partner = alt.get();
                        } else {
                            return false;
                        }
                    }
                    this.bindingMap.put(relation,partner);
                    boolean allGood = true;
                    for (String role : relation.getRoles()) {
                        if (!role.equals(roleName)) {
                            if (relation.getThingInRelation(role).isMatchableWith(partner.getThingInRelation(role))) {
                                allGood = this.narrow(relation.getThingInRelation(role),partner.getThingInRelation(role), pattern, world);
                                if (!allGood) {
                                    return false; // do it all recusively
                                }
                            } else {
                                return false; // we could not match these
                            }
                        }
                    }
                }
            }

        }
        return true;
        // Otherwise we have already been here and nothing has to be done
    }
}

package no.hvl.past.webui.transfer.quickRuleEngine.service;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.*;

import java.util.Collections;

public interface WorldRenderer {

    static final WorldRenderer EMPTY_RENDERER = new WorldRenderer() {
        @Override
        public WorldRepresentation render(World world) {
            return new GridWorld(world, 0, 0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }

        @Override
        public WorldRepresentation.VisualElement render(WorldObject object) {
            return null;
        }

        @Override
        public WorldRepresentation.VisualElement render(Event event) {
            return null;
        }

        @Override
        public WorldRepresentation.VisualElement render(TemporalThing temporalThing) {
            return null;
        }

        @Override
        public WorldRepresentation.VisualElement render(WorldRelation relation) {
            return null;
        }
    };

    WorldRepresentation render(World world);

    WorldRepresentation.VisualElement render(WorldObject object);

    WorldRepresentation.VisualElement render(Event event);

    WorldRepresentation.VisualElement render(TemporalThing temporalThing);

    WorldRepresentation.VisualElement render(WorldRelation relation);

}

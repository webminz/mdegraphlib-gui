package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.World;
import no.hvl.past.webui.transfer.quickRuleEngine.domain.WorldObject;

import java.util.Optional;

public class Pattern {

    private World worldExcerpt;

    public Pattern(World worldExcerpt) {
        this.worldExcerpt = worldExcerpt;
    }

    public Pattern() {
        this.worldExcerpt = null;
    }

    public Optional<Binding> match(World world) {
        if (worldExcerpt == null) {
            return Optional.empty();
        }

        Binding b = new Binding(worldExcerpt);
        while (b.hasUnboundElements()) {
            Optional<WorldObject> singleton = worldExcerpt.getThings()
                    .stream()
                    .filter(t -> t instanceof WorldObject)
                    .map(t -> (WorldObject) t)
                    .filter(WorldObject::isSingleton)
                    .findFirst();
            if (singleton.isPresent()) {
                Optional<WorldObject> singletonPartner = world.findSingleton(singleton.get());
                if (!singletonPartner.map(o -> b.narrow(singleton.get(), singletonPartner.get(), worldExcerpt, world)).orElse(false)) {
                    return Optional.empty(); // If we cannot bind the singleton there is no chance to find a solution
                }
            } else {
                // FIXME do backtracking
                return Optional.empty();
            }
        }
        return Optional.of(b);
    }

    public World getWorldExcerpt() {
        return worldExcerpt;
    }

    public void setWorldExcerpt(World worldExcerpt) {
        this.worldExcerpt = worldExcerpt;
    }
}

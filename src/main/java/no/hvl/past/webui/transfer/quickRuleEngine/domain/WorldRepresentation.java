package no.hvl.past.webui.transfer.quickRuleEngine.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class WorldRepresentation {

    public static abstract class VisualElement {

        private Thing displays;
        private String imageUrl;
        private Set<String> cssClassNames;

        VisualElement(Thing displays, String imageUrl, String... cssClassNames) {
            this.displays = displays;
            this.imageUrl = imageUrl;
            this.cssClassNames = new HashSet<>(Arrays.asList(cssClassNames));
        }

        public abstract String getTitle();

        public Thing getDisplays() {
            return displays;
        }

        public void setDisplays(Thing displays) {
            this.displays = displays;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Set<String> getCssClassNames() {
            return cssClassNames;
        }

        public void addCssClass(String className) {
            this.cssClassNames.add(className);
        }

        public void removeCssClass(String className) {
            this.cssClassNames.remove(className);
        }

        public void removeAllCssClasses() {
            this.cssClassNames.clear();
        }

    }

    private World world;

    public WorldRepresentation(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public abstract Collection<VisualElement> getDisplayed();

}

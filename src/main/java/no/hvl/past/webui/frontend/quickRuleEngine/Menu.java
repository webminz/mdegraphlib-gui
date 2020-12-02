package no.hvl.past.webui.frontend.quickRuleEngine;

import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Menu extends TreeGrid<MenuItem>  {

    private final List<MenuItem> menuItems;

    public Menu(RuleEditorView mainGUI) {
        this.menuItems = new ArrayList<>();
        addComponentHierarchyColumn(item -> {
            return item.render(mainGUI);
        });
        setHeightFull();
        HierarchicalDataProvider<MenuItem, Void> provider = new AbstractBackEndHierarchicalDataProvider<MenuItem, Void>() {
            @Override
            protected Stream<MenuItem> fetchChildrenFromBackEnd(HierarchicalQuery<MenuItem, Void> hierarchicalQuery) {
                return hierarchicalQuery.getParentOptional().map(mi -> mi.children().stream()).orElse(menuItems.stream());
            }

            @Override
            public int getChildCount(HierarchicalQuery<MenuItem, Void> hierarchicalQuery) {
                return hierarchicalQuery.getParentOptional().map(mi -> mi.children().size()).orElse(2);
            }

            @Override
            public boolean hasChildren(MenuItem menuItem) {
                return menuItem.hasChildren();
            }
        };
        setDataProvider(provider);
    }

    public void addItems(Collection<MenuItem> rootElements) {
        this.menuItems.clear();
        this.menuItems.addAll(rootElements);
        //setItems(rootElements, MenuItem::children);
    }


}

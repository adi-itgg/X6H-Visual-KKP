package io.aitech.pv;

import java.awt.*;
import java.util.List;

public class CustomFocusTraversalPolicy extends FocusTraversalPolicy {

    private final java.util.List<Component> order;

    public CustomFocusTraversalPolicy(List<Component> order) {
        this.order = order;
    }

    @Override
    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
        int idx = (order.indexOf(aComponent) + 1) % order.size();
        return order.get(idx);
    }

    @Override
    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
        int idx = (order.indexOf(aComponent) - 1 + order.size()) % order.size();
        return order.get(idx);
    }

    @Override
    public Component getFirstComponent(Container focusCycleRoot) {
        return order.getFirst();
    }

    @Override
    public Component getLastComponent(Container focusCycleRoot) {
        return order.getLast();
    }

    @Override
    public Component getDefaultComponent(Container focusCycleRoot) {
        return order.getFirst();
    }

}
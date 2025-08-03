package ch.mzh.cc.model;

import ch.mzh.cc.Position2D;
import ch.mzh.cc.components.Component;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private String name;
    private EntityType type;
    private Position2D position;

    // private Vector2 worldPosition;
    private boolean active;
    private boolean selectable;

    protected Map<Class<? extends Component>, Component> components;

    public Entity() {}
    
    public Entity(String name, EntityType type, Position2D position) {
        this.name = name;
        this.type = type;
        this.components = new HashMap<>();
        this.position = position;
        // this.worldPosition = new Vector2();
        this.active = true;
        this.selectable = true; // Most entities are selectable by default
        // updateWorldPosition(); // TODO: Refactor to frontend
    }

    public void setGridPosition(Position2D position) {
        this.position = position;
        // updateWorldPosition(); // TODO: Refactor to frontend
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentType) {
        // First try exact match (for performance)
        Component component = components.get(componentType);
        if (component != null) {
            return (T) component;
        }

        // Then try inheritance match
        for (Component comp : components.values()) {
            if (componentType.isAssignableFrom(comp.getClass())) {
                return (T) comp;
            }
        }
        return null;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentType) {
        return components.containsKey(componentType);
    }

    public <T extends Component> void removeComponent(Class<T> componentType) {
        components.remove(componentType);
    }

    /*
    private void updateWorldPosition() {
        worldPosition.set(position.getX() * 32, position.getY() * 32);
    }
    */

    // Getters
    public String getName() { return this.name; }
    public EntityType getType() { return type; }
    public Position2D getPosition() { return position; }
    public boolean isActive() { return active; }
    public boolean isSelectable() { return selectable; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setActive(boolean active) { this.active = active; }
    public void setSelectable(boolean selectable) { this.selectable = selectable; }
}

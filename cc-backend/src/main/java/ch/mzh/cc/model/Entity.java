package ch.mzh.cc.model;

import ch.mzh.cc.Position2D;
import ch.mzh.cc.components.Component;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private String name;
    private EntityType type;
    private Position2D position;
    private int playerId = -1; // -1: neutral, 1: player1, 2: player2

    // private Vector2 worldPosition;
    private boolean active;
    private boolean selectable;

    protected Map<Class<? extends Component>, Component> components;

    public Entity() {}
    
    public Entity(String name, EntityType type, Position2D position, int playerId) {
        this.name = name;
        this.type = type;
        this.components = new HashMap<>();
        this.position = position;
        this.playerId = playerId;
        this.active = true;
        this.selectable = true;
    }

    public void setGridPosition(Position2D position) {
        this.position = position;
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentType) {
        Component component = components.get(componentType);
        if (component != null) {
            return (T) component;
        }

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

    // Getters
    public String getName() { return this.name; }
    public EntityType getType() { return type; }
    public Position2D getPosition() { return position; }
    public boolean isActive() { return active; }
    public boolean isSelectable() { return selectable; }
    public int getPlayerId() { return this.playerId; }

    public boolean isOwnedBy(int playerId) { return this.getPlayerId() == playerId; }
    public boolean isSamePlayer(Entity other) {
        return this.playerId != -1 && this.playerId == other.playerId;
    }
    public boolean isNeutral() { return playerId == -1; }
    public boolean isEnemy(Entity other) {
        return this.playerId != -1 && other.playerId != -1 && this.playerId != other.playerId;
    }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setActive(boolean active) { this.active = active; }
    public void setSelectable(boolean selectable) { this.selectable = selectable; }
}

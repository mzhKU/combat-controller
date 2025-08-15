package ch.mzh.cc;

import ch.mzh.cc.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityManager {

  private final List<Entity> entities;
  private final Map<String, Entity> entitiesMap;

  public EntityManager() {
    entities = new ArrayList<>();
    entitiesMap = new HashMap<>();
  }

  public List<Entity> getEntities() {
    return entities;
  }

  public void addEntity(Entity entity) {
    entities.add(entity);
    entitiesMap.put(entity.getName(), entity);
  }

  public Entity getEntity(String entityName) {
    return entitiesMap.get(entityName);
  }

  public Entity getEntityAt(Position2D position) {
    // TODO: Return Optional<Enity>
    for (Entity entity : entities) {
      if (entity.isActive() && entity.isSelectable() &&
              entity.getPosition().getX() == position.getX() &&
              entity.getPosition().getY() == position.getY()) {
        return entity;
      }
    }
    return null;
  }
}

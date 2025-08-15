package ch.mzh.cc;

import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;

import java.util.*;
import java.util.stream.Collectors;

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

  public Optional<Entity> getEntityAt(Position2D position) {

    for (Entity entity : entities) {
      if (entity.isActive() && entity.isSelectable() &&
              entity.getPosition().getX() == position.getX() &&
              entity.getPosition().getY() == position.getY()) {
        return Optional.of(entity);
      }
    }
    return Optional.empty();
  }

  public void removeEntity(Entity entity) {
    entities.remove(entity);
  }
  /**
   * Returns all active entities within the specified range from a center position
   * @param center The center position to search from
   * @param range Maximum Manhattan distance (inclusive)
   * @return List of entities within range
   */
  public List<Entity> getEntitiesInRange(Position2D center, int range) {
    return entities.stream()
            .filter(Entity::isActive)
            .filter(entity -> Grid.calculateManhattanDistance(center, entity.getPosition()) <= range)
            .toList();
  }

  /**
   * Returns all active entities adjacent to the given position (Manhattan distance = 1)
   * @param center The center position
   * @return List of adjacent entities
   */
  public List<Entity> getAdjacentEntities(Position2D center) {
    return getEntitiesInRange(center, 1);
  }

  /**
   * Returns all active entities at exactly the specified distance
   * @param center The center position
   * @param exactDistance The exact Manhattan distance
   * @return List of entities at exact distance
   */
  public List<Entity> getEntitiesAtDistance(Position2D center, int exactDistance) {
    return entities.stream()
            .filter(Entity::isActive)
            .filter(entity -> Grid.calculateManhattanDistance(center, entity.getPosition()) == exactDistance)
            .toList();
  }

  /**
   * Returns entities in range filtered by type and player
   */
  public List<Entity> getEntitiesInRange(Position2D center, int range, EntityType type, int playerId) {
    return getEntitiesInRange(center, range).stream()
            .filter(entity -> entity.getType() == type)
            .filter(entity -> entity.isOwnedBy(playerId))
            .toList();
  }

  /**
   * Finds the first entity of specified type within range belonging to the player
   */
  public Optional<Entity> findEntityByTypeInRange(Position2D center, int range, EntityType type, int playerId) {
    return getEntitiesInRange(center, range, type, playerId).stream()
            .findFirst();
  }

  /**
   * Gets all entities adjacent to a position that belong to a specific player
   */
  public List<Entity> getFriendlyAdjacentEntities(Position2D center, int playerId) {
    return getAdjacentEntities(center).stream()
            .filter(entity -> entity.isOwnedBy(playerId))
            .toList();
  }

  /**
   * Gets all entities adjacent to a position that are enemies of the specified player
   */
  public List<Entity> getEnemyAdjacentEntities(Position2D center, int playerId) {
    return getAdjacentEntities(center).stream()
            .filter(entity -> !entity.isNeutral() && !entity.isOwnedBy(playerId))
            .toList();
  }

  /**
   * Checks if there are any entities of a specific type within range
   */
  public boolean hasEntityTypeInRange(Position2D center, int range, EntityType type) {
    return entities.stream()
            .filter(Entity::isActive)
            .anyMatch(entity -> entity.getType() == type &&
                    Grid.calculateManhattanDistance(center, entity.getPosition()) <= range);
  }

  /**
   * Gets all positions adjacent to center that are occupied by entities
   */
  public List<Position2D> getOccupiedAdjacentPositions(Position2D center) {
    return getAdjacentEntities(center).stream()
            .map(Entity::getPosition)
            .toList();
  }

  /**
   * Gets all positions adjacent to center (including empty ones)
   * Useful for movement validation
   */
  public List<Position2D> getAllAdjacentPositions(Position2D center) {
    List<Position2D> adjacentPositions = new ArrayList<>();

    // Add all 4 cardinal directions (or 8 if you want diagonals)
    adjacentPositions.add(new Position2D(center.getX() - 1, center.getY()));     // Left
    adjacentPositions.add(new Position2D(center.getX() + 1, center.getY()));     // Right
    adjacentPositions.add(new Position2D(center.getX(), center.getY() - 1));     // Down
    adjacentPositions.add(new Position2D(center.getX(), center.getY() + 1));     // Up

    // If you want diagonal movement too:
    // adjacentPositions.add(new Position2D(center.getX() - 1, center.getY() - 1)); // Bottom-left
    // adjacentPositions.add(new Position2D(center.getX() - 1, center.getY() + 1)); // Top-left
    // adjacentPositions.add(new Position2D(center.getX() + 1, center.getY() - 1)); // Bottom-right
    // adjacentPositions.add(new Position2D(center.getX() + 1, center.getY() + 1)); // Top-right

    return adjacentPositions;
  }

  /**
   * Gets all empty adjacent positions (not occupied by any entity)
   */
  public List<Position2D> getEmptyAdjacentPositions(Position2D center) {
    List<Position2D> allAdjacent = getAllAdjacentPositions(center);
    Set<Position2D> occupied = getAdjacentEntities(center).stream()
            .map(Entity::getPosition)
            .collect(Collectors.toSet());

    return allAdjacent.stream()
            .filter(pos -> !occupied.contains(pos))
            .toList();
  }
}
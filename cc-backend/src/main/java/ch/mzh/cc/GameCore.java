package ch.mzh.cc;

import ch.mzh.cc.command.CommandProcessor;
import ch.mzh.cc.components.CannonComponent;
import ch.mzh.cc.components.FuelSystem;
import ch.mzh.cc.components.HealthComponent;
import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.model.Entity;

public class GameCore {

  private final Grid grid; // TODO: Initialize
  private Entity selectedEntity; // TODO: Could be in a "GameState"?
  private final CommandProcessor commandProcessor;

  private final GameEventManager gameEventManager;
  private final EntityManager entityManager;

  private static final int GRID_WIDTH = 40;
  private static final int GRID_HEIGHT = 40;
  private static final int TILE_SIZE = 32; // 32px tiles
  private static final String FAILURE_REASON = "DEFAULT_REASON";

  public GameCore() {
    this.gameEventManager = new GameEventManager();
    this.entityManager = new EntityManager();
    this.grid = new Grid(GRID_WIDTH, GRID_HEIGHT, TILE_SIZE);
    this.commandProcessor = new CommandProcessor(this);
    FuelSystem fuelSystem = new FuelSystem();
    SupplyRuleEngine supplyRuleEngine = new SupplyRuleEngine(entityManager, fuelSystem);

    // Backend systems listen to events
    gameEventManager.addListener(supplyRuleEngine);
    gameEventManager.addListener(fuelSystem);
  }

  public boolean moveEntity(Entity entity, Position2D targetPosition) {
    boolean entityMoved;

    if (grid.isInvalidPosition(targetPosition)) return false;

    Position2D oldPosition = entity.getPosition();
    VehicleMovementComponent movement = entity.getComponent(VehicleMovementComponent.class);
    entityMoved = movement.move(entity, targetPosition);

    if (entityMoved) {
      gameEventManager.fireEntityMovedSuccessful(entity, oldPosition, targetPosition);
    } else {
      gameEventManager.fireEntityMovedFailed(entity, oldPosition, targetPosition, FAILURE_REASON);
    }
    return entityMoved;
  }

  public boolean selectEntity(Position2D position) {
    Entity entity = entityManager.getEntityAt(position);

    if (entity == null) {
      selectedEntity = null;
      return false;
    }

    selectedEntity = entity;
    gameEventManager.fireEntitySelected(selectedEntity);
    return true;
  }

  public boolean fireAtPosition(Entity shooter, Position2D targetPosition) {
    if (grid.isInvalidPosition(targetPosition)) return false;

    CannonComponent weapon = shooter.getComponent(CannonComponent.class);
    weapon.fire();

    // Simple hit calculation - you can make this more sophisticated
    Entity target = entityManager.getEntityAt(targetPosition);
    boolean hit = target != null;

    if (hit) {
      HealthComponent health = target.getComponent(HealthComponent.class);
      if (health != null && health.takeDamage(weapon.getDamage())) {
        // Target destroyed
        entityManager.removeEntity(target);
        gameEventManager.fireEntityDestroyed(target);
      }
    }

    gameEventManager.fireEntityFired(shooter, targetPosition, hit);
    return true;
  }

  public boolean noEntitySelected() {
    return selectedEntity == null;
  }

  public Entity getSelectedEntity() {
    return this.selectedEntity;
  }

  public GameEventManager getGameEventManager() {
    return this.gameEventManager;
  }

  public EntityManager getEntityManager() {
    return this.entityManager;
  }

  public Grid getGrid() {
    return this.grid;
  }

  public CommandProcessor getCommandProcessor() {
    return this.commandProcessor;
  }
}

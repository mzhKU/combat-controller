package ch.mzh.cc;

import ch.mzh.cc.command.CommandProcessor;
import ch.mzh.cc.components.CannonComponent;
import ch.mzh.cc.components.FuelSystem;
import ch.mzh.cc.components.HealthComponent;
import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.play.GameSystem;

public class GameCore {

  private final Grid grid;
  private Entity selectedEntity;
  private final CommandProcessor commandProcessor;

  private final GameEventManager gameEventManager;
  private final EntityManager entityManager;
  private final GameSystem gameSystem;

  private boolean isEntitySelected;

  private static final int GRID_WIDTH = 30;
  private static final int GRID_HEIGHT = 60;

  // TODO: Use same value here and in Game
  private static final int TILE_SIZE = 15; // 10px tiles

  private static final String FAILURE_REASON = "DEFAULT_REASON";

  public GameCore() {
    this.gameEventManager = new GameEventManager();
    this.entityManager = new EntityManager();
    this.grid = new Grid(GRID_WIDTH, GRID_HEIGHT, TILE_SIZE);
    this.commandProcessor = new CommandProcessor(this);
    this.gameSystem = new GameSystem();

    FuelSystem fuelSystem = new FuelSystem();
    GameRuleEngine gameRuleEngine = new GameRuleEngine(entityManager, fuelSystem, gameSystem, gameEventManager);

    // Backend systems listen to events
    gameEventManager.addListener(gameRuleEngine);
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
    return entityManager.getEntityAt(position)
            .map(entity -> {
              selectedEntity = entity;
              gameEventManager.fireEntitySelected(selectedEntity);
              setEntitySelected(true);
              return true;
            })
            .orElseGet(() -> {
              selectedEntity = null;
              setEntitySelected(false);
              return false;
            });
  }

  public boolean fireAtPosition(Entity shooter, Position2D targetPosition) {
    if (grid.isInvalidPosition(targetPosition)) return false;

    CannonComponent weapon = shooter.getComponent(CannonComponent.class);
    weapon.fire();

    boolean hit = false;
    Entity target = entityManager.getEntityAt(targetPosition).orElse(null);

    if (target != null) {
      hit = true;

      HealthComponent health = target.getComponent(HealthComponent.class);
      if (health != null && health.destroyedIfHealthBelowZero(weapon.getDamage())) {
        entityManager.removeEntity(target);
        gameEventManager.fireEntityDestroyed(shooter.getPlayerId(), target);
      }
    }

    gameEventManager.fireEntityFired(shooter, targetPosition, hit);
    return true;
  }

  public boolean noEntitySelected() {
    return selectedEntity == null;
  }


  public GameEventManager getGameEventManager() {
    return this.gameEventManager;
  }
  public EntityManager getEntityManager() {
    return this.entityManager;
  }
  public GameSystem getGameSystem() {
    return this.gameSystem;
  }
  public Entity getSelectedEntity() {
    return this.selectedEntity;
  }
  public Grid getGrid() {
    return this.grid;
  }


  public boolean isEntitySelected() {
    return this.isEntitySelected;
  }

  private void setEntitySelected(boolean selected) {
    this.isEntitySelected = selected;
  }

}

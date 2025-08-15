package ch.mzh.cc;

import ch.mzh.cc.command.CommandProcessor;
import ch.mzh.cc.components.CannonComponent;
import ch.mzh.cc.components.FuelSystem;
import ch.mzh.cc.components.HealthComponent;
import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.play.GameState;

import java.util.Optional;

public class GameCore {

  private final Grid grid;
  private Entity selectedEntity;
  private final CommandProcessor commandProcessor;

  private final GameEventManager gameEventManager;
  private final EntityManager entityManager;
  private final GameState gameState;

  private boolean isEntitySelected;

  private static final int GRID_WIDTH = 40;
  private static final int GRID_HEIGHT = 80;

  // TODO: Use same value here and in Game
  private static final int TILE_SIZE = 10; // 10px tiles

  private static final String FAILURE_REASON = "DEFAULT_REASON";

  public GameCore() {
    this.gameEventManager = new GameEventManager();
    this.entityManager = new EntityManager();
    this.grid = new Grid(GRID_WIDTH, GRID_HEIGHT, TILE_SIZE);
    this.commandProcessor = new CommandProcessor(this);
    this.gameState = new GameState();

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

    boolean hit = entityManager.getEntityAt(targetPosition)
            .map(target -> {
              Optional.ofNullable(target.getComponent(HealthComponent.class))
                      .filter(health -> health.takeDamage(weapon.getDamage()))
                      .ifPresent(health -> {
                        entityManager.removeEntity(target);
                        gameEventManager.fireEntityDestroyed(target);
                      });
              return true;
            })
            .orElse(false);

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

  public GameState getGameState() {
    return this.gameState;
  }

  public CommandProcessor getCommandProcessor() {
    return this.commandProcessor;
  }

  public boolean isEntitySelected() {
    return this.isEntitySelected;
  }

  private void setEntitySelected(boolean selected) {
    this.isEntitySelected = selected;
  }
}

package ch.mzh.cc;

import ch.mzh.cc.components.FuelSystem;
import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.model.Entity;

public class GameCore {

  private Grid grid;

  private GameEventManager eventManager;
  private EntityManager entityManager;
  private SupplyRuleEngine supplyRuleEngine;
  private FuelSystem fuelSystem;

  public GameCore() {
    this.eventManager = new GameEventManager();
    this.entityManager = new EntityManager();
    this.fuelSystem = new FuelSystem();
    this.supplyRuleEngine = new SupplyRuleEngine(entityManager, fuelSystem);

    // Backend systems listen to events
    eventManager.addListener(supplyRuleEngine);
    eventManager.addListener(fuelSystem);
  }

  public boolean moveEntity(Entity entity, Position2D targetPosition) {
    Position2D oldPosition = entity.getPosition();

    if (grid.isInvalidPosition(targetPosition)) {
      return false;
    }

    VehicleMovementComponent movement = entity.getComponent(VehicleMovementComponent.class);
    boolean moved = movement.move(entity, targetPosition);

    if (moved) {
      eventManager.fireEntityMoved(entity, oldPosition, targetPosition);
    }

    return moved;
  }

}

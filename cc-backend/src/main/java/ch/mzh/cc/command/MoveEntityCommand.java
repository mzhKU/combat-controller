package ch.mzh.cc.command;

import ch.mzh.cc.GameCore;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.model.Entity;

public class MoveEntityCommand extends GameCommand {
  private final Position2D targetPosition;

  public MoveEntityCommand(Position2D targetPosition) {
    this.targetPosition = targetPosition;
  }

  @Override
  public boolean canExecute(GameCore gameCore) {

    if (gameCore.noEntitySelected()) {
      failureReason = "No entity selected.";
      return false;
    }

    if (gameCore.getGrid().isInvalidPosition(targetPosition)) {
      failureReason = "Invalid target position: (" + targetPosition.getX() + ", " + targetPosition.getY() + ")";
      return false;
    }

    Entity selectedEntity = gameCore.getSelectedEntity();
    Entity entity = gameCore.getEntityManager().getEntityAt(targetPosition);

    if (entity != null && entity != selectedEntity) {
      failureReason = "Position occupied by " + entity.getType() + " at (" + targetPosition.getX() + ", " + targetPosition.getY() + ")";
      return false;
    }

    if (!selectedEntity.hasComponent(VehicleMovementComponent.class)) {
      failureReason = "Selected entity cannot move: " + selectedEntity.getName();
      return false;
    }

    return true;
  }

  @Override
  public boolean execute(GameCore gameCore) {
    if (!canExecute(gameCore)) {
      return false;
    }

    Entity selectedEntity = gameCore.getSelectedEntity();
    return gameCore.moveEntity(selectedEntity, targetPosition);
  }

}

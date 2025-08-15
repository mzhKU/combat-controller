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
    if (!gameCore.isEntitySelected()) {
      failureReason = "No entity selected.";
      return false;
    }

    Entity selectedEntity = gameCore.getSelectedEntity();

    if (!gameCore.getGameState().canSelectEntity(selectedEntity)) {
      failureReason = "Cannot move opponent's unit";
      return false;
    }

    if (gameCore.getGrid().isInvalidPosition(targetPosition)) {
      failureReason = "Invalid target position: (" + targetPosition.getX() + ", " + targetPosition.getY() + ")";
      return false;
    }

    // Check if position is occupied by another entity
    return gameCore.getEntityManager().getEntityAt(targetPosition)
            .filter(occupant -> occupant != selectedEntity)
            .map(occupant -> {
              if (occupant.isSamePlayer(selectedEntity)) {
                failureReason = "Position occupied by friendly " + occupant.getType() + " at (" + targetPosition.getX() + ", " + targetPosition.getY() + ")";
              } else {
                failureReason = "Position occupied by enemy " + occupant.getType() + " at (" + targetPosition.getX() + ", " + targetPosition.getY() + ")";
              }
              return false;
            })
            .orElseGet(() -> {
              if (!selectedEntity.hasComponent(VehicleMovementComponent.class)) {
                failureReason = "Selected entity cannot move: " + selectedEntity.getName();
                return false;
              }
              return true;
            }); // Position is free or validation continues
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

package ch.mzh.cc.command;

import ch.mzh.cc.GameCore;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.components.CannonComponent;
import ch.mzh.cc.model.Entity;

import static ch.mzh.cc.Grid.calculateManhattanDistance;

public class FireCommand extends GameCommand {
  private final Position2D targetPosition;

  public FireCommand(Position2D targetPosition) {
    this.targetPosition = targetPosition;
  }

  @Override
  public boolean canExecute(GameCore gameCore) {
    Entity selected = gameCore.getSelectedEntity();
    if (selected == null) {
      failureReason = "No unit selected";
      return false;
    }

    if (gameCore.getGameSystem().isEnemyUnit(selected)) {
      failureReason = "Cannot fire with opponent's unit";
      return false;
    }

    CannonComponent weapon = selected.getComponent(CannonComponent.class);
    if (weapon == null) {
      failureReason = selected.getName() + " cannot fire";
      return false;
    }

    if (weapon.isEmpty()) {
      failureReason = selected.getName() + " is out of ammunition";
      return false;
    }

    int distance = calculateManhattanDistance(selected.getPosition(), targetPosition);
    if (distance > weapon.getRange()) {
      failureReason = "Target out of range (" + distance + "/" + weapon.getRange() + ")";
      return false;
    }

    return true;
  }

  @Override
  public boolean execute(GameCore gameCore) {
    if (!canExecute(gameCore)) return false;

    Entity selected = gameCore.getSelectedEntity();
    return gameCore.fireAtPosition(selected, targetPosition);
  }
}

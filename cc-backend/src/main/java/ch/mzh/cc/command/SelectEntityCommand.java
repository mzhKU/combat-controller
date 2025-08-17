package ch.mzh.cc.command;

import ch.mzh.cc.GameCore;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;

public class SelectEntityCommand extends GameCommand {
  private final Position2D position;

  public SelectEntityCommand(Position2D position) {
    this.position = position;
  }

  @Override
  public boolean execute(GameCore gameCore) {
    return gameCore.selectEntity(position);
  }

  @Override
  public boolean canExecute(GameCore gameCore) {
    Entity entityAtPosition = gameCore.getEntityManager().getEntityAt(position).orElse(null);

    if (entityAtPosition == null) {
      // Allow deselection by clicking empty space
      return true;
    }

    // Check if entity belongs to current player
    if (gameCore.getGameSystem().isEnemyUnit(entityAtPosition)) {
      failureReason = "Cannot select opponent's unit";
      return false;
    }

    return true;
  }
}

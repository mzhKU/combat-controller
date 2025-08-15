package ch.mzh.cc.command;

import ch.mzh.cc.GameCore;
import ch.mzh.cc.Position2D;

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
    return true; // TODO: Should this contain any logic?
  }
}

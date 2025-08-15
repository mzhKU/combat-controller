package ch.mzh.cc.command;

import ch.mzh.cc.GameCore;

public abstract class PlayerCommand extends GameCommand {
  protected final int playerId;

  public PlayerCommand(int playerId) {
    this.playerId = playerId;
  }

  @Override
  public boolean canExecute(GameCore gameCore) {
    return gameCore.getGameState().canTakeAction(playerId) && canExecuteForPlayer(gameCore);
  }

  protected abstract boolean canExecuteForPlayer(GameCore gameCore);
}

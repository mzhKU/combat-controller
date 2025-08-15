package ch.mzh.cc.command;

import ch.mzh.cc.GameCore;

public class EndTurnCommand extends PlayerCommand {

  public EndTurnCommand(int playerId) {
    super(playerId);
  }

  @Override
  protected boolean canExecuteForPlayer(GameCore gameCore) {
    return false;
  }

  @Override
  public boolean execute(GameCore gameCore) {
    gameCore.getGameState().endTurn();
    gameCore.getGameEventManager().fireTurnEnded(playerId);
    return true;
  }
}

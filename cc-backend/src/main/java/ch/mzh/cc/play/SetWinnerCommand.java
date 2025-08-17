package ch.mzh.cc.play;

import ch.mzh.cc.GameEventManager;

public class SetWinnerCommand implements GameSystemCommand {
  private final int winnerId;

  public SetWinnerCommand(int winnerId) {
    this.winnerId = winnerId;
  }

  @Override
  public void execute(GameSystem gameSystem, GameEventManager eventManager) {
    gameSystem.setWinner(winnerId);
    gameSystem.setGameOver();
    eventManager.fireGameOver(winnerId);
  }
}
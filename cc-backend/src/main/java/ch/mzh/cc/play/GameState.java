package ch.mzh.cc.play;

import ch.mzh.cc.model.Entity;

public class GameState {

  private GamePhase currentPhase = GamePhase.PLAYER1_TURN;
  private int turnNumber = 1;
  private boolean actionTaken = false;

  public boolean canTakeAction(int playerId) {
    return (currentPhase == GamePhase.PLAYER1_TURN && playerId == 1) ||
            (currentPhase == GamePhase.PLAYER2_TURN && playerId == 2);
  }

  public void endTurn() {
    currentPhase = (currentPhase == GamePhase.PLAYER1_TURN) ?
            GamePhase.PLAYER2_TURN : GamePhase.PLAYER1_TURN;
    if (currentPhase == GamePhase.PLAYER1_TURN) turnNumber++;
  }

  public void initializeRandomStartingPlayer() {
    currentPhase = Math.random() < 0.5 ? GamePhase.PLAYER1_TURN : GamePhase.PLAYER2_TURN;
  }

  public int getCurrentPlayerId() {
    return switch (currentPhase) {
      case PLAYER1_TURN -> 1;
      case PLAYER2_TURN -> 2;
      case GAME_OVER -> -1;
    };
  }

  public boolean canSelectEntity(Entity entity) {
    return entity.getPlayerId() == getCurrentPlayerId();
  }
}

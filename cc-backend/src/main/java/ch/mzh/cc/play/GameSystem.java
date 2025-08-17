package ch.mzh.cc.play;

import ch.mzh.cc.model.Entity;

public class GameSystem {

  private GamePhase currentPhase;
  private int winnerId = -1; // -1: no winner, 1: player 1, 2: player 2

  public boolean canTakeAction(int playerId) {
    return (currentPhase == GamePhase.PLAYER1_TURN && playerId == 1) ||
            (currentPhase == GamePhase.PLAYER2_TURN && playerId == 2);
  }

  public void endTurn() {
    currentPhase = (currentPhase == GamePhase.PLAYER1_TURN) ? GamePhase.PLAYER2_TURN : GamePhase.PLAYER1_TURN;
  }

  public void setGameOver() {
    currentPhase = GamePhase.GAME_OVER;
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

  public void setWinner(int winnerId) {
    this.winnerId = winnerId;
  }

  // TODO: This is out of place here. Better in in GameCore or something...
  public boolean isEnemyUnit(Entity entity) {
    return entity.getPlayerId() != getCurrentPlayerId();
  }

  public boolean isGameOver() {
    return currentPhase == GamePhase.GAME_OVER;
  }

  public int getWinnerId() {
    return winnerId;
  }
}

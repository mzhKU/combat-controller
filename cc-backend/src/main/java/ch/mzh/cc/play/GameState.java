package ch.mzh.cc.play;

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

}

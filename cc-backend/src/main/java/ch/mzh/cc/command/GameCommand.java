package ch.mzh.cc.command;

public abstract class GameCommand implements Command {
  protected String failureReason;

  public String getFailureReason() {
    return this.failureReason;
  }
}

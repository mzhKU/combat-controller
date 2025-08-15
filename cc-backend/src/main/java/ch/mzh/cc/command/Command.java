package ch.mzh.cc.command;

import ch.mzh.cc.GameCore;

public interface Command {
  boolean execute(GameCore gameCore);
  boolean canExecute(GameCore gameCore);

  String getFailureReason();
}

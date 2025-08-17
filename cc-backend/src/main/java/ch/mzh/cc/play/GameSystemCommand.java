package ch.mzh.cc.play;

import ch.mzh.cc.GameEventManager;

public interface GameSystemCommand {
  void execute(GameSystem gameSystem, GameEventManager eventManager);
}

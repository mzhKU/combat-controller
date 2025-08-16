package ch.mzh.cc;

import ch.mzh.cc.model.Entity;
import ch.mzh.cc.play.GameState;

public interface GameEventListener {
  default void onEntityMoved(Entity entity, Position2D oldPos, Position2D newPos) {}
  default void onEntityMovedFailed(Entity movedEntity, Position2D oldPos, Position2D targetPosition, String failReason) {}
  default void onEntitySelected(Entity entity) {}
  default void onEntityDeselected() {}
  default void onEntityFired(Entity shooter, Position2D targetPosition, boolean hit) {}
  default void onEntityDestroyed(Entity entity) {}
  default void onFuelConsumed(Entity entity, int amount) {}
  default void onTurnEnded(int playerId) {}
}

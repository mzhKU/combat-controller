package ch.mzh.cc;

import ch.mzh.cc.model.Entity;

import java.util.ArrayList;
import java.util.List;

public class GameEventManager {

  /*
  Frontend Input → Backend Game Logic → Backend Events → Backend Systems
     ↓                                      ↓
  Frontend Rendering ← Backend State    Frontend UI Updates
  */

  private final List<GameEventListener> listeners = new ArrayList<>();

  public void addListener(GameEventListener listener) {
    listeners.add(listener);
  }

  public void removeListener(GameEventListener listener) {
    listeners.remove(listener);
  }

  public void fireEntityMovedSuccessful(Entity entity, Position2D oldPos, Position2D newPos) {
    listeners.forEach(l -> l.onEntityMoved(entity, oldPos, newPos));
  }

  public void fireEntityMovedFailed(Entity entity, Position2D oldPos, Position2D newPos, String failureReason) {
    listeners.forEach(l -> l.onEntityMovedFailed(entity, oldPos, newPos, failureReason));
  }

  public void fireEntitySelected(Entity entity) {
    listeners.forEach(l -> l.onEntitySelected(entity));
  }

  public void fireEntityDestroyed(Entity destroyedEntity) {
    listeners.forEach(l -> l.onEntityDestroyed(destroyedEntity));
  }

  public void fireEntityFired(Entity shooter, Position2D targetPosition, boolean hit) {
    // Notify listeners
  }

  public void fireTurnEnded(int playerId) {
    listeners.forEach(l -> l.onTurnEnded(playerId));
  }

  public void fireEntityDeselected() {
    listeners.forEach(l -> l.onEntityDeselected());
  }

  public void fireFuelConsumed(Entity entity, int amount) {
    listeners.forEach(l -> l.onFuelConsumed(entity, amount));
  }

}

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

  public void fireEntityMoved(Entity entity, Position2D oldPos, Position2D newPos) {
    listeners.forEach(l -> l.onEntityMoved(entity, oldPos, newPos));
  }

  public void fireEntitySelected(Entity entity) {
    listeners.forEach(l -> l.onEntitySelected(entity));
  }

  public void fireEntityDeselected() {
    listeners.forEach(l -> l.onEntityDeselected());
  }

  public void fireFuelConsumed(Entity entity, int amount) {
    listeners.forEach(l -> l.onFuelConsumed(entity, amount));
  }
}

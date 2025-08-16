package ch.mzh.cc.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnimationManager {
  private final List<ShotAnimation> activeAnimations;

  public AnimationManager() {
    this.activeAnimations = new ArrayList<>();
  }

  public void addShotAnimation(ShotAnimation animation) {
    activeAnimations.add(animation);
  }

  public void update(float deltaTime) {
    Iterator<ShotAnimation> iterator = activeAnimations.iterator();
    while (iterator.hasNext()) {
      ShotAnimation animation = iterator.next();
      animation.update(deltaTime);

      if (animation.isComplete()) {
        iterator.remove();
      }
    }
  }

  public List<ShotAnimation> getActiveAnimations() {
    return new ArrayList<>(activeAnimations);
  }

  public boolean hasActiveAnimations() {
    return !activeAnimations.isEmpty();
  }

  public void clear() {
    activeAnimations.clear();
  }
}

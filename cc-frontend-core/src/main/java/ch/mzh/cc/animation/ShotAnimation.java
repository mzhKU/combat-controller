package ch.mzh.cc.animation;

import ch.mzh.cc.Position2D;
import com.badlogic.gdx.graphics.Color;

public class ShotAnimation {
  private final Position2D startPosition;
  private final Position2D targetPosition;
  private final float duration; // Animation duration in seconds
  private final Color projectileColor;
  private final float projectileSize;

  private float elapsedTime;
  private boolean isComplete;
  private boolean hasImpacted;

  // Animation phases
  private static final float MUZZLE_FLASH_DURATION = 0.1f;
  private static final float PROJECTILE_FLIGHT_RATIO = 0.8f; // 80% of duration for flight
  private static final float EXPLOSION_DURATION = 0.3f;

  public ShotAnimation(Position2D startPosition, Position2D targetPosition, float duration) {
    this.startPosition = new Position2D(startPosition.getX(), startPosition.getY());
    this.targetPosition = new Position2D(targetPosition.getX(), targetPosition.getY());
    this.duration = duration;
    this.projectileColor = new Color(1.0f, 1.0f, 0.6f, 1.0f); // Yellow-white projectile
    this.projectileSize = 3.0f;
    this.elapsedTime = 0;
    this.isComplete = false;
    this.hasImpacted = false;
  }

  public void update(float deltaTime) {
    if (isComplete) return;

    elapsedTime += deltaTime;

    // Check if projectile has reached target
    float flightDuration = duration * PROJECTILE_FLIGHT_RATIO;
    if (!hasImpacted && elapsedTime >= flightDuration) {
      hasImpacted = true;
    }

    // Check if animation is complete
    if (elapsedTime >= duration) {
      isComplete = true;
    }
  }

  public boolean isComplete() {
    return isComplete;
  }

  public boolean showMuzzleFlash() {
    return elapsedTime <= MUZZLE_FLASH_DURATION;
  }

  public boolean showProjectile() {
    float flightDuration = duration * PROJECTILE_FLIGHT_RATIO;
    return elapsedTime <= flightDuration && elapsedTime > MUZZLE_FLASH_DURATION;
  }

  public boolean showExplosion() {
    float flightDuration = duration * PROJECTILE_FLIGHT_RATIO;
    return elapsedTime > flightDuration && !isComplete;
  }

  // Calculate current projectile position using parabolic trajectory
  public Position2D getCurrentProjectilePosition() {
    if (!showProjectile()) return null;

    float flightDuration = duration * PROJECTILE_FLIGHT_RATIO;
    float flightProgress = (elapsedTime - MUZZLE_FLASH_DURATION) / (flightDuration - MUZZLE_FLASH_DURATION);
    flightProgress = Math.max(0, Math.min(1, flightProgress));

    // Linear interpolation between start and target
    float currentX = startPosition.getX() + (targetPosition.getX() - startPosition.getX()) * flightProgress;
    float currentY = startPosition.getY() + (targetPosition.getY() - startPosition.getY()) * flightProgress;

    return new Position2D((int)currentX, (int)currentY);
  }

  public float getProjectileWorldX(int tileSize) {
    Position2D pos = getCurrentProjectilePosition();
    if (pos == null) return 0;

    float flightDuration = duration * PROJECTILE_FLIGHT_RATIO;
    float flightProgress = (elapsedTime - MUZZLE_FLASH_DURATION) / (flightDuration - MUZZLE_FLASH_DURATION);
    flightProgress = Math.max(0, Math.min(1, flightProgress));

    float startWorldX = startPosition.getX() * tileSize + tileSize / 2f;
    float targetWorldX = targetPosition.getX() * tileSize + tileSize / 2f;

    return startWorldX + (targetWorldX - startWorldX) * flightProgress;
  }

  public float getProjectileWorldY(int tileSize) {
    Position2D pos = getCurrentProjectilePosition();
    if (pos == null) return 0;

    float flightDuration = duration * PROJECTILE_FLIGHT_RATIO;
    float flightProgress = (elapsedTime - MUZZLE_FLASH_DURATION) / (flightDuration - MUZZLE_FLASH_DURATION);
    flightProgress = Math.max(0, Math.min(1, flightProgress));

    float startWorldY = startPosition.getY() * tileSize + tileSize / 2f;
    float targetWorldY = targetPosition.getY() * tileSize + tileSize / 2f;

    // Add parabolic arc - higher arc for longer distances
    float distance = Math.abs(targetPosition.getX() - startPosition.getX()) +
            Math.abs(targetPosition.getY() - startPosition.getY());
    float arcHeight = Math.min(distance * tileSize * 0.3f, tileSize * 8); // Max arc height

    float baseY = startWorldY + (targetWorldY - startWorldY) * flightProgress;
    float arcY = arcHeight * 4 * flightProgress * (1 - flightProgress); // Parabolic curve

    return baseY + arcY;
  }

  public float getMuzzleFlashAlpha() {
    if (!showMuzzleFlash()) return 0;
    return 1.0f - (elapsedTime / MUZZLE_FLASH_DURATION);
  }

  public float getExplosionRadius(int tileSize) {
    if (!showExplosion()) return 0;

    float flightDuration = duration * PROJECTILE_FLIGHT_RATIO;
    float explosionProgress = (elapsedTime - flightDuration) / EXPLOSION_DURATION;
    explosionProgress = Math.max(0, Math.min(1, explosionProgress));

    // Explosion grows then fades
    float maxRadius = tileSize * 0.8f;
    if (explosionProgress <= 0.3f) {
      // Growing phase
      return maxRadius * (explosionProgress / 0.3f);
    } else {
      // Fading phase
      return maxRadius * (1.0f - (explosionProgress - 0.3f) / 0.7f);
    }
  }

  public float getExplosionAlpha() {
    if (!showExplosion()) return 0;

    float flightDuration = duration * PROJECTILE_FLIGHT_RATIO;
    float explosionProgress = (elapsedTime - flightDuration) / EXPLOSION_DURATION;
    explosionProgress = Math.max(0, Math.min(1, explosionProgress));

    if (explosionProgress <= 0.3f) {
      return 1.0f; // Full opacity during growth
    } else {
      return 1.0f - (explosionProgress - 0.3f) / 0.7f; // Fade out
    }
  }

  // Getters
  public Position2D getStartPosition() { return startPosition; }
  public Position2D getTargetPosition() { return targetPosition; }
  public Color getProjectileColor() { return projectileColor; }
  public float getProjectileSize() { return projectileSize; }
}
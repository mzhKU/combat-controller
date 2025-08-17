package ch.mzh.cc.components;

public class HealthComponent implements Component {
  private int health;

  public HealthComponent(int health) {
    this.health = health;
  }

  public boolean destroyedIfHealthBelowZero(int damage) {
    health -= damage;
    return health <= 0; // returns true if destroyed
  }
}

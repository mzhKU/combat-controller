package ch.mzh.cc.components;

public class HealthComponent implements Component {
  private int currentHealth;
  private int maxHealth = 10;

  public boolean takeDamage(int damage) {
    currentHealth -= damage;
    return currentHealth <= 0; // returns true if destroyed
  }
}

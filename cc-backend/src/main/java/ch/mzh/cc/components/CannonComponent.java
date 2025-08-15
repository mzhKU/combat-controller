package ch.mzh.cc.components;

public class CannonComponent implements Component {

  private int magazine;
  private final int range;
  private final int damage;

  public CannonComponent(int range, int damage, int magazine) {
    this.range = range;
    this.damage = damage;
    this.magazine = magazine;
  }

  public boolean canFire() {
    return magazine > 0;
  }

  public void fire() {
      magazine--;
  }

  public int getRange() {
    return this.range;
  }

  public int getDamage() {
    return this.damage;
  }

  public int getMagazine() {
    return this.magazine;
  }

}

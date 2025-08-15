package ch.mzh.cc.components;

public class CannonComponent implements Component {

  private int magazine;
  private int range = 10;
  private int damage = 1;

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

}

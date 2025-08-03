package ch.mzh.cc.model;

public enum TerrainType {
  OPEN_GROUND(1),    // 1 fuel per move
  ROUGH_TERRAIN(2),  // 2 fuel per move
  OBSTACLE(999);     // Impassable

  private final int fuelCost;

  TerrainType(int fuelCost) {
    this.fuelCost = fuelCost;
  }

  public int getFuelCost() { return fuelCost; }
  public boolean isPassable() { return fuelCost < 999; }
}

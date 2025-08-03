package ch.mzh.cc;

public class Position2D {
  private int gridX;
  private int gridY;

  public Position2D(int gridX, int gridY) {
    this.gridX = gridX;
    this.gridY = gridY;
  }

  public int getX() { return this.gridX; }
  public int getY() { return this.gridY; }

  public void setX(int x) { this.gridX = x; }
  public void setY(int y) { this.gridY = y; }
}

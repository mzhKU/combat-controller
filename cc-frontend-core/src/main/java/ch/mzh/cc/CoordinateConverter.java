package ch.mzh.cc;

import com.badlogic.gdx.math.Vector2;

public class CoordinateConverter {
  private final int tileSize;

  public CoordinateConverter(int tileSize) {
    this.tileSize = tileSize;
  }

  public Vector2 gridToWorld(Position2D gridPos) {
    return new Vector2(gridPos.getX() * tileSize, gridPos.getY() * tileSize);
  }

  public Position2D worldToGrid(float worldX, float worldY) {
    return new Position2D((int)(worldX / tileSize), (int)(worldY / tileSize));
  }
}

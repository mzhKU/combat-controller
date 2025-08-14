package ch.mzh.cc;

import ch.mzh.cc.model.TerrainType;

public class Grid {
  private final int width;
  private final int height;
  private final int tileSize;
  private final TerrainType[][] terrain;
  private Position2D[] gridPositions;

  public Grid(int width, int height, int tileSize) {
    this.width = width;
    this.height = height;
    this.tileSize = tileSize;
    this.terrain = new TerrainType[width][height];

    initializeTerrain();
    initializeGridPositions();
  }

  public boolean isInvalidPosition(Position2D position) {
    return position.getX() < 0 || position.getX() >= width || position.getY() < 0 || position.getY() >= height;
  }

  public TerrainType getTerrainAt(Position2D position) {
    if (isInvalidPosition(position)) return TerrainType.OBSTACLE;
    return terrain[position.getX()][position.getY()];
  }

  public int getWidth() { return width; }

  public int getHeight() { return height; }

  public int getTileSize() { return tileSize; }

  private void initializeTerrain() {
    // Same as your current implementation
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        terrain[x][y] = TerrainType.OPEN_GROUND;
      }
    }

    // Add some rough terrain patches
    for (int i = 0; i < width*100-20; i++) {
      int x = (int)(Math.random() * width);
      int y = (int)(Math.random() * height);
      if (x > 20 && x < width - 20 && y > 20 && y < height - 20) {
        terrain[x][y] = TerrainType.ROUGH_TERRAIN;
      }
    }

    // Add some obstacles
    for (int i = 0; i < width*100-40; i++) {
      int x = (int)(Math.random() * width);
      int y = (int)(Math.random() * height);
      if (x > 20 && x < width - 20 && y > 20 && y < height - 20) {
        terrain[x][y] = TerrainType.OBSTACLE;
      }
    }
  }

  private void initializeGridPositions() {
    gridPositions = new Position2D[width*height];
    int j = 0;
    for(int i = 0; i < width*height; i++) {
      int posX = i % width;
      gridPositions[i] = new Position2D(posX, j);
      if (posX == width - 1) {
        j++;
      }
    }
  }

  public static int calculateManhattanDistance(Position2D p1, Position2D p2) {
    return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
  }
}

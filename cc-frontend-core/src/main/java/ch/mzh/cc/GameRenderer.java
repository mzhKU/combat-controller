package ch.mzh.cc;

import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.TerrainType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class GameRenderer {
  private final ShapeRenderer shapeRenderer;
  private final OrthographicCamera camera;
  private final Grid grid;

  public GameRenderer(OrthographicCamera camera, Grid grid) {
    this.shapeRenderer = new ShapeRenderer();
    this.camera = camera;
    this.grid = grid;
  }

  public void render(List<Entity> entities, Entity selectedEntity) {
    shapeRenderer.setProjectionMatrix(camera.combined);

    // Render grid and terrain
    renderGrid();

    // Render entities
    renderEntities(entities, selectedEntity);

    // Render selection indicator if there's a selected entity
    if (selectedEntity != null) {
      renderSelectionIndicator(selectedEntity);
    }
  }

  private void renderEntities(List<Entity> entities, Entity selectedEntity) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    for (Entity entity : entities) {
      if (!entity.isActive()) continue;

      Position2D gridPos = grid.gridToWorld(entity.getPosition().getX(), entity.getPosition().getY());
      Vector2 worldPos = new Vector2(gridPos.getX(), gridPos.getY());
      boolean isSelected = (entity == selectedEntity);

      // Set color and size based on entity type
      switch (entity.getType()) {
        case CANNON:
          if (isSelected) {
            shapeRenderer.setColor(0.3f, 0.3f, 1.0f, 1.0f); // Brighter blue when selected
          } else {
            shapeRenderer.setColor(0.1f, 0.1f, 0.8f, 1.0f); // Blue
          }
          shapeRenderer.rect(worldPos.x + 4, worldPos.y + 4, 24, 24);
          break;
        case TROOP:
          if (isSelected) {
            shapeRenderer.setColor(0.3f, 1.0f, 0.3f, 1.0f); // Brighter green when selected
          } else {
            shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 1.0f); // Green
          }
          shapeRenderer.circle(worldPos.x + 16, worldPos.y + 16, 8);
          break;
        case BASE:
          if (isSelected) {
            shapeRenderer.setColor(1.0f, 1.0f, 0.3f, 1.0f); // Brighter yellow when selected
          } else {
            shapeRenderer.setColor(0.8f, 0.8f, 0.1f, 1.0f); // Yellow
          }
          shapeRenderer.rect(worldPos.x + 2, worldPos.y + 2, 28, 28);
          break;
        case SUPPLY_TRUCK:
          if (isSelected) {
            shapeRenderer.setColor(1.0f, 0.6f, 0.3f, 1.0f); // Brighter orange when selected
          } else {
            shapeRenderer.setColor(0.8f, 0.4f, 0.1f, 1.0f); // Orange
          }
          shapeRenderer.rect(worldPos.x + 6, worldPos.y + 6, 20, 20);
          break;
      }
    }

    shapeRenderer.end();
  }

  private void renderGrid() {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // Calculate visible grid bounds for optimization, convert camera bounds to backend grid coordinates
    Position2D bottomLeft = grid.worldToGrid(camera.position.x - camera.viewportWidth * camera.zoom / 2, camera.position.y - camera.viewportHeight * camera.zoom / 2);
    Position2D   topRight = grid.worldToGrid(camera.position.x + camera.viewportWidth * camera.zoom / 2, camera.position.y + camera.viewportHeight * camera.zoom / 2);

    int startX = Math.max(0, bottomLeft.getX() - 1);
    int   endX = Math.min(grid.getWidth(), topRight.getX() + 1);

    int startY = Math.max(0, bottomLeft.getY() - 1);
    int   endY = Math.min(grid.getHeight(), topRight.getY() + 1);

    // Render terrain tiles
    for (int x = startX; x < endX; x++) {
      for (int y = startY; y < endY; y++) {
        TerrainType terrain = grid.getTerrainAt(new Position2D(x, y));

        // Convert backend grid coordinates to LibGDX world coordinates for rendering
        Position2D worldPosBackend = grid.gridToWorld(x, y);
        Vector2 worldPos = new Vector2(worldPosBackend.getX(), worldPosBackend.getY());

        // Set color based on terrain type
        switch (terrain) {
          case OPEN_GROUND:
            shapeRenderer.setColor(0.4f, 0.6f, 0.3f, 1.0f); // Green
            break;
          case ROUGH_TERRAIN:
            shapeRenderer.setColor(0.6f, 0.5f, 0.3f, 1.0f); // Brown
            break;
          case OBSTACLE:
            shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1.0f); // Gray
            break;
        }

        shapeRenderer.rect(worldPos.x, worldPos.y,
                grid.getTileSize() - 1, grid.getTileSize() - 1);
      }
    }

    shapeRenderer.end();

    // Render grid lines...
    renderGridLines(startX, endX, startY, endY);
  }

  private void renderGridLines(int startX, int endX, int startY, int endY) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);

    // Only render grid lines when zoomed in enough
    if (camera.zoom < 1.0f) {
      // Vertical lines
      for (int x = startX; x <= endX; x++) {
        Position2D worldPos = grid.gridToWorld(x, startY);
        Position2D worldPosEnd = grid.gridToWorld(x, endY);
        shapeRenderer.line(worldPos.getX(), worldPos.getY(),
                worldPos.getX(), worldPosEnd.getY());
      }

      // Horizontal lines
      for (int y = startY; y <= endY; y++) {
        Position2D worldPos = grid.gridToWorld(startX, y);
        Position2D worldPosEnd = grid.gridToWorld(endX, y);
        shapeRenderer.line(worldPos.getX(), worldPos.getY(),
                worldPosEnd.getX(), worldPos.getY());
      }
    }

    shapeRenderer.end();
  }

  private void renderSelectionIndicator(Entity selectedEntity) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f); // White selection border

    Position2D gridPos = grid.gridToWorld(selectedEntity.getPosition().getX(), selectedEntity.getPosition().getY());
    Vector2 worldPos = new Vector2(gridPos.getX(), gridPos.getY());

    // Draw selection border around the tile
    shapeRenderer.rect(worldPos.x, worldPos.y, grid.getTileSize(), grid.getTileSize());

    // Draw corner markers for better visibility
    int cornerSize = 6;
    int tileSize = grid.getTileSize();

    // Top-left corner
    shapeRenderer.line(worldPos.x, worldPos.y + tileSize, worldPos.x + cornerSize, worldPos.y + tileSize);
    shapeRenderer.line(worldPos.x, worldPos.y + tileSize, worldPos.x, worldPos.y + tileSize - cornerSize);

    // Top-right corner
    shapeRenderer.line(worldPos.x + tileSize - cornerSize, worldPos.y + tileSize, worldPos.x + tileSize, worldPos.y + tileSize);
    shapeRenderer.line(worldPos.x + tileSize, worldPos.y + tileSize, worldPos.x + tileSize, worldPos.y + tileSize - cornerSize);

    // Bottom-left corner
    shapeRenderer.line(worldPos.x, worldPos.y, worldPos.x + cornerSize, worldPos.y);
    shapeRenderer.line(worldPos.x, worldPos.y, worldPos.x, worldPos.y + cornerSize);

    // Bottom-right corner
    shapeRenderer.line(worldPos.x + tileSize - cornerSize, worldPos.y, worldPos.x + tileSize, worldPos.y);
    shapeRenderer.line(worldPos.x + tileSize, worldPos.y, worldPos.x + tileSize, worldPos.y + cornerSize);

    shapeRenderer.end();
  }

}
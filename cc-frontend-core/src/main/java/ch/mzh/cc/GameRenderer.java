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
  private final GameCore gameCore;
  private final CoordinateConverter coordinateConverter;

  public GameRenderer(OrthographicCamera camera, GameCore gameCore, CoordinateConverter coordinateConverter) {
    this.shapeRenderer = new ShapeRenderer();

    this.camera = camera;
    this.gameCore = gameCore;
    this.coordinateConverter = coordinateConverter;
  }

  public void render(List<Entity> entities, Entity selectedEntity, CommandMode commandMode) {
    shapeRenderer.setProjectionMatrix(camera.combined);

    renderGrid();
    renderEntities(entities, selectedEntity);

    if (selectedEntity != null) {
      renderSelectionIndicator(selectedEntity);
      renderActionPreview(selectedEntity, commandMode); // Preview fuel- / fire-range
    }

    renderModeIndicator(commandMode); // Show active mode
  }

  private void renderEntities(List<Entity> entities, Entity selectedEntity) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    for (Entity entity : entities) {
      if (!entity.isActive()) continue;

      Vector2 worldPos = coordinateConverter.gridToWorld(entity.getPosition());
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
    Grid grid = gameCore.getGrid();
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // Calculate visible grid bounds for optimization, convert camera bounds to backend grid coordinates
    Position2D bottomLeft = coordinateConverter.worldToGrid(camera.position.x - camera.viewportWidth * camera.zoom / 2, camera.position.y - camera.viewportHeight * camera.zoom / 2);
    Position2D topRight = coordinateConverter.worldToGrid(camera.position.x + camera.viewportWidth * camera.zoom / 2, camera.position.y + camera.viewportHeight * camera.zoom / 2);

    int startX = Math.max(0,               bottomLeft.getX() - 1);
    int   endX = Math.min(grid.getWidth(),   topRight.getX() + 1);

    int startY = Math.max(0,                bottomLeft.getY() - 1);
    int   endY = Math.min(grid.getHeight(),   topRight.getY() + 1);

    // Render terrain tiles
    for (int x = startX; x < endX; x++) {
      for (int y = startY; y < endY; y++) {
        Position2D thisTile = new Position2D(x, y);
        TerrainType terrain = grid.getTerrainAt(thisTile);

        // Convert backend grid coordinates to LibGDX world coordinates for rendering
        Vector2 worldPos = coordinateConverter.gridToWorld(thisTile);

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

        shapeRenderer.rect(worldPos.x, worldPos.y, grid.getTileSize() - 1, grid.getTileSize() - 1);
      }
    }
    shapeRenderer.end();

    renderGridLines();
  }

  private void renderGridLines() {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
    shapeRenderer.end();
  }

  private void renderSelectionIndicator(Entity selectedEntity) {
    Grid grid = gameCore.getGrid();
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f); // White selection border

    Vector2 worldPos = coordinateConverter.gridToWorld(selectedEntity.getPosition());

    drawSelectionBorderAroundTile(worldPos, grid.getTileSize());
    drawCornerMarkers(worldPos, grid.getTileSize());

    shapeRenderer.end();
  }

  private void drawCornerMarkers(Vector2 worldPos, int tileSize) {
    int cornerSize = 6;

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

  }

  private void drawSelectionBorderAroundTile(Vector2 worldPos, int tileSize) {
    shapeRenderer.rect(worldPos.x, worldPos.y, tileSize, tileSize);
  }

  private void renderModeIndicator(CommandMode mode) {
    // Simple text rendering - you might want to use a proper font/UI library
    // For now, just change the selection color or add a visual indicator
  }

  private void renderActionPreview(Entity selected, CommandMode mode) {
    // Show movement range or fire range based on mode
    // This is optional for first implementation
  }
}
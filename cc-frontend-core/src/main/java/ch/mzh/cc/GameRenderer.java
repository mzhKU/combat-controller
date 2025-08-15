package ch.mzh.cc;

import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.TerrainType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line;

public class GameRenderer {
  private final ShapeRenderer shapeRenderer;
  private final OrthographicCamera camera;
  private final GameCore gameCore;
  private final CoordinateConverter coordinateConverter;

  private static final float BASE_SIZE_RATIO = 0.9f;      // 9px for 10px tile
  private static final float CANNON_SIZE_RATIO = 0.8f;    // 8px for 10px tile
  private static final float SUPPLY_TRUCK_SIZE_RATIO = 0.6f; // 6px for 10px tile
  private static final float TROOP_SIZE_RATIO = 0.6f;

  private static final float BUTTON_WIDTH = 80f;
  private static final float BUTTON_HEIGHT = 30f;
  private static final float BUTTON_MARGIN = 10f;

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
    renderUI();
  }

  public boolean isEndTurnButtonClicked(float worldX, float worldY) {
    float buttonX = camera.position.x + camera.viewportWidth * camera.zoom / 2 - BUTTON_WIDTH - BUTTON_MARGIN;
    float buttonY = camera.position.y + camera.viewportHeight * camera.zoom / 2 - BUTTON_HEIGHT - BUTTON_MARGIN;

    return worldX >= buttonX && worldX <= buttonX + BUTTON_WIDTH &&
            worldY >= buttonY && worldY <= buttonY + BUTTON_HEIGHT;
  }

  private void renderEntities(List<Entity> entities, Entity selectedEntity) {
    shapeRenderer.begin(Filled);

    int tileSize = gameCore.getGrid().getTileSize();

    for (Entity entity : entities) {
      if (!entity.isActive()) continue;

      Vector2 worldPos = coordinateConverter.gridToWorld(entity.getPosition());
      boolean isSelected = (entity == selectedEntity);
      int playerId = entity.getPlayerId();

      // Calculate sizes and positions based on tile size
      float unitSize;
      float offset;

      // Set color and size based on entity type and player
      switch (entity.getType()) {
        case CANNON:
          unitSize = tileSize * CANNON_SIZE_RATIO;
          offset = (tileSize - unitSize) / 2;
          if (playerId == 1) {
            if (isSelected) {
              shapeRenderer.setColor(0.4f, 0.4f, 1.0f, 1.0f); // Brighter blue when selected
            } else {
              shapeRenderer.setColor(0.0f, 0.0f, 1.0f, 1.0f); // Normal blue
            }
          } else { // Player 2
            if (isSelected) {
              shapeRenderer.setColor(1.0f, 0.4f, 0.4f, 1.0f); // Brighter red when selected
            } else {
              shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f); // Normal red
            }
          }
          shapeRenderer.rect(worldPos.x + offset, worldPos.y + offset, unitSize, unitSize);
          break;
        case TROOP:
          unitSize = tileSize * TROOP_SIZE_RATIO;
          if (playerId == 1) {
            if (isSelected) {
              shapeRenderer.setColor(0.4f, 0.8f, 1.0f, 1.0f); // Brighter azure when selected
            } else {
              shapeRenderer.setColor(0.0f, 0.7f, 1.0f, 1.0f); // Azure
            }
          } else { // Player 2
            if (isSelected) {
              shapeRenderer.setColor(1.0f, 0.6f, 0.4f, 1.0f); // Brighter orange-red when selected
            } else {
              shapeRenderer.setColor(1.0f, 0.4f, 0.0f, 1.0f); // Orange-red
            }
          }
          shapeRenderer.circle(worldPos.x + tileSize/2f, worldPos.y + tileSize/2f, unitSize/2f);
          break;
        case BASE:
          unitSize = tileSize * BASE_SIZE_RATIO;
          offset = (tileSize - unitSize) / 2;
          if (playerId == 1) {
            if (isSelected) {
              shapeRenderer.setColor(0.2f, 0.2f, 0.8f, 1.0f); // Brighter dark blue when selected
            } else {
              shapeRenderer.setColor(0.0f, 0.0f, 0.6f, 1.0f); // Dark blue
            }
          } else { // Player 2
            if (isSelected) {
              shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1.0f); // Brighter dark red when selected
            } else {
              shapeRenderer.setColor(0.6f, 0.0f, 0.0f, 1.0f); // Dark red
            }
          }
          shapeRenderer.rect(worldPos.x + offset, worldPos.y + offset, unitSize, unitSize);
          break;
        case SUPPLY_TRUCK:
          unitSize = tileSize * SUPPLY_TRUCK_SIZE_RATIO;
          offset = (tileSize - unitSize) / 2;
          if (playerId == 1) {
            if (isSelected) {
              shapeRenderer.setColor(0.7f, 0.9f, 1.0f, 1.0f); // Brighter light blue when selected
            } else {
              shapeRenderer.setColor(0.5f, 0.8f, 1.0f, 1.0f); // Light blue
            }
          } else { // Player 2
            if (isSelected) {
              shapeRenderer.setColor(1.0f, 0.7f, 0.7f, 1.0f); // Brighter light red when selected
            } else {
              shapeRenderer.setColor(1.0f, 0.5f, 0.5f, 1.0f); // Light red
            }
          }
          shapeRenderer.rect(worldPos.x + offset, worldPos.y + offset, unitSize, unitSize);
          break;
      }
    }

    shapeRenderer.end();
  }

  private void renderGrid() {
    Grid grid = gameCore.getGrid();
    shapeRenderer.begin(Filled);

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
    shapeRenderer.begin(Line);
    shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
    shapeRenderer.end();
  }

  private void renderSelectionIndicator(Entity selectedEntity) {
    Grid grid = gameCore.getGrid();
    shapeRenderer.begin(Line);
    shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f); // White selection border

    Vector2 worldPos = coordinateConverter.gridToWorld(selectedEntity.getPosition());

    drawSelectionBorderAroundTile(worldPos, grid.getTileSize());
    drawCornerMarkers(worldPos, grid.getTileSize());

    shapeRenderer.end();
  }

  private void drawCornerMarkers(Vector2 worldPos, int tileSize) {
    int cornerSize = Math.max(2, tileSize / 5); // Scale corner size with tile size

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

  private void renderUI() {
    shapeRenderer.begin(Filled);

    // Render current player indicator
    renderPlayerIndicator();

    // Render end turn button
    renderEndTurnButton();

    shapeRenderer.end();
  }

  private void renderPlayerIndicator() {
    int currentPlayer = gameCore.getGameState().getCurrentPlayerId();

    // Position at top-left of screen
    float x = camera.position.x - camera.viewportWidth * camera.zoom / 2 + BUTTON_MARGIN;
    float y = camera.position.y + camera.viewportHeight * camera.zoom / 2 - BUTTON_MARGIN - 20;

    // Set color based on current player
    if (currentPlayer == 1) {
      shapeRenderer.setColor(0.0f, 0.0f, 1.0f, 0.8f); // Blue for player 1
    } else {
      shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 0.8f); // Red for player 2
    }

    shapeRenderer.rect(x, y, 100, 20);
  }

  private void renderEndTurnButton() {
    // Position at top-right of screen
    float x = camera.position.x + camera.viewportWidth * camera.zoom / 2 - BUTTON_WIDTH - BUTTON_MARGIN;
    float y = camera.position.y + camera.viewportHeight * camera.zoom / 2 - BUTTON_HEIGHT - BUTTON_MARGIN;

    // Button background
    shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.8f);
    shapeRenderer.rect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

    // Button border
    shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    shapeRenderer.rect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
  }
}
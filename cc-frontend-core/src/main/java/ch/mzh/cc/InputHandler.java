package ch.mzh.cc;

import ch.mzh.cc.command.*;
import ch.mzh.cc.model.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import static ch.mzh.cc.CommandMode.FIRE;
import static ch.mzh.cc.CommandMode.MOVE;

public class InputHandler extends InputAdapter {

  /*
  Responsibilities:
  - InputHandler only handles input conversion to commands
  - GameCore only handles game logic
  - CommandProcessor handles command validation and execution
  */

  // TODO: The handle methods probably should return void

  private CommandMode forcedMode = null; // null = auto-detect
  private CommandMode commandMode = MOVE;

  private final OrthographicCamera camera;
  private final Vector3 mouseWorldPos;
  private final CoordinateConverter coordinateConverter;
  private final CommandProcessor commandProcessor;
  private final GameCore gameCore;
  private final GameRenderer gameRenderer;

  public InputHandler(OrthographicCamera camera, CoordinateConverter coordinateConverter, CommandProcessor commandProcessor, GameCore gameCore, GameRenderer gameRenderer) {
    this.camera = camera;
    this.coordinateConverter = coordinateConverter;
    this.mouseWorldPos = new Vector3();
    this.commandProcessor = commandProcessor;
    this.gameCore = gameCore;
    this.gameRenderer = gameRenderer;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    // Convert screen coordinates to world coordinates
    mouseWorldPos.set(screenX, screenY, 0);
    camera.unproject(mouseWorldPos);

    // Convert to grid position and update hover in renderer
    Position2D gridPos = coordinateConverter.worldToGrid(mouseWorldPos.x, mouseWorldPos.y);

    // Only update hover if within grid bounds
    if (!gameCore.getGrid().isInvalidPosition(gridPos)) {
      gameRenderer.setHoverPosition(gridPos);
    } else {
      gameRenderer.setHoverPosition(null);
    }

    return true;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    // Convert screen coordinates to world coordinates
    mouseWorldPos.set(screenX, screenY, 0);
    camera.unproject(mouseWorldPos);

    if (gameRenderer.isEndTurnButtonClicked(mouseWorldPos.x, mouseWorldPos.y)) {
      int currentPlayer = gameCore.getGameState().getCurrentPlayerId();
      commandProcessor.queueCommand(new EndTurnCommand(currentPlayer));
      commandProcessor.executeNextCommand();
      return true;
    }

    Position2D gridPositionFromScreen = coordinateConverter.worldToGrid(mouseWorldPos.x, mouseWorldPos.y);

    if (button == Input.Buttons.LEFT) {
      commandProcessor.queueCommand(new SelectEntityCommand(gridPositionFromScreen));
    } else if (button == Input.Buttons.RIGHT) {
      CommandMode activeMode = determineActiveMode(gridPositionFromScreen);
      Command command = switch (activeMode) {
        case MOVE -> new MoveEntityCommand(gridPositionFromScreen);
        case FIRE -> new FireCommand(gridPositionFromScreen);
      };
      commandProcessor.queueCommand(command);
    }
    commandProcessor.executeNextCommand();
    return true;
  }

  @Override
  public boolean keyDown(int keycode) {
    return switch (keycode) {
      case Input.Keys.M -> {
        setForcedMode(MOVE);
        yield true;
      }
      case Input.Keys.F -> {
        setForcedMode(FIRE);
        yield true;
      }
      case Input.Keys.TAB -> {
        toggleMode();
        yield true;
      }
      case Input.Keys.ESCAPE -> {
        setForcedMode(null); // Return to auto-detect
        yield true;
      }
      default -> false;
    };
  }

  public void setForcedMode(CommandMode mode) {
    this.forcedMode = mode;
    this.commandMode = mode != null ? mode : MOVE;
  }

  public CommandMode getCommandMode() {
    return commandMode;
  }

  private boolean isEnemy(Entity target, Entity selected) {
    return target.isEnemy(selected);
  }

  private CommandMode determineActiveMode(Position2D position) {
    if (forcedMode != null) return forcedMode;

    // Auto-detect based on context. Query GameCore for context, but don't execute anything.
    if (gameCore.noEntitySelected()) {
      return MOVE; // Default when nothing selected
    }

    Entity selected = gameCore.getSelectedEntity();
    return gameCore.getEntityManager().getEntityAt(position)
            .filter(target -> isEnemy(target, selected))
            .map(target -> FIRE)
            .orElse(MOVE);
  }

  private void toggleMode() {
    if (forcedMode == null) {
      setForcedMode(FIRE); // Start with fire when toggling from auto
    } else {
      setForcedMode(forcedMode == MOVE ? FIRE : MOVE);
    }
  }

}

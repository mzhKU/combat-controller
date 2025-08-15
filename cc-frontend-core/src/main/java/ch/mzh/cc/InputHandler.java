package ch.mzh.cc;

import ch.mzh.cc.command.Command;
import ch.mzh.cc.command.CommandProcessor;
import ch.mzh.cc.command.MoveEntityCommand;
import ch.mzh.cc.command.SelectEntityCommand;
import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.model.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class InputHandler extends InputAdapter {

  /*
  Responsibilities:
  - InputHandler only handles input conversion to commands
  - GameCore only handles game logic
  - CommandProcessor handles command validation and execution
  */

  // TODO: The handle methods probably should return void

  private final OrthographicCamera camera;
  private final Vector3 mouseWorldPos;
  private final CoordinateConverter coordinateConverter;
  private final CommandProcessor commandProcessor;

  public InputHandler(OrthographicCamera camera, CoordinateConverter coordinateConverter, CommandProcessor commandProcessor) {
    this.camera = camera;
    this.coordinateConverter = coordinateConverter;
    this.mouseWorldPos = new Vector3();
    this.commandProcessor = commandProcessor;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
      commandProcessor.queueCommand(new SelectEntityCommand(getGridPositionFromScreen(screenX, screenY)));
    } else if (button == Input.Buttons.RIGHT) {
      commandProcessor.queueCommand(new MoveEntityCommand(getGridPositionFromScreen(screenX, screenY)));
    }
    commandProcessor.executeNextCommand();
    return true;
  }

  private Position2D getGridPositionFromScreen(int screenX, int screenY) {
    mouseWorldPos.set(screenX, screenY, 0);
    camera.unproject(mouseWorldPos);
    return coordinateConverter.worldToGrid(mouseWorldPos.x, mouseWorldPos.y);
  }

}

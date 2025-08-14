package ch.mzh.cc;

import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.model.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class InputHandler extends InputAdapter {

  private final OrthographicCamera camera;
  private final GameCore gameCore;
  private final Vector3 mouseWorldPos;
  private final CoordinateConverter coordinateConverter;

  public InputHandler(OrthographicCamera camera, GameCore gameCore, CoordinateConverter coordinateConverter) {
    this.camera = camera;
    this.gameCore = gameCore;
    this.coordinateConverter = coordinateConverter;
    this.mouseWorldPos = new Vector3();
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
      return handleEntitySelection(screenX, screenY);
    } else if (button == Input.Buttons.RIGHT) {
      if (gameCore.isEntitySelected()) {
        return handleMovementCommandOfSelectedEntity(screenX, screenY);
      }
    }
    return false;
  }

  private boolean handleEntitySelection(int screenX, int screenY) {
    convertScreenToWorldCoordinates(screenX, screenY); // TODO: Why this?

    Position2D selectedGridPosition = coordinateConverter.worldToGrid(mouseWorldPos.x, mouseWorldPos.y);

    if (gameCore.getGrid().isInvalidPosition(selectedGridPosition)) {
      return false;
    }

    return gameCore.selectEntity(selectedGridPosition);
  }

  private boolean handleMovementCommandOfSelectedEntity(int screenX, int screenY) {
    // TODO: Clean up unit selection and movement of unit
    if (isImmobile(gameCore.getSelectedEntity())) return false;

    convertScreenToWorldCoordinates(screenX, screenY); // TODO: why this?

    Position2D targetPosition = coordinateConverter.worldToGrid(mouseWorldPos.x, mouseWorldPos.y);

    if (gameCore.getGrid().isInvalidPosition(targetPosition)) {
      // TODO: this should go into the FAILURE_REASON
      System.out.println("Invalid target position: (" + targetPosition.getX() + ", " + targetPosition.getY() + ")");
      return false;
    }

    Entity entityAtTarget = gameCore.getEntityManager().getEntityAt(targetPosition);
    if (entityAtTarget != null && entityAtTarget != gameCore.getSelectedEntity()) {
      // TODO: this should go into the FAILURE_REASON
      System.out.println("Cannot move to occupied position: " + entityAtTarget.getType() + " at (" + targetPosition.getX() + ", " + targetPosition.getY() + ")");
      return false;
    }

    // TODO: Should here be a return value
    // TODO: Should frontend or backend react to the return value?
    return gameCore.moveEntity(gameCore.getSelectedEntity(), targetPosition);
  }

  private void convertScreenToWorldCoordinates(int screenX, int screenY) {
    mouseWorldPos.set(screenX, screenY, 0);
    camera.unproject(mouseWorldPos);
  }

  // TODO: Move to entity.
  // TODO: Prevent NPE when right-click on empty grid with no entity selected
  private boolean isImmobile(Entity selectedEntity) {
    if (!selectedEntity.hasComponent(VehicleMovementComponent.class)) {
      System.out.println("This entity cannot move: " + selectedEntity.getName());
      return true;
    }
    return false;
  }

}

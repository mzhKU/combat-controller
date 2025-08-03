package ch.mzh.cc;

import ch.mzh.cc.components.FuelComponent;
import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.model.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class InputHandler extends InputAdapter implements Observable {

  private final OrthographicCamera camera;
  private final Grid grid;
  private final EntityManager entityManager;
  private final Vector3 mouseWorldPos;
  private final CoordinateConverter coordinateConverter;
  private final List<Observer> observers = new ArrayList<>(); // TODO: Initialize consistently

  private Entity selectedEntity;


  public InputHandler(OrthographicCamera camera, Grid grid, EntityManager entityManager, CoordinateConverter coordinateConverter) {
    this.camera = camera;
    this.grid = grid;
    this.entityManager = entityManager;
    this.coordinateConverter = coordinateConverter;
    this.mouseWorldPos = new Vector3();
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
      handleEntitySelection(screenX, screenY);
      return true;
    } else if (button == Input.Buttons.RIGHT) {
      handleMovementCommandOfSelectedEntity(screenX, screenY);
      return true;
    }
    return false;
  }

  @Override
  public void addObserver(Observer observer) {
    System.out.println("Adding observer: " + observer);
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }

  public Entity getSelectedEntity() {
    return selectedEntity;
  }

  private void handleEntitySelection(int screenX, int screenY) {
    convertScreenToWorldCoordinates(screenX, screenY); // TODO: Why this?

    Position2D selectedGridPosition = coordinateConverter.worldToGrid(mouseWorldPos.x, mouseWorldPos.y);

    if (grid.isInvalidPosition(selectedGridPosition)) {
      selectedEntity = null;
      return;
    }

    Entity clickedEntity = entityManager.getEntityAt(selectedGridPosition);
    updateEntityChangeListeners(clickedEntity);
  }

  private void handleMovementCommandOfSelectedEntity(int screenX, int screenY) {
    if (isImmobile(selectedEntity)) return;

    convertScreenToWorldCoordinates(screenX, screenY); // TODO: why this?

    Position2D targetPosition = coordinateConverter.worldToGrid(mouseWorldPos.x, mouseWorldPos.y);

    if (grid.isInvalidPosition(targetPosition)) {
      System.out.println("Invalid target position: (" + targetPosition.getX() + ", " + targetPosition.getY() + ")");
      return;
    }

    Entity entityAtTarget = entityManager.getEntityAt(targetPosition);
    if (entityAtTarget != null && entityAtTarget != selectedEntity) {
      System.out.println("Cannot move to occupied position: " + entityAtTarget.getType() + " at (" + targetPosition.getX() + ", " + targetPosition.getY() + ")");
      return;
    }

    VehicleMovementComponent movement = selectedEntity.getComponent(VehicleMovementComponent.class);
    boolean moved = movement.move(selectedEntity, targetPosition);

    if (moved) {
      printMovement(selectedEntity, targetPosition);
      observers.forEach(o -> o.onEntityMoved(selectedEntity));
    }
    else {
      System.out.println("Could not move."); // TODO: add reasons for not being able to move.
    }
  }

  private void updateEntityChangeListeners(Entity entity) {
    if (entity != null) {
      if (entity != selectedEntity) {
        selectedEntity = entity;
        observers.forEach(o -> o.onEntitySelected(selectedEntity));
      }
    } else {
      selectedEntity = null;
      observers.forEach(Observer::onEntityDeselected);
    }
  }

  private void convertScreenToWorldCoordinates(int screenX, int screenY) {
    mouseWorldPos.set(screenX, screenY, 0);
    camera.unproject(mouseWorldPos);
  }

  // TODO: Move to entity.
  // TODO: Prevent NPE when right-click on empty grid with no entity selected
  private boolean isImmobile(Entity selectedEntity) {
    if (!selectedEntity.hasComponent(VehicleMovementComponent.class))      {
      System.out.println("This entity cannot move: " + selectedEntity.getName());
      return true;
    }
    return false;
  }

  private void printMovement(Entity selectedEntity, Position2D targetPosition) {
    FuelComponent fuel = selectedEntity.getComponent(FuelComponent.class);
    System.out.println("Moved " + selectedEntity.getName() + " to (" + targetPosition.getX() + ", " + targetPosition.getY() + "), fuel used: " + fuel.getLastFuelUsage() + ", fuel remaining: " + fuel.getCurrentFuel() + ".");
  }
}

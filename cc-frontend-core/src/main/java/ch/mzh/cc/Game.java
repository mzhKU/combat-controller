package ch.mzh.cc;

import ch.mzh.cc.components.BaseSupplyComponent;
import ch.mzh.cc.components.Component;
import ch.mzh.cc.components.FuelComponent;
import ch.mzh.cc.components.FuelSystem;
import ch.mzh.cc.components.VehicleMovementComponent;
import ch.mzh.cc.components.VehicleSupplyComponent;
import ch.mzh.cc.model.Base;
import ch.mzh.cc.model.Cannon;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;
import ch.mzh.cc.model.SupplyTruck;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import static ch.mzh.cc.model.EntityType.SUPPLY_TRUCK;

public class Game extends ApplicationAdapter implements Observer {

  private OrthographicCamera camera;
  private GameRenderer gameRenderer;
  private Grid grid;
  private InputHandler inputHandler;
  private EntityManager entityManager;
  private FuelSystem fuelSystem;
  private SupplyRuleEngine supplyRuleEngine;
  private CoordinateConverter coordinateConverter;

  // Camera movement
  private static final float CAMERA_SPEED = 600f;
  private static final float ZOOM_SPEED = 1.0f;
  private static final float MIN_ZOOM = 0.1f;
  private static final float MAX_ZOOM = 2.0f;

  private static final int GRID_WIDTH = 40;
  private static final int GRID_HEIGHT = 40;
  private static final int TILE_SIZE = 32; // 32px tiles

  private static final int BASE_INIT_X = 10;
  private static final int BASE_INIT_Y = 10;

  @Override
  public void create() {
    initializeCamera();
    initializeGrid();
    initializeEntityManager();

    initializeCoordinateConverter();
    initializeGameRenderer();
    initializeInputHandler();

    createBase();
    createSupplyTruck();
    createCannon();
    createFuelSystem();
    createSupplyRuleEngine();
  }

  @Override
  public void render() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    calculateNewCameraPosition(deltaTime);
    camera.update();

    // Clear screen
    Gdx.gl.glClearColor(0.2f, 0.3f, 0.2f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    gameRenderer.render(entityManager.getEntities(), inputHandler.getSelectedEntity());
  }

  @Override
  public void onEntityMoved(Entity movedEntity) {
    supplyRuleEngine.processMovement(movedEntity, movedEntity.getPosition());
  }

  @Override
  public void onEntitySelected(Entity entity) {

  }

  @Override
  public void onEntityDeselected() {

  }

  private void calculateNewCameraPosition(float deltaTime) {
    // Camera movement
    Vector3 cameraMovement = new Vector3();

    if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      cameraMovement.x -= CAMERA_SPEED * deltaTime;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      cameraMovement.x += CAMERA_SPEED * deltaTime;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
      cameraMovement.y += CAMERA_SPEED * deltaTime;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
      cameraMovement.y -= CAMERA_SPEED * deltaTime;
    }

    camera.translate(cameraMovement);

    // Zoom controls
    if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
      camera.zoom = Math.min(camera.zoom + ZOOM_SPEED * deltaTime, MAX_ZOOM);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.E)) {
      camera.zoom = Math.max(camera.zoom - ZOOM_SPEED * deltaTime, MIN_ZOOM);
    }

    // Keep camera within bounds
    float halfWidth = camera.viewportWidth * camera.zoom / 2;
    float halfHeight = camera.viewportHeight * camera.zoom / 2;

    // camera.position.x = Math.max(halfWidth, Math.min(camera.position.x, gameGrid.getWorldWidth() - halfWidth));
    // camera.position.y = Math.max(halfHeight, Math.min(camera.position.y, gameGrid.getWorldHeight() - halfHeight));
  }

  private void initializeCamera() {
    camera = new OrthographicCamera();
    camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.zoom = 1.0f;
  }

  private void initializeGrid() {
    this.grid = new Grid(GRID_WIDTH, GRID_HEIGHT, TILE_SIZE);
  }

  private void initializeGameRenderer() {
    this.gameRenderer = new GameRenderer(camera, grid, coordinateConverter);
  }

  private void initializeEntityManager() {
    entityManager = new EntityManager();
  }

  private void initializeInputHandler() {
    inputHandler = new InputHandler(camera, grid, entityManager, coordinateConverter);
    Gdx.input.setInputProcessor(inputHandler);
    inputHandler.addObserver(this);
  }

  private void initializeCoordinateConverter() {
    this.coordinateConverter = new CoordinateConverter(TILE_SIZE);
  }

  private void createBase() {
    Component baseSupplyComponent = new BaseSupplyComponent(1);
    Base homeBase = new Base("Base 1", EntityType.BASE, new Position2D(BASE_INIT_X, BASE_INIT_Y));
    homeBase.addComponent(baseSupplyComponent);
    entityManager.addEntity(homeBase);
  }

  private void createSupplyTruck() {
    Component truckMovement = new VehicleMovementComponent();
    Component truckSupply = new VehicleSupplyComponent(1);
    Component truckFuel = new FuelComponent(100, 1);
    Entity supplyTruck = new SupplyTruck("Supply Truck 1", SUPPLY_TRUCK, new Position2D(8, 8));
    supplyTruck.addComponent(truckMovement);
    supplyTruck.addComponent(truckFuel);
    supplyTruck.addComponent(truckSupply);
    entityManager.addEntity(supplyTruck);
  }

  private void createCannon() {
    Component cannonMovement = new VehicleMovementComponent();
    Component cannonFuel = new FuelComponent(50, 2);
    Entity cannon = new Cannon("Cannon 1", EntityType.CANNON, new Position2D(15, 15));
    cannon.addComponent(cannonMovement);
    cannon.addComponent(cannonFuel);
    entityManager.addEntity(cannon);
  }

  private void createFuelSystem() {
    fuelSystem = new FuelSystem();
  }

  private void createSupplyRuleEngine() {
    this.supplyRuleEngine = new SupplyRuleEngine(entityManager, fuelSystem);
  }
}

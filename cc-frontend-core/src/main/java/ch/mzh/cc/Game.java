package ch.mzh.cc;

import ch.mzh.cc.command.CommandProcessor;
import ch.mzh.cc.components.*;
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

public class Game extends ApplicationAdapter implements GameEventListener {

  private OrthographicCamera camera;
  private GameCore gameCore;
  private GameRenderer gameRenderer;
  private InputHandler inputHandler;
  private CoordinateConverter coordinateConverter;
  private CommandProcessor commandProcessor;

  // Camera movement
  private static final float CAMERA_SPEED = 100f;
  private static final float ZOOM_SPEED = 1.0f;
  private static final float MIN_ZOOM = 0.5f;
  private static final float MAX_ZOOM = 3.0f;

  // TODO: Use same value here and in GameCore
  private static final int TILE_SIZE = 15; // 10px tiles

  private static final int CANNON_DAMAGE = 100;

  private static final int   BASE_PLAYER_1_INIT_X = 10;
  private static final int   BASE_PLAYER_1_INIT_Y = 10;
  private static final int  TRUCK_PLAYER_1_INIT_X =  8;
  private static final int  TRUCK_PLAYER_1_INIT_Y =  8;
  private static final int CANNON_PLAYER_1_INIT_X = 15;
  private static final int CANNON_PLAYER_1_INIT_Y = 15;

  private static final int   BASE_PLAYER_2_INIT_X = 10;
  private static final int   BASE_PLAYER_2_INIT_Y = 38;
  private static final int  TRUCK_PLAYER_2_INIT_X =  8;
  private static final int  TRUCK_PLAYER_2_INIT_Y = 32;
  private static final int CANNON_PLAYER_2_INIT_X = 15;
  private static final int CANNON_PLAYER_2_INIT_Y = 35;

  @Override
  public void create() {
    initializeCamera();
    initializeGameCore();

    gameCore.getGameSystem().initializeRandomStartingPlayer();

    initializeCoordinateConverter();
    initializeGameRenderer();
    initializeCommandProcessor();
    initializeInputHandler();

    // PLAYER 1
    createBase(               "Base 1", 1,   BASE_PLAYER_1_INIT_X,   BASE_PLAYER_1_INIT_Y);
    createSupplyTruck("Supply Truck 1", 1,  TRUCK_PLAYER_1_INIT_X,  TRUCK_PLAYER_1_INIT_Y);
    createCannon(           "Cannon 1", 1, CANNON_PLAYER_1_INIT_X, CANNON_PLAYER_1_INIT_Y);

    // PLAYER 2
    createBase(               "Base 2", 2,   BASE_PLAYER_2_INIT_X,   BASE_PLAYER_2_INIT_Y);
    createSupplyTruck("Supply Truck 2", 2,  TRUCK_PLAYER_2_INIT_X,  TRUCK_PLAYER_2_INIT_Y);
    createCannon(           "Cannon 2", 2, CANNON_PLAYER_2_INIT_X, CANNON_PLAYER_2_INIT_Y);

    setupInitialCameraView();
  }

  @Override
  public void render() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    if (!gameCore.getGameSystem().isGameOver()) {
      commandProcessor.executeAllCommands();
    }

    gameRenderer.updateAnimations(deltaTime);

    calculateNewCameraPosition(deltaTime);
    camera.update();
    clearScreen();

    gameRenderer.render(
            gameCore.getEntityManager().getEntities(),
            gameCore.getSelectedEntity(),
            inputHandler.getCommandMode()
    );

    if (gameCore.getGameSystem().isGameOver()) {
      gameRenderer.renderGameOverOverlay();
    }
  }

  @Override
  public void onEntityMoved(Entity movedEntity, Position2D oldPos, Position2D targetPosition) {
    // Update UI, animations...
    FuelComponent fuel = movedEntity.getComponent(FuelComponent.class);
    String entityName = movedEntity.getName();
    int entityX = targetPosition.getX();
    int entityY = targetPosition.getY();
    int lastFuelUsage = fuel.getLastFuelUsage();
    int currentFuel = fuel.getCurrentFuel();
    System.out.printf("Moved %s to (%d, %d), fuel used: %d, fuel remaining: %d.%n",
            entityName, entityX, entityY, lastFuelUsage, currentFuel);
  }

  @Override
  public void onEntityMovedFailed(Entity movedEntity, Position2D oldPos, Position2D targetPosition, String failureReason) {
    // TODO: add reasons for not being able to move.
    // TODO: Maybe use a `MovedEvent`, which tells if it was successful or not
    System.out.println("Could not move: " + failureReason);
  }

  @Override
  public void onEntitySelected(Entity entity) {
    // UI Animations etc
  }

  @Override
  public void onEntityDestroyed(Entity destroyedEntity) {
    System.out.println("Destroyed entity: " + destroyedEntity.getName());
  }

  @Override
  public void onTurnEnded(int playerId) {
    System.out.println("Player " + playerId + " ended their turn");
    System.out.println("Now it's Player " + gameCore.getGameSystem().getCurrentPlayerId() + "'s turn");

  }

  @Override
  public void onEntityFired(Entity shooter, Position2D targetPosition, boolean hit) {
    gameRenderer.addShotAnimation(shooter.getPosition(), targetPosition);
  }

  @Override
  public void onGameOver(int winnerId) {
    System.out.println("Winner is player: " + winnerId);
  }

  private void calculateNewCameraPosition(float deltaTime) {
    // Camera movement
    Vector3 cameraMovement = new Vector3();
    float adjustedSpeed = CAMERA_SPEED * camera.zoom;

    if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      cameraMovement.x -= adjustedSpeed * deltaTime;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      cameraMovement.x += adjustedSpeed * deltaTime;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
      cameraMovement.y += adjustedSpeed * deltaTime;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
      cameraMovement.y -= adjustedSpeed * deltaTime;
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
    Grid grid = gameCore.getGrid();
    float worldWidth = grid.getWidth() * TILE_SIZE;
    float worldHeight = grid.getHeight() * TILE_SIZE;

    float halfWidth = camera.viewportWidth * camera.zoom / 2;
    float halfHeight = camera.viewportHeight * camera.zoom / 2;

    // camera.position.x = Math.max(halfWidth, Math.min(camera.position.x, worldWidth - halfWidth));
    // camera.position.y = Math.max(halfHeight, Math.min(camera.position.y, worldHeight - halfHeight));
  }

  private void initializeCamera() {
    // Set up camera with vertical aspect ratio (mobile-like)
    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();

    camera = new OrthographicCamera();
    camera.setToOrtho(false, screenWidth, screenHeight);
  }

  private void initializeGameCore() {
    this.gameCore = new GameCore();
    gameCore.getGameEventManager().addListener(this);
  }

  private void initializeGameRenderer() {
    this.gameRenderer = new GameRenderer(camera, gameCore, coordinateConverter);
  }

  private void initializeInputHandler() {
    inputHandler = new InputHandler(camera, coordinateConverter, commandProcessor, gameCore, gameRenderer);
    Gdx.input.setInputProcessor(inputHandler);
  }

  private void initializeCoordinateConverter() {
    this.coordinateConverter = new CoordinateConverter(TILE_SIZE);
  }

  private void initializeCommandProcessor() {
    this.commandProcessor = new CommandProcessor(gameCore);
  }

  private void setupInitialCameraView() {
    // Calculate zoom level to show full grid
    Grid grid = gameCore.getGrid();
    float worldWidth = grid.getWidth() * TILE_SIZE;
    float worldHeight = grid.getHeight() * TILE_SIZE;

    float zoomX = camera.viewportWidth / worldWidth;
    float zoomY = camera.viewportHeight / worldHeight;

    // Use the larger zoom value to ensure entire grid is visible
    camera.zoom = Math.max(zoomX, zoomY);

    // Clamp zoom to our limits
    camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, camera.zoom));

    // Center camera on grid
    camera.position.x = worldWidth / 2;
    camera.position.y = worldHeight / 2;

    camera.update();
  }

  private void clearScreen() {
    Gdx.gl.glClearColor(0.2f, 0.3f, 0.2f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

  private void createBase(String entityName, int playerId, int x, int y) {
    Component baseSupplyComponent = new BaseSupplyComponent(1);
    Component healthComponent = new HealthComponent(100);
    Base homeBase = new Base(entityName, EntityType.BASE, new Position2D(x, y), playerId);
    homeBase.addComponent(baseSupplyComponent);
    homeBase.addComponent(healthComponent);
    gameCore.getEntityManager().addEntity(homeBase);
  }

  private void createSupplyTruck(String entityName, int playerId, int x, int y) {
    Component truckMovement = new VehicleMovementComponent();
    Component truckSupply = new VehicleSupplyComponent(1);
    Component truckFuel = new FuelComponent(100, 1);
    Component healthComponent = new HealthComponent(10);
    Entity supplyTruck = new SupplyTruck(entityName, SUPPLY_TRUCK, new Position2D(x, y), playerId);
    supplyTruck.addComponent(truckMovement);
    supplyTruck.addComponent(truckFuel);
    supplyTruck.addComponent(truckSupply);
    supplyTruck.addComponent(healthComponent);
    gameCore.getEntityManager().addEntity(supplyTruck);
  }

  private void createCannon(String entityName, int playerId, int x, int y) {
    Component cannonMovement = new VehicleMovementComponent();
    Component cannonFuel = new FuelComponent(50, 2);
    Component weapon = new CannonComponent(10, CANNON_DAMAGE, 20);
    Component healthComponent = new HealthComponent(50);
    Entity cannon = new Cannon(entityName, EntityType.CANNON, new Position2D(x, y), playerId);
    cannon.addComponent(cannonMovement);
    cannon.addComponent(cannonFuel);
    cannon.addComponent(weapon);
    cannon.addComponent(healthComponent);
    gameCore.getEntityManager().addEntity(cannon);
  }

}

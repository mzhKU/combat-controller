package ch.mzh.cc;

import ch.mzh.cc.components.FuelSystem;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.play.BaseDestroyed;
import ch.mzh.cc.play.GameRule;
import ch.mzh.cc.play.GameSystem;
import ch.mzh.cc.play.GameSystemCommand;
import ch.mzh.cc.rules.CannonMovesNextToSupplyTruck;
import ch.mzh.cc.rules.SupplyAction;
import ch.mzh.cc.rules.SupplyRule;
import ch.mzh.cc.rules.SupplyTruckMovesNextToCannon;
import ch.mzh.cc.rules.VehicleMovesNextToBase;

import java.util.List;
import java.util.Optional;

public class GameRuleEngine implements GameEventListener {
    private final List<SupplyRule> supplyRules;
    private final List<GameRule> gameRules;
    private final EntityManager entityManager;
    private final GameEventManager gameEventManager;
    private final FuelSystem fuelSystem;
    private final GameSystem gameSystem;

    public GameRuleEngine(EntityManager entityManager, FuelSystem fuelSystem, GameSystem gameSystem, GameEventManager gameEventManager) {
        this.supplyRules = List.of(
                new VehicleMovesNextToBase(),
                new SupplyTruckMovesNextToCannon(),
                new CannonMovesNextToSupplyTruck()
        );
        this.gameRules = List.of(
                new BaseDestroyed()
        );
        this.entityManager = entityManager;
        this.gameEventManager = gameEventManager;
        this.fuelSystem = fuelSystem;
        this.gameSystem = gameSystem;
    }

    @Override
    public void onEntityMoved(Entity movedEntity, Position2D oldPos, Position2D endPosition) {
        supplyRules.stream()
                .map(rule -> rule.apply(entityManager, movedEntity, endPosition))
                .flatMap(Optional::stream)
                .forEach(this::executeSupplyAction);
    }

    @Override
    public void onEntityDestroyed(Entity destroyedEntity) {
        gameRules.stream()
                .map(rule -> rule.apply(entityManager, destroyedEntity))
                .flatMap(Optional::stream)
                .forEach(cmd -> cmd.execute(gameSystem, gameEventManager));
    }

    private void executeSupplyAction(SupplyAction action) {
        fuelSystem.transferFuel(action.getRefueler(), action.getRefuelee());
    }

    private void execute(GameSystemCommand command) {
        System.out.println("Base destroyed, game over.");
    }
}
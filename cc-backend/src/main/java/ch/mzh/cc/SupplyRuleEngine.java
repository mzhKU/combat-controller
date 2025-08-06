package ch.mzh.cc;

import ch.mzh.cc.components.FuelSystem;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.rules.CannonMovesNextToSupplyTruck;
import ch.mzh.cc.rules.SupplyAction;
import ch.mzh.cc.rules.SupplyRule;
import ch.mzh.cc.rules.SupplyTruckMovesNextToCannon;
import ch.mzh.cc.rules.VehicleMovesNextToBase;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SupplyRuleEngine implements GameEventListener {
    private final List<SupplyRule> rules;
    private final EntityManager entityManager;
    private final FuelSystem fuelSystem;

    public SupplyRuleEngine(EntityManager entityManager, FuelSystem fuelSystem) {
        this.rules = Arrays.asList(
                new VehicleMovesNextToBase(),
                new SupplyTruckMovesNextToCannon(),
                new CannonMovesNextToSupplyTruck()
        );
        this.entityManager = entityManager;
        this.fuelSystem = fuelSystem;
    }

    @Override
    public void onEntityMoved(Entity movedEntity, Position2D oldPos, Position2D endPosition) {
        rules.stream()
                .map(rule -> rule.apply(entityManager, movedEntity, endPosition))
                .flatMap(Optional::stream)
                .forEach(this::executeSupplyAction);
    }

    private void executeSupplyAction(SupplyAction action) {
        fuelSystem.transferFuel(action.getRefueler(), action.getRefuelee());
    }
}
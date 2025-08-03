package ch.mzh.cc.rules;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;

import java.util.Optional;

import static ch.mzh.cc.Grid.calculateManhattanDistance;


public class SupplyTruckMovesNextToCannon implements SupplyRule {

    @Override
    public Optional<SupplyAction> apply(EntityManager entityManager, Entity movedEntity, Position2D endPosition) {
        Entity cannon = entityManager.getEntity("Cannon 1");
        if (calculateManhattanDistance(cannon.getPosition(), endPosition) == 1 && movedEntity.getType() == EntityType.SUPPLY_TRUCK) {
            return Optional.of(new SupplyAction(entityManager.getEntity("Supply Truck 1"), cannon));
        }
        return Optional.empty();
    }
}

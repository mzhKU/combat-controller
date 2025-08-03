package ch.mzh.cc.rules;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;

import java.util.Optional;

import static ch.mzh.cc.Grid.calculateManhattanDistance;


public class CannonMovesNextToSupplyTruck implements SupplyRule {

    @Override
    public Optional<SupplyAction> apply(EntityManager entityManager, Entity movedEntity, Position2D endPosition) {
        Entity supplyTruck = entityManager.getEntity("Supply Truck 1");
        // TODO: Protect against supply truck being null
        if (calculateManhattanDistance(supplyTruck.getPosition(), endPosition) == 1 && movedEntity.getType() == EntityType.CANNON) {
            return Optional.of(new SupplyAction(entityManager.getEntity("Supply Truck 1"), movedEntity));
        }
        return Optional.empty();
    }
}

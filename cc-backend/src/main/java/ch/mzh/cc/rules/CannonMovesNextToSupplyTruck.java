package ch.mzh.cc.rules;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;

import java.util.Optional;

public class CannonMovesNextToSupplyTruck implements SupplyRule {

    @Override
    public Optional<SupplyAction> apply(EntityManager entityManager, Entity movedEntity, Position2D endPosition) {
        if (movedEntity.getType() != EntityType.CANNON) {
            return Optional.empty();
        }

        // Only find supply trucks owned by the same player
        return entityManager.findEntityByTypeInRange(
                        endPosition, 1, EntityType.SUPPLY_TRUCK, movedEntity.getPlayerId())
                .map(supplyTruck -> new SupplyAction(supplyTruck, movedEntity));
    }
}
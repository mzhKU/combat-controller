package ch.mzh.cc.rules;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;

import java.util.Optional;


public class SupplyTruckMovesNextToCannon implements SupplyRule {
    @Override
    public Optional<SupplyAction> apply(EntityManager entityManager, Entity movedEntity, Position2D endPosition) {
        if (movedEntity.getType() != EntityType.SUPPLY_TRUCK) {
            return Optional.empty();
        }

        // Only find cannons owned by the same player
        return entityManager.findEntityByTypeInRange(
                        endPosition, 1, EntityType.CANNON, movedEntity.getPlayerId())
                .map(cannon -> new SupplyAction(movedEntity, cannon));
    }
}

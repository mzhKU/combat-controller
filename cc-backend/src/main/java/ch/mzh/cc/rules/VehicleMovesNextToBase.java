package ch.mzh.cc.rules;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;
import ch.mzh.cc.play.GameState;

import java.util.Optional;
import java.util.Set;

import static ch.mzh.cc.Grid.calculateManhattanDistance;

public class VehicleMovesNextToBase implements SupplyRule {
    private static final Set<EntityType> REFUELABLE_TYPES = Set.of(EntityType.CANNON, EntityType.SUPPLY_TRUCK);

    @Override
    public Optional<SupplyAction> apply(EntityManager entityManager, Entity movedEntity, Position2D endPosition) {
        if (!REFUELABLE_TYPES.contains(movedEntity.getType())) {
            return Optional.empty();
        }

        // Only find bases owned by the same player (or neutral bases)
        return entityManager.getEntitiesInRange(endPosition, 1).stream()
                .filter(entity -> entity.getType() == EntityType.BASE)
                .filter(base -> base.isOwnedBy(movedEntity.getPlayerId()) || base.isNeutral())
                .findFirst()
                .map(base -> new SupplyAction(base, movedEntity));
    }
}
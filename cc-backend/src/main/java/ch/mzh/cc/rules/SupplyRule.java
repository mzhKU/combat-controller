package ch.mzh.cc.rules;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;

import java.util.Optional;

public interface SupplyRule {

    Optional<SupplyAction> apply(EntityManager entityManager, Entity movedEntity, Position2D endPosition);

}

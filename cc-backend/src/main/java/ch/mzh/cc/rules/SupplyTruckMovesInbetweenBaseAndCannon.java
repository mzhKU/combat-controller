package ch.mzh.cc.rules;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;

import java.util.Optional;

public class SupplyTruckMovesInbetweenBaseAndCannon implements SupplyRule {

  // The truck first supplies the cannon and is then resupplied by the base
  // Result: both the cannon and the supply truck are full

  @Override
  public Optional<SupplyAction> apply(EntityManager entityManager, Entity movedEntity, Position2D endPosition) {
    return Optional.empty();
  }
}

package ch.mzh.cc.rules;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;

import java.util.Optional;

public class CannonMovesNextToSupplyTruckWhichIsNextToBase implements SupplyRule {

  // When a cannon moves next to a supply truck which in turn is next to a base,
  // the truck resupplies the cannon and is immediately also resupplied by the base.
  // Both units will be full at the end.

  @Override
  public Optional<SupplyAction> apply(EntityManager entityManager, Entity movedEntity, Position2D endPosition) {
    return Optional.empty();
  }
}

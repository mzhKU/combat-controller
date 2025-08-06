package ch.mzh.cc.components;

import ch.mzh.cc.GameEventListener;
import ch.mzh.cc.model.Entity;

public class FuelSystem implements GameEventListener {
  public void transferFuel(Entity supplier, Entity receiver) {
    SupplyComponent supplyFuel = supplier.getComponent(SupplyComponent.class);
    FuelComponent targetFuel = receiver.getComponent(FuelComponent.class);

    // supplyFuel should be != null here...
    if (supplyFuel == null || targetFuel == null) {
      return;
    }

    // TODO: Test if FuelSystem always calls canRefuel before it calls refuelTarget.
    // TODO: Test if FuelSystem always checks supplyFuel != null and targetFuel != null
    // supplyFuel.canRefuel was also already called above...
    if (!supplyFuel.canRefuel(supplier, receiver)) return;
    boolean success = supplyFuel.refuelTarget(supplier, receiver);
    if (success) {
      System.out.println(supplier.getName() + " refueled " + receiver.getName() +
              " (Current: " + targetFuel.getCurrentFuel() + "/" + targetFuel.getMaxFuel() + ")");
    } else {
      System.out.println("REFUEL FAILED.");
    }
  }
}
